import React, { Component } from 'react';
import { withRouter } from 'react-router-dom';
import {
  Button, Select, Radio, Form, Input, Popover, Icon, Spin, DatePicker,
} from 'choerodon-ui';
import moment from 'moment';
import YAML from 'yamljs';
import { observer } from 'mobx-react';
import { FormattedMessage, injectIntl } from 'react-intl';
import CreateAutoTestStore from '../../../stores/CreateAutoTestStore';
import { YamlEditor } from '../../../../../components';
import { commonLink } from '../../../../../common/utils';
import {
  getYaml, runTestTiming, runTestInstant,
} from '../../../../../api/AutoTestApi';
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
@injectIntl
@Form.create()
@observer
class ConfirmInfo extends Component {
  state = {
    triggerType: 'easy',
    testType: 'instant',
    data: null,
  }

  // 创建任务切换触发类型
  changeValue(e) {
    const { resetFields } = this.props.form;
    resetFields(['simpleRepeatInterval', 'simpleRepeatCount', 'simpleRepeatIntervalUnit', 'cronExpression']);
    this.setState({
      triggerType: e.target.value === 'simple-trigger' ? 'easy' : 'cron',
    });
  }

  disabledEndDate = (endTime) => {
    const { startTime } = this.state;
    if (!endTime || !startTime) {
      return false;
    }
    return endTime.valueOf() <= startTime.valueOf();
  }

  range = (start, end) => {
    const result = [];
    for (let i = start; i < end; i += 1) {
      result.push(i);
    }
    return result;
  }

  disabledDateStartTime = (date) => {
    this.startTimes = date;
    if (date && this.endTimes && this.endTimes.day() === date.day()) {
      if (this.endTimes.hour() === date.hour() && this.endTimes.minute() === date.minute()) {
        return {
          disabledHours: () => this.range(this.endTimes.hour() + 1, 24),
          disabledMinutes: () => this.range(this.endTimes.minute() + 1, 60),
          disabledSeconds: () => this.range(this.endTimes.second(), 60),
        };
      } if (this.endTimes.hour() === date.hour()) {
        return {
          disabledHours: () => this.range(this.endTimes.hour() + 1, 24),
          disabledMinutes: () => this.range(this.endTimes.minute() + 1, 60),
        };
      }
      return {
        disabledHours: () => this.range(this.endTimes.hour() + 1, 24),
      };
    }
    return '';
  }

  clearStartTimes = (status) => {
    if (!status) {
      this.endTimes = null;
    }
  }

  clearEndTimes = (status) => {
    if (!status) {
      this.startTimes = null;
    }
  }

  disabledDateEndTime = (date) => {
    this.endTimes = date;
    if (date && this.startTimes && this.startTimes.day() === date.day()) {
      if (this.startTimes.hour() === date.hour() && this.startTimes.minute() === date.minute()) {
        return {
          disabledHours: () => this.range(0, this.startTimes.hour()),
          disabledMinutes: () => this.range(0, this.startTimes.minute()),
          disabledSeconds: () => this.range(0, this.startTimes.second() + 1),
        };
      } if (this.startTimes.hour() === date.hour()) {
        return {
          disabledHours: () => this.range(0, this.startTimes.hour()),
          disabledMinutes: () => this.range(0, this.startTimes.minute()),
        };
      }
      return {
        disabledHours: () => this.range(0, this.startTimes.hour()),
      };
    }
    return '';
  }

  onStartChange = (value) => {
    this.onChange('startTime', value);
  }

  onEndChange = (value) => {
    this.onChange('endTime', value);
  }

  onChange = (field, value) => {
    const { setFieldsValue } = this.props.form;
    this.setState({
      [field]: value,
    }, () => {
      setFieldsValue({ [field]: this.state[field] });
    });
  }

  /**
   * 部署应用
   */
  handleDeploy = () => {
    // this.setState({
    //   loading: true,
    // });
    // const instances = CreateAutoTestStore.currentInstance;
    // const value = this.state.value || CreateAutoTestStore.value.yaml;
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
    const { testType } = this.state;

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

  getCronContent = () => {
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
  }

  renderTimingExecute = () => {
    const { triggerType } = this.state;
    const { intl, AppState } = this.props;
    const { getFieldDecorator } = this.props.form;
    return (
      <div className="c7ntest-create-task">
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
              disabledTime={this.disabledDateStartTime}
              showTime={{ defaultValue: moment('00:00:00', 'HH:mm:ss') }}
              // getCalendarContainer={() => document.getElementsByClassName('c7n-modal-body')[document.getElementsByClassName('c7n-modal-body').length - 1]}
              onChange={this.onStartChange}
              onOpenChange={this.clearStartTimes}
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
              disabledDate={this.disabledEndDate.bind(this)}
              disabledTime={this.disabledDateEndTime.bind(this)}
              showTime={{ defaultValue: moment() }}
              // getCalendarContainer={() => document.getElementsByClassName('c7n-modal-body')[document.getElementsByClassName('c7n-modal-body').length - 1]}
              onChange={this.onEndChange}
              onOpenChange={this.clearEndTimes}
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
          >
            <Icon
              onClick={this.checkCron}
              style={{ display: triggerType === 'cron' ? 'inline-block' : 'none' }}
              className="c7ntest-task-detail-popover-icon"
              type="find_in_page-o"
            />
          </Popover>
        </div>
      </div>
    );
  }

  toTestHistory = () => {
    this.props.history.push(commonLink('/AutoTest/list'));
    CreateAutoTestStore.clearTestInfo();
  }

  render() {
    const {
      app, appVersion, version,
    } = CreateAutoTestStore;
    const { intl, saveRef } = this.props;
    saveRef(this);
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
      <section className="deployApp-review">
        <RadioGroup
          value={testType}
          style={{ marginBottom: 20 }}
          onChange={(e) => {
            this.setState({
              testType: e.target.value,
            });
          }}
        >
          <Radio style={radioStyle} value="instant">立即执行</Radio>
          <Radio style={radioStyle} value="timing">定时执行</Radio>
        </RadioGroup>
        {/* 定时执行 */}
        {testType === 'timing' && this.renderTimingExecute()}
        <div style={{ border: '1px solid var(--divider)', borderRadius: '4px' }}>
          <div style={{
            height: 48, borderBottom: '1px solid var(--divider)', lineHeight: '48px', fontSize: '14px', fontWeight: 500, paddingLeft: 16,
          }}
          >
            确认信息
          </div>
          <div style={{ padding: 16 }}>
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
            {/* <div>
              <div className="deployApp-title">
                <span className="deployApp-title-text">
                  目标版本：
                </span>
              </div>
              <div className="deployApp-text">
                {version && version.versionName}
              </div>
            </div> */}
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
  }
}

export default withRouter(ConfirmInfo);
