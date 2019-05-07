import React, { Component } from 'react';
import {
  Form, Input, Select, Modal, Spin, DatePicker, 
} from 'choerodon-ui';
import { Content } from 'choerodon-front-boot';
import { FormattedMessage } from 'react-intl';
import { getProjectVersion } from '../../../api/agileApi';
import { addCycle } from '../../../api/cycleApi';
import { getProjectName } from '../../../common/utils';

const { Option } = Select;
const FormItem = Form.Item;
const { Sidebar } = Modal;

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

  onOk = () => {
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({ loading: true });
        // window.console.log('Received values of form: ', values);
        const { fromDate, toDate } = values;

        addCycle({
          ...values,
          ...{
            type: 'cycle',
            fromDate: fromDate ? fromDate.format('YYYY-MM-DD HH:mm:ss') : null,
            toDate: toDate ? toDate.format('YYYY-MM-DD HH:mm:ss') : null,
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
      visible, onCancel,  
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
        <Spin spinning={loading}>
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
              link="http://v0-8.choerodon.io/zh/docs/user-guide/test-management/test-cycle/create-cycle/"
            >
              <Form>
                <FormItem
                  // {...formItemLayout}
                  label={null}
                >
                  {getFieldDecorator('versionId', {
                    rules: [{
                      required: true, message: '请选择版本!',
                    }],
                  })(
                    <Select
                      loading={selectLoading}
                      onFocus={this.getProjectVersion}
                      style={{ width: 500, margin: '0 0 10px 0' }}
                      label={<FormattedMessage id="version" />}
                    >
                      {options}
                    </Select>,
                  )}
                </FormItem>
                <FormItem
                  // {...formItemLayout}
                  label={null}
                >
                  {getFieldDecorator('cycleName', {
                    rules: [{
                      required: true, message: '请输入名称!',
                    }],
                  })(
                    <Input style={{ width: 500 }} maxLength={30} label={<FormattedMessage id="name" />} />,
                    // <div style={{ width: 500 }}>
                    //   <TextArea maxLength={30} label="说明" placeholder="说明" autosize />
                    // </div>
                  )}
                </FormItem>
                <FormItem
                  // {...formItemLayout}
                  label={null}
                >
                  {getFieldDecorator('description', {
                    // rules: [{
                    //   required: true, message: '请输入说明!',
                    // }],
                  })(
                    <Input style={{ width: 500 }} maxLength={30} label={<FormattedMessage id="comment" />} />,
                    // <div style={{ width: 500 }}>
                    //   <TextArea maxLength={30} label="说明" placeholder="说明" autosize />
                    // </div>
                  )}
                </FormItem>
                <FormItem
                  // {...formItemLayout}
                  label={null}
                >
                  {getFieldDecorator('build', {
                    // rules: [{
                    //   required: true, message: '请输入构建号!',
                    // }],
                  })(
                    <Input style={{ width: 500 }} maxLength={30} label={<FormattedMessage id="cycle_build" />} />,
                    // <div style={{ width: 500 }}>
                    //   <TextArea maxLength={30} label="说明" placeholder="说明" autosize />
                    // </div>
                  )}
                </FormItem>
                <FormItem
                  // {...formItemLayout}
                  label={null}
                >
                  {getFieldDecorator('environment', {
                    // rules: [{
                    //   required: true, message: '请输入环境!',
                    // }],
                  })(
                    <Input style={{ width: 500 }} maxLength={30} label={<FormattedMessage id="cycle_environment" />} />,
                    // <div style={{ width: 500 }}>
                    //   <TextArea maxLength={30} label="说明" placeholder="说明" autosize />
                    // </div>
                  )}
                </FormItem>
                <FormItem>
                  {getFieldDecorator('fromDate', {
                    // rules: [{
                    //   required: true, message: '请选择日期!',
                    // }],
                  })(
                    <DatePicker
                      format="YYYY-MM-DD"
                      style={{ width: 500 }}
                      label={<FormattedMessage id="cycle_startTime" />}
                    />,
                    // <div style={{ width: 500 }}>
                    //   <TextArea maxLength={30} label="说明" placeholder="说明" autosize />
                    // </div>
                  )}
                </FormItem>
                <FormItem>
                  {getFieldDecorator('toDate', {
                    // rules: [{
                    //   required: true, message: '请选择日期!',
                    // }],
                  })(
                    <DatePicker
                      label={<FormattedMessage id="cycle_endTime" />}
                      format="YYYY-MM-DD"
                      style={{ width: 500 }}
                    />,
                    // <div style={{ width: 500 }}>
                    //   <TextArea maxLength={30} label="说明" placeholder="说明" autosize />
                    // </div>
                  )}
                </FormItem>
              </Form>
            </Content>
          </Sidebar>
        </Spin>
      </div>
    );
  }
}

CreateCycle.propTypes = {

};

export default Form.create()(CreateCycle);
