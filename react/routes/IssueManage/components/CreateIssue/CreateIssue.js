import React, { Component } from 'react';
import { stores, Content } from '@choerodon/master';
import { withRouter } from 'react-router-dom';
import { find, debounce, map } from 'lodash';
import {
  Select, Form, Input, Modal,
} from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import './CreateIssue.scss';
import { UploadButton } from '../CommonComponent';
import {
  handleFileUpload, beforeTextUpload, validateFile, normFile,
} from '../../../../common/utils';
import { createIssue, getFoldersByVersion } from '../../../../api/IssueManageApi';
import IssueStore from '../../stores/IssueStore';
import {
  getLabels, getModules, getPrioritys, getProjectVersion,
} from '../../../../api/agileApi';
import { getUsers } from '../../../../api/IamApi';
import { WYSIWYGEditor } from '../../../../components';
import UserHead from '../UserHead';
import { getProjectName } from '../../../../common/utils';

const { AppState } = stores;
const { Sidebar } = Modal;
const { Option } = Select;
const FormItem = Form.Item;

let sign = false;

class CreateIssue extends Component {
  constructor(props) {
    super(props);
    this.state = {
      createLoading: false,
      selectLoading: false,
      originLabels: [],
      originComponents: [],
      originPriorities: [],
      originFixVersions: [],
      originUsers: [],
      // origin: {},
      folders: [],
    };
  }

  componentDidMount() {
    this.getPrioritys();
    this.loadVersions();
  }

  loadVersions = () => {
    getProjectVersion().then((res) => {
      this.setState({
        originFixVersions: res,
        selectLoading: false,
      });
      const { setFieldsValue } = this.props.form;
      if (find(res, { versionId: this.props.defaultVersion })) {
        setFieldsValue({ versionId: this.props.defaultVersion });
      }
    });
  }

  onFilterChange(input) {
    if (!sign) {
      this.setState({
        selectLoading: true,
      });
      getUsers(input).then((res) => {
        this.setState({
          originUsers: res.list,
          selectLoading: false,
        });
      });
      sign = true;
    } else {
      this.debounceFilterIssues(input);
    }
  }

  debounceFilterIssues = debounce((input) => {
    this.setState({
      selectLoading: true,
    });
    getUsers(input).then((res) => {
      this.setState({
        originUsers: res.list,
        selectLoading: false,
      });
    });
  }, 500);


  getPrioritys() {
    getPrioritys().then((priorities) => {
      const defaultPriority = find(priorities, { default: true });
      if (defaultPriority) {
        this.props.form.setFieldsValue({ priorityId: defaultPriority.id });
      }
      this.setState({
        originPriorities: priorities,
      });
    });
  }

  loadFolders = () => {
    const { getFieldValue } = this.props.form;
    if (getFieldValue('versionId')) {
      this.setState({
        selectLoading: true,
      });
      getFoldersByVersion(getFieldValue('versionId')).then((folders) => {
        this.setState({
          folders,
          selectLoading: false,
        });
      });
    }
  }

