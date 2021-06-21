/*
 * @Author: LainCarl
 * @Date: 2019-01-25 11:36:56
 * @Last Modified by: LainCarl
 * @Last Modified time: 2019-01-25 13:56:36
 * @Feature: 创建状态侧边栏
 */

import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {
  Form, Input, Select, Modal,
} from 'choerodon-ui';
import { Button } from 'choerodon-ui/pro';

import { FormattedMessage } from 'react-intl';
import { ColorPicker } from '../../../../components';
import './CreateStatus.less';

const FormItem = Form.Item;
const { Sidebar } = Modal;
const { Option } = Select;
const defaultProps = {

};

const propTypes = {
  visible: PropTypes.bool.isRequired,
  loading: PropTypes.bool.isRequired,
  onCheckStatusRepeat: PropTypes.func.isRequired,
  onCancel: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
};
class CreateStatus extends Component {
  componentDidUpdate(nextProps) {
    const { resetFields } = this.props.form;
    if (this.props.visible === false && nextProps.visible === true) {
      resetFields();
      this.validateColor();
    }
  }

  // 创建类型改变时检查
  handleStatusTypeChange = () => {
    // 等待form的值改变
    setTimeout(() => {
      this.validateColor();
    }, 0);
  }

  validateColor = () => {
    this.props.form.validateFields(['statusColor'], { force: true });
  }

  handleCheckColor = (rule, statusColor, callback) => {
    const { getFieldValue } = this.props.form;
    const statusType = getFieldValue('statusType');
    this.props.onCheckStatusRepeat({ statusType, statusColor })(rule, statusColor, callback);
  }

  handleOk = () => {
    const { onSubmit } = this.props;
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        onSubmit({
          ...values,
          statusName: values.statusName.trim(),
        });
      }
    });
  }

  handleCheckStatusRepeat = (...args) => {
    const { getFieldsValue } = this.props.form;
    const status = getFieldsValue(['statusType', 'statusName']);
    this.props.onCheckStatusRepeat(status)(...args);
  }

  render() {
    const {
      visible, onCancel, loading, activeKey,
    } = this.props;
    const { getFieldDecorator, getFieldValue } = this.props.form;
    return (
      <div>
        <Sidebar
          className="c7ntest-createStatus-modal"
          title={`创建${getFieldValue('statusType') === 'CYCLE_CASE' ? '执行' : '步骤'}状态`}
          visible={visible}
          // onOk={this.handleOk}
          // onCancel={onCancel}
          // confirmLoading={loading}
          footer={[
            <Button key="back" funcType="raised" onClick={onCancel}><FormattedMessage id="cancel" /></Button>,
            <Button key="submit" color="primary" funcType="raised" loading={loading} onClick={this.handleOk}>
              <FormattedMessage id="save" />
            </Button>,
          ]}
          width={380}
        >
          <Form>
            <FormItem>
              {getFieldDecorator('statusType', {
                initialValue: activeKey,
                rules: [{
                  required: true, message: '请选择类型!',
                }],
              })(
                <Select label={<FormattedMessage id="type" />} onChange={this.handleStatusTypeChange}>
                  <Option value="CYCLE_CASE">执行状态</Option>
                  <Option value="CASE_STEP">步骤状态</Option>
                </Select>,
              )}
            </FormItem>
            <FormItem>
              {getFieldDecorator('statusName', {
                rules: [{
                  required: true,
                  whitespace: true,
                  message: '请输入状态名称',
                }, {
                  validator: this.handleCheckStatusRepeat,
                }],
              })(
                <Input maxLength={30} label={<FormattedMessage id="status_name" />} />,
              )}
            </FormItem>
            <FormItem>
              {getFieldDecorator('description', {
              })(
                <Input maxLength={30} label={<FormattedMessage id="comment" />} />,
              )}
            </FormItem>
            <FormItem className="c7ntest-color-container">
              {getFieldDecorator('statusColor', {
                rules: [{
                  required: true,
                  message: '请选择颜色',
                }, {
                  validator: this.handleCheckColor,
                }],
                initialValue: 'GRAY',
              })(
                <ColorPicker />,
              )}
            </FormItem>
          </Form>
        </Sidebar>
      </div>
    );
  }
}

CreateStatus.propTypes = propTypes;
CreateStatus.defaultProps = defaultProps;
export default Form.create()(CreateStatus);
