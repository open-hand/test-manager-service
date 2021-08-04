/*
 * @Author: LainCarl
 * @Date: 2019-01-25 11:37:12
 * @Last Modified by: LainCarl
 * @Last Modified time: 2019-01-25 13:51:42
 * @Feature: 编辑状态侧边栏
 */

import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Form, Input, Modal } from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import { ColorPicker } from '../../../../components';
import './EditStatus.less';

const FormItem = Form.Item;
const { Sidebar } = Modal;
const defaultProps = {

};

const propTypes = {
  visible: PropTypes.bool.isRequired,
  loading: PropTypes.bool.isRequired,
  initValue: PropTypes.shape({}).isRequired,
  onCheckStatusRepeat: PropTypes.func.isRequired,
  onCancel: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
};
class EditStatus extends Component {
  componentWillReceiveProps(nextProps) {
    const { setFieldsValue } = this.props.form;
    if (this.props.visible === false && nextProps.visible === true) {
      const { statusName, description, statusColor } = nextProps.initValue;
      setFieldsValue({ statusName, description, statusColor });
    }
  }

  handleCheckColor = (rule, statusColor, callback) => {
    const { statusType, statusId } = this.props.initValue;
    this.props.onCheckStatusRepeat({ statusType, statusColor, statusId })(rule, statusColor, callback);
  }

  handleCheckStatusRepeat = (...args) => {
    const { getFieldValue } = this.props.form;
    const statusName = getFieldValue('statusName');
    const { statusType, statusId } = this.props.initValue;
    this.props.onCheckStatusRepeat({ statusId, statusName, statusType })(...args);
  }

  handleOk = () => {
    const { initValue, onSubmit } = this.props;

    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        onSubmit({
          ...initValue,
          ...values,
          statusName: values.statusName.trim() || initValue.statusName.trim(),
        });
      }
    });
  }

  render() {
    const {
      visible, onCancel, loading, initValue,
    } = this.props;
    const { getFieldDecorator } = this.props.form;
    return (
      <div>
        <Sidebar
          className="c7ntest-editStatus-modal"
          title={`编辑${initValue.statusType === 'CYCLE_CASE' ? '执行' : '步骤'}状态`}
          visible={visible}
          onOk={this.handleOk}
          onCancel={onCancel}
          confirmLoading={loading}
          width={380}
        >
          <Form>
            <FormItem>
              {getFieldDecorator('statusName', {
                rules: [{
                  required: true,
                  whitespace: true,
                  message: '请输入状态!',
                }, {
                  validator: this.handleCheckStatusRepeat,
                }],
              })(
                <Input maxLength={30} label={<FormattedMessage id="status" />} />,
              )}
            </FormItem>
            <FormItem>
              {getFieldDecorator('description', {
              })(
                <Input style={{ marginTop: 20 }} maxLength={30} label={<FormattedMessage id="comment" />} />,
              )}
            </FormItem>
            <FormItem className="c7ntest-color-container">
              {getFieldDecorator('statusColor', {
                rules: [{
                  required: true, message: '请选择颜色',
                }, {
                  validator: this.handleCheckColor,
                }],
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

EditStatus.propTypes = propTypes;
EditStatus.defaultProps = defaultProps;
export default Form.create()(EditStatus);