  handleCreateIssue = () => {
    this.props.form.validateFields((err, values) => {
      if (!err) {
        const { description, fileList } = values;
        const exitComponents = this.state.originComponents;
        const componentIssueRelVOList = map(values.componentIssueRel, (component) => {
          const target = find(exitComponents, { name: component });
          if (target) {
            return target;
          } else {
            return ({
              name: component,
              projectId: AppState.currentMenuType.id,
            });
          }
        });
        const exitLabels = this.state.originLabels;
        const labelIssueRelVOList = map(values.issueLink, (label) => {
          const target = find(exitLabels, { labelName: label });
          if (target) {
            return target;
          } else {
            return ({
              labelName: label,
              projectId: AppState.currentMenuType.id,
            });
          }
        });
        const exitFixVersions = this.state.originFixVersions;
        const version = values.versionId;
        const target = find(exitFixVersions, { versionId: version });
        let fixVersionIssueRelVOList = [];
        if (target) {
          fixVersionIssueRelVOList = [{
            ...target,
            relationType: 'fix',
          }];
        } else {
          Choerodon.prompt('版本错误');
          return null;
        }
        const testType = IssueStore.getTestType;
        const extra = {
          typeCode: 'issue_test',
          issueTypeId: testType,
          summary: values.summary,
          priorityCode: `priority-${values.priorityId}`,
          priorityId: values.priorityId,
          sprintId: values.sprintId || 0,
          epicId: values.epicId || 0,
          epicName: values.epicName,
          parentIssueId: 0,
          assigneeId: values.assigneedId,
          labelIssueRelVOList,
          versionIssueRelVOList: fixVersionIssueRelVOList,
          componentIssueRelVOList,
        };
        this.setState({ createLoading: true });
        const deltaOps = description;
        if (deltaOps) {
          beforeTextUpload(deltaOps, extra, this.handleSave.bind(this, extra, fileList, values.folderId));
        } else {
          extra.description = '';
          this.handleSave(extra, [], values.folderId);
        }
      }
      return null;
    });
  };

