import { stores } from '@choerodon/boot';
import {
  Form, Input, message, Modal, Checkbox,
} from 'choerodon-ui';
import { observer } from 'mobx-react';
import _ from 'lodash';
import React, { Component } from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';
import { CompactPicker } from 'react-color';

import './priorityCreate.less';
import priorityApi from '@/api/priority';

const FormItem = Form.Item;
const { Sidebar } = Modal;
const { TextArea } = Input;

const { AppState } = stores;

@Form.create({})
@injectIntl
@observer
class PriorityCreate extends Component {
  state = {
    priorityColor: '#3F51B5',
    displayColorPicker: false,
    loading: false,
  };

  handleCreatingOk = () => {
    const { form, PriorityStore } = this.props;
    this.setState({
      loading: true,
    });
    form.validateFieldsAndScroll(async (err, data) => {
      if (!err) {
        const { name, des, default: isDefault } = data;
        const { priorityColor } = this.state;
        const orgId = AppState.currentMenuType.organizationId;

        try {
          await priorityApi.create({
            name,
            description: des,
            defaultFlag: !!isDefault,
            colour: priorityColor,
            // enableFlag: true,
            // objectVersionNumber: 1,
          });
          message.success('添加成功');
          PriorityStore.loadPriorityList(orgId);
          this.hideSidebar();
        } catch (e) {
          message.error('添加失败');
          this.setState({
            loading: false,
          });
        }
      } else {
        this.setState({
          loading: false,
        });
      }
    });
  };

  handleCreatingCancel = () => {
    this.hideSidebar();
  };

  handleChangeComplete = async (color) => {
    this.setState({
      priorityColor: color.hex,
    });
  };

  handleClickSwatch = () => {
    const { displayColorPicker } = this.state;
    this.setState({
      displayColorPicker: !displayColorPicker,
    });
  };

  handleCloseColorPicker = () => {
    this.setState({
      displayColorPicker: false,
    });
  };

  checkName = _.debounce(async (rule, value, callback) => {
    // 名称检查
    const { intl } = this.props;
    const res = await priorityApi.checkName(value);
    if (res) {
      callback(intl.formatMessage({ id: 'priority.create.name.error' }));
    } else {
      callback();
    }
  }, 460);

  hideSidebar() {
    const { PriorityStore, form } = this.props;
    form.resetFields();
    this.setState({
      priorityColor: '#0062B1',
      displayColorPicker: false,
      loading: false,
    });
    PriorityStore.setOnCreatingPriority(false);
  }

  render() {
    const { priorityColor, displayColorPicker, loading } = this.state;
    const { PriorityStore, form, intl } = this.props;
    const { onCreatingPriority } = PriorityStore;
    const { getFieldDecorator } = form;

    return (
      <Sidebar
        title={<FormattedMessage id="priority.create" />}
        visible={onCreatingPriority}
        className="c7ntest-priority-create"
        okText={<FormattedMessage id="save" />}
        cancelText={<FormattedMessage id="cancel" />}
        onOk={this.handleCreatingOk}
        onCancel={this.handleCreatingCancel}
        confirmLoading={loading}
        width={380}
      >
        <Form>
          <FormItem
            label="name"
          >
            {
              getFieldDecorator(
                'name',
                {
                  rules: [
                    {
                      required: true,
                      message: intl.formatMessage({ id: 'priority.name.required.error' }),
                    },
                    {
                      validator: this.checkName,
                    },
                  ],
                },
              )(
                <Input
                  label={<FormattedMessage id="priority.name" />}
                  placeholder={intl.formatMessage({ id: 'priority.create.name.placeholder' })}
                  maxLength={10}
                />,
              )
            }
          </FormItem>
          <FormItem
            label="des"
          >
            {
              getFieldDecorator(
                'des',
              )(
                <TextArea
                  label={<FormattedMessage id="priority.des" />}
                  placeholder={intl.formatMessage({ id: 'priority.create.des.placeholder' })}
                  maxLength={45}
                />,
              )
            }
          </FormItem>
          <div className="issue-color-picker">
            <div className="issue-priority-swatch" onClick={this.handleClickSwatch} role="none">
              <div className="issue-priority-color" style={{ background: priorityColor }} />
            </div>
            {
              displayColorPicker
                ? (
                  <div className="popover">
                    <div className="cover" onClick={this.handleCloseColorPicker} role="none" />
                    <CompactPicker color={priorityColor} onChange={this.handleChangeComplete} />
                  </div>
                )
                : null
            }
          </div>
          <FormItem
            label="default"
          >
            {
              getFieldDecorator(
                'default',
              )(
                <Checkbox>设置为默认优先级</Checkbox>,
              )
            }
          </FormItem>
        </Form>
      </Sidebar>
    );
  }
}

export default PriorityCreate;
