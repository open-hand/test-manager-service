import React, { Component } from 'react';
import {
  Modal, Select, Form, Input, Spin,
} from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import { getProjectVersion } from '../../../../api/agileApi';
import { clone, getCyclesByVersionId } from '../../../../api/cycleApi';

const { Option } = Select;
const FormItem = Form.Item;
class CloneStage extends Component {
  state = {
    versions: [],
    cycleList: [],
    loading: false,
  }

  componentWillReceiveProps(nextProps) {
    const { resetFields, setFieldsValue } = this.props.form;
    if (this.props.visible === false && nextProps.visible === true) {
      resetFields();
      setFieldsValue({ cycleName: nextProps.currentCloneStage.title });
      this.setInitValues(nextProps);
    }
  }

  setInitValues = (nextProps) => {
    this.setState({
      loading: true,
    });
    const { versionId, parentCycleId } = nextProps.currentCloneStage;
    const { setFieldsValue } = this.props.form;
    Promise.all([getProjectVersion(), getCyclesByVersionId(versionId)])
      .then(([versions, cycleList]) => {
        this.setState({
          versions,
          cycleList,
          loading: false,
        });
        setFieldsValue({ versionId, parentCycleId });
      });
  }

  // getProjectVersion = () => {
  //   this.setState({
  //     selectLoading: true,
  //   });
  //   getProjectVersion().then((versions) => {
  //     this.setState({
  //       versions,
  //       selectLoading: false,
  //     });
  //   });
  // }

  getCyclesByVersionId = () => {
    this.setState({
      selectLoading: true,
    });
    const { getFieldValue } = this.props.form;
    const versionId = getFieldValue('versionId');
    getCyclesByVersionId(versionId).then((cycleList) => {
      this.setState({
        cycleList,
        selectLoading: false,
      });
    });
  }

  handleOk = () => {
    // console.log(this.props.currentCloneStage);
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        const { versionId, cycleName, parentCycleId } = values;
        this.setState({
          loading: true,
        });
        clone(this.props.currentCloneStage.cycleId, { cycleName, parentCycleId, versionId }, 'CLONE_FOLDER').then((data) => {
          this.setState({
            loading: false,
          });
          if (data.failed) {
            Choerodon.prompt('名字重复');
          } else {
            this.props.onOk();
          }
        }).catch((error) => {    
          if (/error\.clone\.cycle\.date\.not\.be\.null/.test(error.response.data.message)) {
            Choerodon.prompt('阶段持续时间不可为空');
          } else {
            Choerodon.prompt('网络出错');
          }
          this.setState({
            loading: false,
          });
        });
      }
    });
  }

  render() {
    const {
      loading, selectLoading, versions, cycleList,
    } = this.state;
    const { getFieldDecorator, getFieldValue, resetFields } = this.props.form;
    const versionOptions = versions.map(version => (
      <Option value={version.versionId} key={version.versionId}>
        {version.name}
      </Option>
    ));
    const cycleOptions = cycleList.map(cycle => (
      <Option value={cycle.cycleId} key={cycle.cycleId}>
        {cycle.cycleName}
      </Option>
    ));
    return (
      <Modal
        title={<FormattedMessage id="cycle_cloneStage" />}
        visible={this.props.visible}
        onOk={this.handleOk}
        onCancel={this.props.onCancel}
      >
        <Spin spinning={loading}>
          <Form style={{ marginTop: 15 }}>
            <FormItem>
              {getFieldDecorator('versionId', {
                rules: [{
                  required: true, message: '请选择版本!',
                }],
              })(
                <Select
                  // style={{ width: 500, margin: '0 0 10px 0' }}
                  label={<FormattedMessage id="version" />}
                  onChange={() => { resetFields('parentCycleId'); }}
                  loading={selectLoading}
                  // onFocus={this.getProjectVersion}
                >
                  {versionOptions}
                </Select>,
              )}
            </FormItem>
            <FormItem>
              {getFieldDecorator('parentCycleId', {
                rules: [{
                  required: true, message: '请选择循环!',
                }],
              })(
                <Select
                  disabled={!getFieldValue('versionId')}
                  // style={{ width: 500, margin: '0 0 10px 0' }}
                  label={<FormattedMessage id="cycle" />}

                  loading={selectLoading}
                  onFocus={this.getCyclesByVersionId}
                >
                  {cycleOptions}
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
                  label={<FormattedMessage id="cycle_stageName" />}
                />,
              )}
            </FormItem>
          </Form>
        </Spin>
      </Modal>
    );
  }
}

CloneStage.propTypes = {

};

export default Form.create()(CloneStage);
