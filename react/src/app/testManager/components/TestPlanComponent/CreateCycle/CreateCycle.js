import React, { Component } from 'react';
import {
  Form, Input, Select, Modal, Spin, DatePicker,
} from 'choerodon-ui';
import { Content } from '@choerodon/boot';
import { FormattedMessage } from 'react-intl';
import { getProjectVersion } from '../../../api/agileApi';
import { addCycle } from '../../../api/cycleApi';
import { getProjectName } from '../../../common/utils';
import TestPlanStore from '../../../store/project/TestPlan/TestPlanStore';

const { Option } = Select;
const FormItem = Form.Item;
const { Sidebar } = Modal;
const { RangePicker } = DatePicker;
class CreateCycle extends Component {
  state = {
    versions: [],
    selectLoading: false,
    loading: false,
  }

  componentWillReceiveProps(nextProps) {
    const { resetFields } = this.props.form;
    if (this.props.visible === false && nextProps.visible === true) {
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
    const {
      visible, onOk, onCancel, type,
    } = this.props;
    const { getFieldDecorator } = this.props.form;
    const { versions, loading, selectLoading } = this.state;
    const options = versions.map(version => (
      <Option value={version.versionId} key={version.versionId}>
        {version.name}
      </Option>
    ));
    return (
      <div>
        <Sidebar
          title={<FormattedMessage id="cycle_create_title" />}
          visible={visible}
          onOk={this.onOk}
          onCancel={onCancel}
        >
          <Content
            style={{
              padding: '0 0 10px 0',
            }}
            title={<FormattedMessage id="cycle_create_content_title" values={{ name: getProjectName() }} />}
            description={<FormattedMessage id="cycle_create_content_description" />}
            link="http://v0-16.choerodon.io/zh/docs/user-guide/test-management/test-plan/create-cycle/"
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
                      // loading={selectLoading}
                      // onFocus={this.getProjectVersion}
                      style={{ width: 500, margin: '0 0 10px 0' }}
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
                    }],
                  })(
                    <Input style={{ width: 500 }} maxLength={30} label={<FormattedMessage id="name" />} />,
                  )}
                </FormItem>
                <FormItem>
                  {getFieldDecorator('description', {
                  })(
                    <Input style={{ width: 500 }} maxLength={30} label={<FormattedMessage id="comment" />} />,
                  )}
                </FormItem>
                <FormItem>
                  {getFieldDecorator('build', {
                  })(
                    <Input style={{ width: 500 }} maxLength={30} label={<FormattedMessage id="cycle_build" />} />,
                  )}
                </FormItem>
                <FormItem>
                  {getFieldDecorator('environment', {
                  })(
                    <Input style={{ width: 500 }} maxLength={30} label={<FormattedMessage id="cycle_environment" />} />,
                  )}
                </FormItem>
                <FormItem>
                  <span className="c7n-input-wrapper c7n-input-has-value c7n-input-has-label">
                    <div className="c7n-input-label"><span>持续时间</span></div>
                    {getFieldDecorator('range', {
                      rules: [{
                        required: true, message: '请选择日期!',
                      }],
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

CreateCycle.propTypes = {

};

export default Form.create()(CreateCycle);
