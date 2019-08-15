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
import { Content } from '@choerodon/master';
import { FormattedMessage } from 'react-intl';
import { getProjectName } from '../../../../common/utils';
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
          title={`编辑“${initValue.statusType === 'CYCLE_CASE' ? '执行' : '步骤'}”状态`}
          visible={visible}
          onOk={this.handleOk}
          onCancel={onCancel}
          confirmLoading={loading}
        >
          <Content
            style={{
              padding: '0 0 10px 0',
            }}
            title={<FormattedMessage id="status_side_edit_content_title" values={{ name: getProjectName() }} />}
            description={<FormattedMessage id="status_side_edit_content_description" />}
            link="http://v0-16.choerodon.io/zh/docs/user-guide/test-management/setting/status/"
          >
            <Form>
              <FormItem>
                {getFieldDecorator('statusName', {
                  rules: [{
                    required: true, message: '请输入状态!',
                  }, {
                    validator: this.handleCheckStatusRepeat,
                  }],
                })(
                  <Input style={{ width: 500 }} maxLength={30} label={<FormattedMessage id="status" />} />,
                )}
              </FormItem>
              <FormItem>
                {getFieldDecorator('description', {
                })(
                  <Input style={{ width: 500 }} maxLength={30} label={<FormattedMessage id="comment" />} />,
                )}
              </FormItem>
              <FormItem>
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
          </Content>
        </Sidebar>
      </div>
    );
  }
}

EditStatus.propTypes = propTypes;
EditStatus.defaultProps = defaultProps;
export default Form.create()(EditStatus);
