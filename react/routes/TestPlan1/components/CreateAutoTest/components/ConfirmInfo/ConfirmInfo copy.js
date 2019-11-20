import React, { Component, useState } from 'react';
import { withRouter } from 'react-router-dom';
import {
  Button, Select, Radio, Form, Input, Popover, Icon, Spin, DatePicker,
} from 'choerodon-ui';
import moment from 'moment';
import YAML from 'yamljs';
import { observer } from 'mobx-react';
import { FormattedMessage, injectIntl } from 'react-intl';
import CreateAutoTestStore from '../../../../stores/CreateAutoTestStore';
import { YamlEditor } from '../../../../../../components';
import { commonLink } from '../../../../../../common/utils';
import {
  getYaml, runTestTiming, runTestInstant,
} from '../../../../../../api/AutoTestApi';
import './ConfirmInfo.less';

const intlPrefix = 'taskdetail';
const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const { Option } = Select;
const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 8 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 16 },
  },
};

export default withRouter(Form.create(injectIntl(observer((props) => {
  const [triggerType, setTriggerType] = useState('easy');
  const [testType, setTestType] = useState('instant');
  const [data, setData] = useState(null);
  const [startTimes, setStartTimes] = useState();
  const [endTimes, setEndTimes] = useState();


  // 创建任务切换触发类型
  const changeValue = (e) => {
    const { resetFields } = props.form;
    resetFields(['simpleRepeatInterval', 'simpleRepeatCount', 'simpleRepeatIntervalUnit', 'cronExpression']);
    setTriggerType(e.target.value === 'simple-trigger' ? 'easy' : 'cron');
  };

  const disabledEndDate = (endTime) => {
    if (!endTime || !startTime) {
      return false;
    }
    return endTime.valueOf() <= startTime.valueOf();
  };

  const range = (start, end) => {
    const result = [];
    for (let i = start; i < end; i += 1) {
      result.push(i);
    }
    return result;
  };

  const disabledDateStartTime = (date) => {
    setStartTime(date);
    if (date && endTimes && endTimes.day() === date.day()) {
      if (endTimes.hour() === date.hour() && endTimes.minute() === date.minute()) {
        return {
          disabledHours: () => range(endTimes.hour() + 1, 24),
          disabledMinutes: () => range(endTimes.minute() + 1, 60),
          disabledSeconds: () => range(endTimes.second(), 60),
        };
      } else if (endTimes.hour() === date.hour()) {
        return {
          disabledHours: () => range(endTimes.hour() + 1, 24),
          disabledMinutes: () => range(endTimes.minute() + 1, 60),
        };
      } else {
        return {
          disabledHours: () => range(endTimes.hour() + 1, 24),
        };
      }
    }
    return '';
  };

  const clearStartTimes = (status) => {
    if (!status) {
      setEndTimes(null);
    }
  };

  const clearEndTimes = (status) => {
    if (!status) {
      setStartTimes(null);
    }
  };

  const disabledDateEndTime = (date) => {
    setEndTimes(date);
    if (date && startTimes && startTimes.day() === date.day()) {
      if (startTimes.hour() === date.hour() && startTimes.minute() === date.minute()) {
        return {
          disabledHours: () => range(0, startTimes.hour()),
          disabledMinutes: () => range(0, startTimes.minute()),
          disabledSeconds: () => range(0, startTimes.second() + 1),
        };
      } else if (startTimes.hour() === date.hour()) {
        return {
          disabledHours: () => range(0, startTimes.hour()),
          disabledMinutes: () => range(0, startTimes.minute()),
        };
      } else {
        return {
          disabledHours: () => range(0, startTimes.hour()),
        };
      }
    }
    return '';
  };

  const onChange = (field, value) => {
    const { setFieldsValue } = props.form;
    this.setState({
      [field]: value,
    }, () => {
      setFieldsValue({ [field]: this.state[field] });
    });
  };

  const onStartChange = (value) => {
    onChange('startTime', value);
  };

  const onEndChange = (value) => {
    onChange('endTime', value);
  };
 

  /**
   * 部署应用
   */
  const handleDeploy = () => {
    let isNotChange = true;
    const oldYaml = CreateAutoTestStore.getConfigValue.yaml;
    const newYaml = CreateAutoTestStore.getNewConfigValue.yaml;
    const oldvalue = YAML.parse(oldYaml);
    const newvalue = YAML.parse(newYaml);
    // console.log(oldvalue);    
    if (JSON.stringify(oldvalue) !== JSON.stringify(newvalue)) {
      isNotChange = false;
    }
    const {
      app, appVersion, env, version,
    } = CreateAutoTestStore;
    const applicationDeployDTO = {
      isNotChange,
      appId: app.id,
      code: app.code,
      appVersionId: appVersion.id || 5,
      environmentId: env.id || 144,
      projectVersionId: version.versionId,
      values: newYaml,
      type: this.state.mode === 'new' ? 'create' : 'update',
      // appInstanceId: this.state.mode === 'new'
      //   ? null : this.state.instanceId || (instances && instances.length === 1 && instances[0].id),
    };

    if (testType === 'instant') {
      CreateAutoTestStore.setLoading(true);
      // 立即执行
      runTestInstant(applicationDeployDTO).then((res) => {
        CreateAutoTestStore.setLoading(false);
        this.toTestHistory();
      }).catch((err) => {
        CreateAutoTestStore.setLoading(false);
      });
    } else {
      // 定时执行
      this.props.form.validateFieldsAndScroll((err, values) => {
        if (!err) {
          CreateAutoTestStore.setLoading(true);
          const flag = values.triggerType === 'simple-trigger';
          const {
            startTime, endTime, cronExpression, simpleRepeatInterval,
            simpleRepeatIntervalUnit, simpleRepeatCount,
          } = values;
          const scheduleTaskDTO = {
            ...values,
            startTime: startTime.format('YYYY-MM-DD HH:mm:ss'),
            endTime: endTime ? endTime.format('YYYY-MM-DD HH:mm:ss') : null,
            cronExpression: flag ? null : cronExpression,
            simpleRepeatInterval: flag ? Number(simpleRepeatInterval) : null,
            simpleRepeatIntervalUnit: flag ? simpleRepeatIntervalUnit : null,
            simpleRepeatCount: flag ? Number(simpleRepeatCount) : null,
            params: {
              deploy: applicationDeployDTO,
            },
          };
          runTestTiming(scheduleTaskDTO).then((res) => {
            CreateAutoTestStore.setLoading(false);
            this.toTestHistory();
          });
        } else {
          /* console.log(err); */
        }
      });
    }
  };

  const getCronContent = () => {
    const { cronLoading, cronTime } = this.state;
    const { intl } = this.props;
    let content;
    if (cronLoading === 'empty') {
      content = (
        <div className="c7ntest-task-deatil-cron-container-empty">
          <FormattedMessage id={`${intlPrefix}.cron.tip`} />
          <a href={intl.formatMessage({ id: `${intlPrefix}.cron.tip.link` })} rel="noopener noreferrer">
            <span>{intl.formatMessage({ id: 'learnmore' })}</span>
            <Icon type="open_in_new" style={{ fontSize: '13px' }} />
          </a>
        </div>
      );
    } else if (cronLoading === true) {
      content = (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
          <Spin />
        </div>
      );
    } else if (cronLoading === 'right') {
      content = (
        <div className="c7ntest-task-deatil-cron-container">
          <FormattedMessage id={`${intlPrefix}.cron.example`} />
          {
            cronTime.map((value, key) => (
              <li>
                <FormattedMessage id={`${intlPrefix}.cron.runtime`} values={{ time: key + 1 }} />
                <span>{value}</span>
              </li>
            ))
          }
        </div>
      );
    } else {
      content = (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
          <FormattedMessage id={`${intlPrefix}.cron.wrong`} />
        </div>
      );
    }
    return content;
  };

  const renderTimingExecute = () => {
    const { intl, AppState } = props;
    const { getFieldDecorator } = props.form;
    return (
      <div className="c7ntest-create-task" style={{ paddingLeft: '0.36rem' }}>
        <FormItem
          {...formItemLayout}
          className="c7ntest-create-task-inline-formitem"
          style={{ display: 'block', width: 512 }}
        >
          {getFieldDecorator('startTime', {
            rules: [{
              required: true,
              message: intl.formatMessage({ id: `${intlPrefix}.task.start.time.required` }),
            }],
          })(
            <DatePicker
              label="开始时间"
              style={{ width: '100%' }}
              format="YYYY-MM-DD HH:mm:ss"
              disabledDate={this.disabledStartDate}
              disabledTime={disabledDateStartTime}
              showTime={{ defaultValue: moment('00:00:00', 'HH:mm:ss') }}
              // getCalendarContainer={() => document.getElementsByClassName('c7n-modal-body')[document.getElementsByClassName('c7n-modal-body').length - 1]}
              onChange={onStartChange}
              onOpenChange={clearStartTimes}
            />,
          )}
        </FormItem>
        <FormItem
          {...formItemLayout}
          className="c7ntest-create-task-inline-formitem"
          style={{ display: 'block', width: 512 }}
        >
          {getFieldDecorator('endTime', {
            rules: [],
          })(
            <DatePicker
              label="结束时间"
              format="YYYY-MM-DD HH:mm:ss"
              style={{ width: '100%' }}
              disabledDate={disabledEndDate.bind(this)}
              disabledTime={disabledDateEndTime.bind(this)}
              showTime={{ defaultValue: moment() }}
              // getCalendarContainer={() => document.getElementsByClassName('c7n-modal-body')[document.getElementsByClassName('c7n-modal-body').length - 1]}
              onChange={onEndChange}
              onOpenChange={clearEndTimes}
            />,
          )}
        </FormItem>
        <FormItem
          {...formItemLayout}
          style={{ marginBottom: '0.1rem' }}
        >
          {getFieldDecorator('triggerType', {
            initialValue: 'simple-trigger',
          })(
            <RadioGroup
              className="c7ntest-create-task-radio-container"
              label="触发类型"
              onChange={this.changeValue.bind(this)}
            >
              <Radio value="simple-trigger">简单任务</Radio>
              <Radio value="cron-trigger">Cron任务</Radio>
            </RadioGroup>,
          )}
        </FormItem>
        <div style={{ display: triggerType === 'easy' ? 'block' : 'none' }}>
          <FormItem
            {...formItemLayout}
            className="c7ntest-create-task-inline-formitem"
          >
            {getFieldDecorator('simpleRepeatInterval', {
              rules: [{
                required: triggerType === 'easy',
                message: intl.formatMessage({ id: `${intlPrefix}.repeat.required` }),
              }, {
                pattern: /^[1-9]\d*$/,
                message: intl.formatMessage({ id: `${intlPrefix}.repeat.pattern` }),
              }],
              validateFirst: true,
            })(
              <Input style={{ width: '164px' }} autoComplete="off" label="重复间隔" />,
            )}
          </FormItem>
          <FormItem
            {...formItemLayout}
            className="c7ntest-create-task-inline-formitem c7ntest-create-task-inline-formitem-select"
          >
            {getFieldDecorator('simpleRepeatIntervalUnit', {
              rules: [],
              initialValue: 'SECONDS',
            })(
              <Select
                style={{ width: '164px' }}
                label="时间单位"
              >
                <Option value="SECONDS">秒</Option>
                <Option value="MINUTES">分</Option>
                <Option value="HOURS">时</Option>
                <Option value="DAYS">天</Option>
              </Select>,
            )}
          </FormItem>

          <FormItem
            className="c7ntest-create-task-inline-formitem"
            {...formItemLayout}
          >
            {getFieldDecorator('simpleRepeatCount', {
              rules: [{
                required: triggerType === 'easy',
                message: intl.formatMessage({ id: `${intlPrefix}.repeat.time.required` }),
              }, {
                pattern: /^[1-9]\d*$/,
                message: intl.formatMessage({ id: `${intlPrefix}.repeat.pattern` }),
              }],
            })(
              <Input style={{ width: '164px' }} autoComplete="off" label="执行次数" />,
            )}
          </FormItem>
        </div>
        <div>
          <FormItem
            {...formItemLayout}
            style={{ display: triggerType === 'cron' ? 'inline-block' : 'none' }}
            className="c7ntest-create-task-inline-formitem"
          >
            {getFieldDecorator('cronExpression', {
              rules: [{
                required: triggerType === 'cron',
                message: intl.formatMessage({ id: `${intlPrefix}.cron.expression.required` }),
              }],
            })(
              <Input style={{ width: 512 }} autoComplete="off" label="Cron表达式" />,
            )}
          </FormItem>
          <Popover
            content={this.getCronContent()}
            trigger="click"
            placement="bottom"
            overlayClassName="c7ntest-task-detail-popover"
          // getPopupContainer={() => document.getElementsByClassName('sidebar-content')[0].parentNode}
          >
            <Icon
              onClick={this.checkCron}
              style={{ display: triggerType === 'cron' ? 'inline-block' : 'none' }}
              className="c7ntest-task-detail-popover-icon"
              type="find_in_page"
            />
          </Popover>
        </div>
      </div>
    );
  };

  const toTestHistory = () => {
    this.props.history.push(commonLink('/AutoTest/list'));
    CreateAutoTestStore.clearTestInfo();
  };

  const {
    app, appVersion,
  } = CreateAutoTestStore;
  const { intl, confirmInfoRef } = this.props;
  const { formatMessage } = intl;
  const data = CreateAutoTestStore.getNewConfigValue;
  const {
    testType,
  } = this.state;
  const options = {
    theme: 'neat',
    mode: 'yaml',
    readOnly: true,
    lineNumbers: true,
  };
  const radioStyle = {
    display: 'block',
    height: '30px',
    lineHeight: '30px',
  };
  return (
    <section className="deployApp-review" ref={confirmInfoRef}>
      <RadioGroup
        value={testType}
        style={{ marginBottom: 20, paddingLeft: 36 }}
        onChange={(e) => {
          setTestType(e.target.value);
        }}
      >
        <Radio style={radioStyle} value="instant">立即执行</Radio>
        <Radio style={radioStyle} value="timing">定时执行</Radio>
      </RadioGroup>
      {/* 定时执行 */}
      {testType === 'timing' && renderTimingExecute()}
      <div>
        <div style={{
          borderTop: '1px solid #D8D8D8', fontSize: '16px', fontWeight: 500, paddingLeft: 36, marginTop: 20,
        }}
        >
          确认信息
        </div>
        <div style={{ padding: 16, paddingLeft: 36 }}>
          <div>
            <div className="deployApp-title">
              <span className="deployApp-title-text">
                应用名称：
              </span>
            </div>
            <div className="deployApp-text">
              {app && app.name}
              <span className="deployApp-value">
                {app && `(${app.code})`}
              </span>
            </div>
          </div>
          <div>
            <div className="deployApp-title">
              <span className="deployApp-title-text">
                应用版本：
              </span>
            </div>
            <div className="deployApp-text">
              {appVersion && appVersion.version}
            </div>
          </div>
          <div>
            <div className="deployApp-title">
              {/* <Icon type="description" /> */}
              <span className="deployApp-title-text">
                {formatMessage({ id: 'autoteststep_two_config' })}
                {'：'}
              </span>
            </div>
          </div>
          {data && (
          <div>
            <YamlEditor
              options={options}
              newLines={data.newLines}
              readOnly
              value={data.yaml}
              highlightMarkers={data.highlightMarkers}
            />
          </div>
          )}
        </div>
      </div>
    </section>
  );
}))));