  handleSave = (data, fileList, folderId) => {
    createIssue(data, folderId)
      .then((res) => {
        if (fileList.length > 0) {
          const config = {
            issueType: res.statusId,
            issueId: res.issueId,
            fileName: fileList[0].name,
            projectId: AppState.currentMenuType.id,
          };
          if (fileList.some((one) => !one.url)) {
            handleFileUpload(fileList, () => { }, config);
          }
        }
        this.props.onOk(data, folderId);
      });
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const {
      initValue, visible, onCancel, onOk,
    } = this.props;
    const { originPriorities, folders, selectLoading } = this.state;
    const priorityOptions = originPriorities.map((priority) => (
      <Option key={priority.id} value={priority.id}>
        <div style={{ display: 'inline-flex', alignItems: 'center', padding: '2px' }}>
          <div
            className="c7ntest-level"
            style={{
              // backgroundColor: priorityColor,
              color: priority.colour,
              borderRadius: '2px',
              padding: '0 8px',
              display: 'inline-block',
            }}
          >
            {priority.name}
          </div>
        </div>
      </Option>
    ));
    const folderOptions = folders.map((folder) => (
      <Option value={folder.folderId} key={folder.folderId}>
        {folder.name}
      </Option>
    ));

    return (
      <Sidebar
        className="c7ntest-createIssue"
        title={<FormattedMessage id="issue_create_name" />}
        visible={visible || false}
        onOk={this.handleCreateIssue}
        onCancel={onCancel}
        okText="创建"
        cancelText="取消"
        confirmLoading={this.state.createLoading}
      >
        <Content
          style={{
            padding: '1px 0 10px 0',
          }}
        >
          <Form layout="vertical" style={{ width: 670 }} className="c7ntest-form">
            <FormItem>
              {getFieldDecorator('versionId', {
                rules: [
                  {
                    required: true,
                    message: '请选择版本',
                  }, {
                    transform: (value) => (value ? value.toString() : value),
                  }],
              })(
                <Select
                  label={<FormattedMessage id="issue_create_content_version" />}
                  // mode="tags"
                  loading={this.state.selectLoading}
                  getPopupContainer={(triggerNode) => triggerNode.parentNode}
                  tokenSeparators={[',']}
                  onChange={() => {
                    const { resetFields } = this.props.form;
                    resetFields(['folderId']);
                  }}
                >
                  {this.state.originFixVersions.map((version) => <Option key={version.name} value={version.versionId}>{version.name}</Option>)}
                </Select>,
              )}
            </FormItem>
            <FormItem>
              {getFieldDecorator('priorityId', {
                rules: [{ required: true, message: '优先级为必选项' }],
                // initialValue: this.state.origin.defaultPriorityCode,
              })(
                <Select
                  label={<FormattedMessage id="issue_issueFilterByPriority" />}
                  getPopupContainer={(triggerNode) => triggerNode.parentNode}
                >
                  {priorityOptions}
                </Select>,
              )}
            </FormItem>
            <FormItem className="c7ntest-line">
              {getFieldDecorator('summary', {
                rules: [{ required: true, message: '概要为必输项' }],
              })(
                <Input label={<FormattedMessage id="issue_issueFilterBySummary" />} maxLength={44} />,
              )}
            </FormItem>
            <FormItem className="c7ntest-line">
              {getFieldDecorator('description')(
                <WYSIWYGEditor
                  style={{ height: 200, width: '100%' }}
                />,
              )}
            </FormItem>
            <FormItem style={{ display: 'block' }}>
              {getFieldDecorator('fileList', {
                initialValue: [],
                valuePropName: 'fileList',
                getValueFromEvent: normFile,
                rules: [{
                  validator: validateFile,
                }],
              })(
                <UploadButton />,
              )}
            </FormItem>
            <FormItem>
              {getFieldDecorator('assigneedId', {})(
                <Select
                  label={<FormattedMessage id="issue_issueSortByPerson" />}
                  getPopupContainer={(triggerNode) => triggerNode.parentNode}
                  loading={this.state.selectLoading}
                  filter
                  filterOption={false}
                  allowClear
                  onFilterChange={this.onFilterChange.bind(this)}
                >
                  {this.state.originUsers.map((user) => (
                    <Option key={user.id} value={user.id}>
                      <UserHead
                        user={{
                          id: user.id,
                          loginName: user.loginName,
                          realName: user.realName,
                          avatar: user.imageUrl,
                        }}
                      />
                    </Option>
                  ))}
                </Select>,
              )}
            </FormItem>
            <FormItem
              label={null}
            >
              {getFieldDecorator('folderId', {
                rules: [{
                  required: true, message: '请选择文件夹!',
                }],
              })(
                <Select
                  loading={selectLoading}
                  onFocus={this.loadFolders}
                  label={<FormattedMessage id="issue_folder" />}
                >
                  {folderOptions}
                </Select>,
              )}
            </FormItem>
            <FormItem>
              {getFieldDecorator('componentIssueRel', {
                rules: [{ transform: (value) => (value ? value.toString() : value) }],
              })(
                <Select
                  label={<FormattedMessage id="summary_component" />}
                  mode="tags"
                  loading={this.state.selectLoading}
                  getPopupContainer={(triggerNode) => triggerNode.parentNode}
                  tokenSeparators={[',']}
                  onFocus={() => {
                    this.setState({
                      selectLoading: true,
                    });
                    getModules().then((res) => {
                      this.setState({
                        originComponents: res,
                        selectLoading: false,
                      });
                    });
                  }}
                >
                  {this.state.originComponents.map((component) => <Option key={component.name} value={component.name}>{component.name}</Option>)}
                </Select>,
              )}
            </FormItem>
            <FormItem>
              {getFieldDecorator('issueLink', {
                rules: [{ transform: (value) => (value ? value.toString() : value) }],
              })(
                <Select
                  label={<FormattedMessage id="summary_label" />}
                  mode="tags"
                  loading={this.state.selectLoading}
                  getPopupContainer={(triggerNode) => triggerNode.parentNode}
                  tokenSeparators={[',']}
                  onFocus={() => {
                    this.setState({
                      selectLoading: true,
                    });
                    getLabels().then((res) => {
                      this.setState({
                        originLabels: res,
                        selectLoading: false,
                      });
                    });
                  }}
                >
                  {this.state.originLabels.map((label) => (
                    <Option
                      key={label.labelName}
                      value={label.labelName}
                    >
                      {label.labelName}

                    </Option>
                  ))}
                </Select>,
              )}
            </FormItem>
          </Form>
        </Content>
      </Sidebar>
    );
  }
}
export default Form.create({})(withRouter(CreateIssue));
