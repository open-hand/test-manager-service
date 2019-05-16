import React, { Component } from 'react';
import {
  Form, Input, Select, Modal, Spin, DatePicker,
} from 'choerodon-ui';
import { Content, stores } from '@choerodon/boot';
import moment from 'moment';
import { observer } from 'mobx-react';
import { getProjectVersion } from '../../../api/agileApi';
import { editFolder } from '../../../api/cycleApi';
import TestPlanStore from '../../../store/project/TestPlan/TestPlanStore';

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
    const visible = this.props.visible;
    const { getFieldDecorator } = this.props.form;
    const { versions, loading, selectLoading } = this.state;
    const {
      versionId, title, description, build, environment, fromDate, toDate,
    } = TestPlanStore.CurrentEditCycle;
    const options = versions.map(version => (
      <Option value={version.versionId} key={version.versionId}>
        {version.name}
      </Option>
    ));
    return (
      <div>
        <Sidebar
          destroyOnClose
          title="修改测试循环"
          visible={visible}
          onOk={this.onOk}
          onCancel={this.onCancel}
        >
          <Content
            style={{
              padding: '0 0 10px 0',
            }}
            title={`在项目“${AppState.currentMenuType.name}”中修改测试循环`}
            description="您可以更改一个测试循环的具体信息。"
            link="http://v0-8.choerodon.io/zh/docs/user-guide/test-management/test-cycle/"
          >
            <Spin spinning={loading}>
              <Form>
                <FormItem>
                  {getFieldDecorator('cycleName', {
                    initialValue: title,
                    rules: [{
                      required: true, message: '请输入名称!',
                    }],
                  })(
                    <Input style={{ width: 500 }} maxLength={30} label="名称" />,
                  )}
                </FormItem>
                <FormItem>
                  {getFieldDecorator('description', {
                    initialValue: description,
                  })(
                    <Input style={{ width: 500 }} maxLength={30} label="说明" />,
                  )}
                </FormItem>
                <FormItem>
                  {getFieldDecorator('build', {
                    initialValue: build,
                  })(
                    <Input style={{ width: 500 }} maxLength={30} label="构建号" />,
                  )}
                </FormItem>
                <FormItem>
                  {getFieldDecorator('environment', {
                    initialValue: environment,
                  })(
                    <Input style={{ width: 500 }} maxLength={30} label="环境" />,
                  )}
                </FormItem>
                <FormItem>
                  <span className="c7n-input-wrapper c7n-input-has-value c7n-input-has-label">
                    <div className="c7n-input-label"><span>持续时间</span></div>
                    {getFieldDecorator('range', {
                      rules: [{
                        required: true,
                        message: '请选择日期!',                      
                      }],
                      initialValue: fromDate && toDate ? [moment(fromDate), moment(toDate)] : undefined,
                    })(
                      <RangePicker                                   
                        format="YYYY-MM-DD"
                        style={{ width: 500 }}
                      />,
                    )}
                  </span>
                </FormItem>
              </Form>
            </Spin>
          </Content>
        </Sidebar>
      </div>
    );
  }
}

export default Form.create()(EditCycle);
