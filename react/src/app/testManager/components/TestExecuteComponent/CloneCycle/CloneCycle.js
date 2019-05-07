import React, { Component } from 'react';
import {
  Modal, Select, Form, Input, Spin, 
} from 'choerodon-ui';
import _ from 'lodash';
import { FormattedMessage } from 'react-intl';
import { getProjectVersion } from '../../../api/agileApi';
import { clone } from '../../../api/cycleApi';

const Option = Select.Option;
const FormItem = Form.Item;
class CloneCycle extends Component {
  state = {
    versions: [],
    loading: false,
  }

  componentWillReceiveProps(nextProps) {
    const { resetFields, setFieldsValue } = this.props.form;
    if (this.props.visible === false && nextProps.visible === true) {
      resetFields();
      setFieldsValue({ cycleName: nextProps.currentCloneCycle.title });
    }
  }
  
  getProjectVersion = () => {
    this.setState({
      selectLoading: true,
    });
    getProjectVersion().then((versions) => {
      console.log(_.reverse(versions));
      this.setState({
        versions: _.reverse(versions),
        selectLoading: false,
      });
    });
  }

  handleOk = () => {
    // console.log(this.props.currentCloneCycle);
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        const { versionId, cycleName } = values;
        this.setState({
          loading: true,
        });
        clone(this.props.currentCloneCycle.cycleId, { cycleName, versionId }, 'CLONE_CYCLE').then((data) => {
          this.setState({
            loading: false,
          });
          if (data.failed) {
            Choerodon.prompt('名字重复');           
          } else {
            this.props.onOk();
          }
        }).catch(() => {
          Choerodon.prompt('网络出错');
          this.setState({
            loading: false,
          });         
        });
      }
    });
  }

  render() {
    const { loading, selectLoading, versions } = this.state;
    const { getFieldDecorator } = this.props.form;
    const versionOptions = versions.map(version => (
      <Option value={version.versionId} key={version.versionId}>
        {version.name}
      </Option>
    ));
    return (
      <Modal
        title={<FormattedMessage id="cycle_cloneCycle" />}
        visible={this.props.visible}
        onOk={this.handleOk}
        onCancel={this.props.onCancel}
      >
        <Spin spinning={loading}>
          <Form>
            <FormItem>
              {getFieldDecorator('versionId', {
                rules: [{
                  required: true, message: '请选择版本!',
                }],
                initialValue: versions[0] && versions[0].versionId,
              })(
                <Select
                // style={{ width: 500, margin: '0 0 10px 0' }}
                  label={<FormattedMessage id="version" />}

                  loading={selectLoading}
                  onFocus={this.getProjectVersion}
                >
                  {versionOptions}
                </Select>,
              )}
            </FormItem>
            <FormItem>
              {getFieldDecorator('cycleName', {
                rules: [{
                  required: true, message: '请输入名称!',
                }],
              })(
                <Input
                // style={{ width: 500, margin: '0 0 10px 0' }}
                  label={<FormattedMessage id="cycle_cycleName" />}
                />,
              )}
            </FormItem>
          </Form>
        </Spin>
      </Modal>
    );
  }
}

CloneCycle.propTypes = {

};

export default Form.create()(CloneCycle);
