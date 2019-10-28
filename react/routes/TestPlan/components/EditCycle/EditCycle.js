import React, { Component } from 'react';
import {
  Form, Input, Select, Modal, Spin, DatePicker,
} from 'choerodon-ui';
import { Content, stores } from '@choerodon/boot';
import { Choerodon } from '@choerodon/boot';
import moment from 'moment';
import { observer } from 'mobx-react';
import { getProjectVersion } from '../../../../api/agileApi';
import { editFolder, checkCycleName } from '../../../../api/cycleApi';
import TestPlanStore from '../../stores/TestPlanStore';

const { Option } = Select;
const { AppState } = stores;
const FormItem = Form.Item;
const { Sidebar } = Modal;
const { RangePicker } = DatePicker;
@observer
class EditCycle extends Component {
  state = {
    versions: [],
    selectLoading: false,
    loading: false,
  }

  componentWillReceiveProps(nextProps) {
    const { resetFields } = this.props.form;
    if (nextProps.visible && !this.props.visible) {
      resetFields();
    }
  }
  
  onCancel = () => {
    TestPlanStore.ExitEditCycle();
  }

  validateName = async (rule, name, callback) => {
    const initialValue = TestPlanStore.CurrentEditCycle;
    const { versionId, title: cycleName } = initialValue;
    if (!versionId || cycleName === name) {
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

  onOk = () => {
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({ loading: true });
        // window.console.log('Received values of form: ', values);
        const { range } = values;
        const [fromDate, toDate] = range;
        const initialValue = TestPlanStore.CurrentEditCycle;
        editFolder({
          ...values,
          ...{
            cycleId: initialValue.cycleId,
            objectVersionNumber: initialValue.objectVersionNumber,
            type: 'cycle',
            fromDate: fromDate ? fromDate.startOf('day').format('YYYY-MM-DD HH:mm:ss') : null,
            toDate: toDate ? toDate.endOf('day').format('YYYY-MM-DD HH:mm:ss') : null,
          },
        }).then((res) => {
          if (res.failed) {
            Choerodon.prompt('同名循环已存在');
          } else {
            TestPlanStore.getTree();
            TestPlanStore.ExitEditCycle();
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
    this.setState({
      selectLoading: true,
    });
    getProjectVersion().then((versions) => {
      this.setState({
        versions,
        selectLoading: false,
      });
    });
  }


  render() {
    const { visible } = this.props;
    const { getFieldDecorator } = this.props.form;
    const { versions, loading, selectLoading } = this.state;
    const {
      versionId, title, description, build, environment, fromDate, toDate,
    } = TestPlanStore.CurrentEditCycle;

    return (
      <div>
        <Sidebar
          destroyOnClose
          title="修改测试循环"
          visible={visible}
          onOk={this.onOk}
          onCancel={this.onCancel}
          width={380}
        >          
          <Spin spinning={loading}>
            <Form>
              <FormItem>
                {getFieldDecorator('cycleName', {
                  initialValue: title,
                  rules: [{
                    required: true, message: '请输入名称!',
                  }, {
                    validator: this.validateName,
                  }],
                })(
                  <Input maxLength={30} label="名称" />,
                )}
              </FormItem>
              <FormItem>
                {getFieldDecorator('description', {
                  initialValue: description,
                })(
                  <Input maxLength={30} label="说明" />,
                )}
              </FormItem>
              <FormItem>
                {getFieldDecorator('build', {
                  initialValue: build,
                })(
                  <Input maxLength={30} label="构建号" />,
                )}
              </FormItem>
              <FormItem>
                {getFieldDecorator('environment', {
                  initialValue: environment,
                })(
                  <Input maxLength={30} label="环境" />,
                )}
              </FormItem>
              <FormItem>
                <span className="c7n-input-wrapper c7n-input-has-value c7n-input-has-label">
                  <div className="c7n-input-label" style={{ transform: 'none' }}><span>持续时间</span></div>
                  {getFieldDecorator('range', {
                    rules: [{
                      required: true,
                      message: '请选择日期!',                      
                    }],
                    initialValue: fromDate && toDate ? [moment(fromDate), moment(toDate)] : undefined,
                  })(
                    <RangePicker                                   
                      format="YYYY-MM-DD"
                      style={{ width: '100%' }}
                    />,
                  )}
                </span>
              </FormItem>
            </Form>
          </Spin>         
        </Sidebar>
      </div>
    );
  }
}

export default Form.create()(EditCycle);
