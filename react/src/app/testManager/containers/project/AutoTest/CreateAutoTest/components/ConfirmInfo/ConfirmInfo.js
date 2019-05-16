import React, { Component } from 'react';
import { withRouter } from 'react-router-dom';
import {
  Button, Select, Radio, Form, Input, Popover, Icon, Spin, DatePicker,
} from 'choerodon-ui';
import moment from 'moment';
import YAML from 'yamljs';
import { observer } from 'mobx-react';
import { FormattedMessage, injectIntl } from 'react-intl';
import CreateAutoTestStore from '../../../../../../store/project/AutoTest/CreateAutoTestStore';
import { YamlEditor } from '../../../../../../components/CommonComponent';
import { commonLink } from '../../../../../../common/utils';
import {
  getYaml, runTestTiming, runTestInstant, 
} from '../../../../../../api/AutoTestApi';
import './ConfirmInfo.scss';

const intlPrefix = 'taskdetail';
const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;
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
    // selectType:''
    triggerType: 'easy',
    testType: 'instant',
    data: null,
  }

  componentDidMount() {
    // this.loadYaml();
  }

  // loadYaml=() => {
  //   getYaml().then((data) => {
  //     if (data) {
  //       this.setState({
  //         data,
  //       });
  //     }
  //   });
  // }
  
  // 创建任务切换触发类型
  changeValue(e) {
    const { resetFields } = this.props.form;
    resetFields(['simpleRepeatInterval', 'simpleRepeatCount', 'simpleRepeatIntervalUnit', 'cronExpression']);
    this.setState({
      triggerType: e.target.value === 'simple-trigger' ? 'easy' : 'cron',
    });
  }

  disabledEndDate = (endTime) => {
    const startTime = this.state.startTime;
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
      } else if (this.endTimes.hour() === date.hour()) {
        return {
          disabledHours: () => this.range(this.endTimes.hour() + 1, 24),
          disabledMinutes: () => this.range(this.endTimes.minute() + 1, 60),
        };
      } else {
        return {
          disabledHours: () => this.range(this.endTimes.hour() + 1, 24),
        };
      }
    }
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
      } else if (this.startTimes.hour() === date.hour()) {
        return {
          disabledHours: () => this.range(0, this.startTimes.hour()),
          disabledMinutes: () => this.range(0, this.startTimes.minute()),
        };
      } else {
        return {
          disabledHours: () => this.range(0, this.startTimes.hour()),
        };
      }
    }
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
      this.setState({
        loading: true,
      });
      // 立即执行
      runTestInstant(applicationDeployDTO).then((res) => {
        this.setState({
          loading: false,
        });
        this.toTestHistory();
      }).catch((err) => {
        this.setState({
          loading: false,
        });
      });
    } else {
      // 定时执行
      this.props.form.validateFieldsAndScroll((err, values) => {
        if (!err) {
          this.setState({
            loading: true,
          });
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
            this.setState({
              loading: false,
            });
            this.toTestHistory();
          });
        } else {
          console.log(err);
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
          <a href={intl.formatMessage({ id: `${intlPrefix}.cron.tip.link` })} target="_blank">
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
        >
          {getFieldDecorator('startTime', {
            rules: [{
              required: true,
              message: intl.formatMessage({ id: `${intlPrefix}.task.start.time.required` }),
            }],
          })(
            <DatePicker
              label="开始时间"
              style={{ width: '248px' }}
              format="YYYY-MM-DD HH:mm:ss"
              disabledDate={this.disabledStartDate}
              disabledTime={this.disabledDateStartTime}
              showTime={{ defaultValue: moment('00:00:00', 'HH:mm:ss') }}
              // getCalendarContainer={() => document.getElementsByClassName('ant-modal-body')[document.getElementsByClassName('ant-modal-body').length - 1]}
              onChange={this.onStartChange}
              onOpenChange={this.clearStartTimes}
            />,
          )}
        </FormItem>
        <FormItem
          {...formItemLayout}
          className="c7ntest-create-task-inline-formitem"
        >
          {getFieldDecorator('endTime', {
            rules: [],
          })(
            <DatePicker
              label="结束时间"
              style={{ width: '248px' }}
              format="YYYY-MM-DD HH:mm:ss"
              disabledDate={this.disabledEndDate.bind(this)}
              disabledTime={this.disabledDateEndTime.bind(this)}
              showTime={{ defaultValue: moment() }}
              // getCalendarContainer={() => document.getElementsByClassName('ant-modal-body')[document.getElementsByClassName('ant-modal-body').length - 1]}
              onChange={this.onEndChange}
              onOpenChange={this.clearEndTimes}
            />,
          )}
        </FormItem>
        <FormItem
          {...formItemLayout}
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
              <Input style={{ width: '100px' }} autoComplete="off" label="重复间隔" />,
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
                style={{ width: '124px' }}
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
              <Input style={{ width: '100px' }} autoComplete="off" label="执行次数" />,
            )}
          </FormItem>
        </div>
        <div>
          <FormItem
            {...formItemLayout}
            style={{ display: triggerType === 'cron' ? 'inline-block' : 'none' }}
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
  }

  toTestHistory = () => {
    this.props.history.push(commonLink('/AutoTest/list'));
    CreateAutoTestStore.clearTestInfo();
  }

  render() {
    const {
      app, appVersion, version, env, 
    } = CreateAutoTestStore;
    const { intl } = this.props;
    const { formatMessage } = intl;
    const data = this.state.data || CreateAutoTestStore.getNewConfigValue;
    const {
      testType, loading,
    } = this.state;
    const options = {
      theme: 'neat',
      mode: 'yaml',
      readOnly: true,
      lineNumbers: true,
    };
    return (
      <section className="deployApp-review">
        <p>
          {formatMessage({ id: 'autoteststep_three_description' })}
        </p>
        <section className="deployApp-section">
          <div>
            <div className="deployApp-title">
              <span className="deployApp-title-text">
                {'测试类型：'}
              </span>
            </div>
            <div className="deployApp-text">
              <RadioGroup
                value={testType}
                onChange={(e) => {
                  this.setState({
                    testType: e.target.value,
                  });
                }}
              >
                <Radio value="instant">立即执行</Radio>
                <Radio value="timing">定时执行</Radio>
              </RadioGroup>
            </div>
          </div>
          {/* 定时执行 */}
          {testType === 'timing' && this.renderTimingExecute()}
          {/* <div>
            <div className="deployApp-title">
              <span className="deployApp-title-text">
                {'测试框架：'}
              </span>
            </div>
            <div className="deployApp-text">
              {app && app.name}
              <span className="deployApp-value">
                {'('}
                {app && app.code}
                {')'}
              </span>
            </div>
          </div> */}
          <div>
            <div className="deployApp-title">
              <span className="deployApp-title-text">
                {'应用名称：'}
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
                {'应用版本：'}
              </span>
            </div>
            <div className="deployApp-text">
              {appVersion && appVersion.version}              
            </div>
          </div>
          <div>
            <div className="deployApp-title">
              <span className="deployApp-title-text">
                {'目标版本：'}
              </span>
            </div>
            <div className="deployApp-text">
              {version && version.versionName}              
            </div>
          </div>
          {/* <div>
            <div className="deployApp-title">
              <Icon type="version" />
              <span className="deployApp-title-text">
                {formatMessage({ id: 'autoteststep_three_version' })}
                {'：'}
              </span>
            </div>
            <div className="deployApp-text">{this.state.versionDto && this.state.versionDto.version}</div>
          </div>
          <div>
            <div className="deployApp-title">
              <Icon type="donut_large" />
              <span className="deployApp-title-text">
                {formatMessage({ id: 'autoteststep_one_env_title' })}
                {'：'}
              </span>
            </div>
            <div className="deployApp-text">
              {this.state.envDto && this.state.envDto.name}
              <span className="deployApp-value">
                {'('}
                {this.state.envDto && this.state.envDto.code}
                {')'}
              </span>
            </div>
          </div>
          */}
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
              {<YamlEditor
                options={options}
                newLines={data.newLines}
                readOnly={this.state.current === 3}
                value={data.yaml}
                highlightMarkers={data.highlightMarkers}
              />}
            </div>
          )}
        </section>
        <section className="deployApp-section">
          <Button type="primary" funcType="raised" onClick={this.handleDeploy} loading={loading}>{formatMessage({ id: 'autotestbtn_autotest' })}</Button>
          <Button funcType="raised" onClick={CreateAutoTestStore.preStep}>{formatMessage({ id: 'previous' })}</Button>
          <Button funcType="raised" className="c7ntest-autotest-clear" onClick={CreateAutoTestStore.clearTestInfo}>{formatMessage({ id: 'cancel' })}</Button>
        </section>
      </section>
    );
  }
}

export default withRouter(ConfirmInfo);
