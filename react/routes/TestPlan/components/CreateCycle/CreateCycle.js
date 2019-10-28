import React, { Component } from 'react';
import { Choerodon } from '@choerodon/boot';
import {
  Form, Input, Select, Modal, Spin, DatePicker,
} from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import { getProjectVersion } from '../../../../api/agileApi';
import { addCycle, checkCycleName } from '../../../../api/cycleApi';
import TestPlanStore from '../../stores/TestPlanStore';
import './CreateCycle.less';

const { Option } = Select;
const FormItem = Form.Item;
const { Sidebar } = Modal;
const { RangePicker } = DatePicker;
class CreateCycle extends Component {
  state = {
    versions: [],
    loading: false,
  }

  componentWillReceiveProps(nextProps) {
    const { form: { resetFields }, visible } = this.props;
    if (visible === false && nextProps.visible === true) {
      resetFields();
    }
  }

  componentDidMount() {
    this.getProjectVersion();
  }

  onOk = () => {
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        const { range } = values;
        const [fromDate, toDate] = range || [null, null];
        // window.console.log('Received values of form: ', values);
        this.setState({ loading: true });
        addCycle({
          ...values,
          ...{
            type: 'cycle',
            fromDate: fromDate ? fromDate.startOf('day').format('YYYY-MM-DD HH:mm:ss') : null,
            toDate: toDate ? toDate.endOf('day').format('YYYY-MM-DD HH:mm:ss') : null,
          },
        }).then((res) => {
          if (res.failed) {
            Choerodon.prompt('同名循环已存在');
          } else {
            this.props.onOk();
          }
          this.setState({ loading: false });
        }).catch(() => {
          Choerodon.prompt('网络异常');
          this.setState({ loading: false });
        });
      }
    });
  }

  getProjectVersion = () => {
    getProjectVersion().then((versions) => {
      this.setState({
        versions,
      });
    });
  }

  validateName = async (rule, name, callback) => {
    const { form: { getFieldValue } } = this.props;
    const versionId = getFieldValue('versionId');
    if (!versionId) {
      callback();
      return;
    }
    const hasSame = await checkCycleName({
      type: 'cycle',
      cycleName: name,
      versionId,
      parentCycleId: 0,
    });
    if (hasSame) {
      callback('含有同名循环');
    } else {
      callback();
    }
  }

  handleVersionChange = () => {   
    const { form: { validateFields } } = this.props;
    setTimeout(() => {
      validateFields(['cycleName'], { force: true });
    });
  }

  render() {
    const {
      visible, onCancel, form: { getFieldDecorator },
    } = this.props;
    const { versions, loading } = this.state;
    const options = versions.map(version => (
      <Option value={version.versionId} key={version.versionId}>
        {version.name}
      </Option>
    ));
    return (
      <div>
        <Sidebar
          title={<FormattedMessage id="cycle_create" />}
          visible={visible}
          onOk={this.onOk}
          onCancel={onCancel}
          width={380}
        >
          <Spin spinning={loading}>
            <Form>
              <FormItem>
                {getFieldDecorator('versionId', {
                  rules: [{
                    required: true, message: '请选择版本!',
                  }],
                  initialValue: versions && versions.length > 0 && TestPlanStore.exportVersionId,
                })(
                  <Select
                    onChange={this.handleVersionChange}
                    label={<FormattedMessage id="version" />}
                  >
                    {options}
                  </Select>,
                )}
              </FormItem>
              <FormItem>
                {getFieldDecorator('cycleName', {
                  rules: [{
                    required: true, message: '请输入名称!',
                  }, {
                    validator: this.validateName,
                  }],
                })(
                  <Input label={<FormattedMessage id="cycleName" />} />,
                )}
              </FormItem>
              <FormItem>
                <span className="c7n-input-wrapper c7n-input-has-value c7n-input-has-label">
                  <div className="c7n-input-label" style={{ transform: 'none' }}><span>持续时间</span></div>
                  {getFieldDecorator('range', {
                    rules: [{
                      required: true, message: '请选择日期!',
                    }],
                  })(
                    <RangePicker
                      format="YYYY-MM-DD"
                      style={{ width: '100%' }}
                    />,
                  )}
                </span>
              </FormItem>
              <FormItem>
                {getFieldDecorator('description', {
                })(
                  <Input label={<FormattedMessage id="comment" />} />,
                )}
              </FormItem>
              <FormItem>
                {getFieldDecorator('build', {
                })(
                  <Input label={<FormattedMessage id="cycle_build" />} />,
                )}
              </FormItem>
              <FormItem>
                {getFieldDecorator('environment', {
                })(
                  <Input label={<FormattedMessage id="cycle_environment" />} />,
                )}
              </FormItem>
            </Form>
          </Spin>
        </Sidebar>
      </div>
    );
  }
}

CreateCycle.propTypes = {

};

export default Form.create()(CreateCycle);
