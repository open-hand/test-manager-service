import React, { Component } from 'react';
import { stores, Content } from 'choerodon-front-boot';
import _ from 'lodash';
import {
  Select, Form, Input, Button, Modal, Icon,
} from 'choerodon-ui';
import { FormattedMessage, injectIntl } from 'react-intl';
import {
  handleFileUpload, beforeTextUpload, getProjectName, randomString,
} from '../../../common/utils';
import {
  getIssueTypes, getPrioritys, getEpics, getSprintsUnClosed, getProjectVersion, getModules, getLabels,
} from '../../../api/agileApi';
import { getUsers } from '../../../api/IamApi';
import { addBugForExecuteOrStep, getIssueLinkTypes } from '../../../api/ExecuteDetailApi';
import { loadIssuesInLink } from '../../../api/IssueManageApi';
import { WYSIWYGEditor, FullEditor } from '../../CommonComponent';
import UserHead from '../../IssueManageComponent/UserHead';
import TypeTag from '../../IssueManageComponent/TypeTag';
import UploadButton from '../../IssueManageComponent/CommonComponent/UploadButton';
import ExecuteDetailStore from '../../../store/project/TestExecute/ExecuteDetailStore';
import './CreateBug.scss';

const sign = false;
const { AppState } = stores;
const { Sidebar } = Modal;
const { Option } = Select;
const FormItem = Form.Item;

@injectIntl
class CreateBug extends Component {
  state = {
    delta: '',
    fullEdit: false,
    selectLoading: false,
    createLoading: false,
    fileList: [],
    priorities: [],
    users: [],
    epics: [],
    sprints: [],
    versionList: [],
    components: [],
    labels: [],
    issueLinkArr: [randomString(5)],
    links: [],
    issueLinkTypes: [],
    issues: [],
    defaultPriority: false,
    bugType: undefined,
  }

  componentDidMount() {
    Promise.all([
      getIssueTypes('agile'), getPrioritys(), getUsers(), getEpics(), getSprintsUnClosed(), getProjectVersion(), getModules(), getLabels(),
    ]).then(([originIssueTypes, priorities, userData, epics, sprints, versionList, components, labels]) => {
      this.setState({
        priorities,
        users: userData.content.filter(u => u.enabled),
        epics,
        sprints,
        versionList,
        components,
        defaultPriority: priorities.find(item => item.default),
        labels,
        bugType: originIssueTypes.find(item => item.typeCode === 'bug'),
      });
    });
  }

  loadUsers = (value) => {
    getUsers(value).then((userData) => {
      this.setState({
        users: userData.content.filter(u => u.enabled),
      });
    });
  }

  debounceFilterIssues = _.debounce((input) => {
    this.setState({
      selectLoading: true,
    });
    loadIssuesInLink(0, 20, undefined, input).then((res) => {
      this.setState({
        issues: res.content,
        selectLoading: false,
      });
    });
  }, 500);

  getLinks() {
    this.setState({
      selectLoading: true,
    });
    getIssueLinkTypes().then((res) => {
      this.setState({
        selectLoading: false,
        links: res.content,
        issueLinkTypes: res.content,
      });
      this.transform(res.content);
    });
  }

  handleUserSelectChange = (value) => {
    const { users } = this.state;
    if (!users.length) {
      this.props.form.setFieldsValue({
        assigneeId: undefined,
      });
    }
  }

  setFileList = (data) => {
    this.setState({ fileList: data });
  }

  transform = (links) => {
    // split active and passive
    const active = links.map(link => ({
      name: link.outWard,
      linkTypeId: link.linkTypeId,
    }));
    const passive = [];
    links.forEach((link) => {
      if (link.inWard !== link.outWard) {
        passive.push({
          name: link.inWard,
          linkTypeId: link.linkTypeId,
        });
      }
    });
    this.setState({
      links: active.concat(passive),
    });
  };

  handleCreateIssue = () => {
    const { form } = this.props;
    const {
      components,
      labels,
      versionList,
      delta,
      bugType,
      issueLinkTypes,
      issues,
    } = this.state;

    form.validateFields((err, values) => {
      if (!err) {
        const exitComponents = components;
        const componentIssueRelDTOList = _.map(values.componentIssueRel, (component) => {
          const target = _.find(exitComponents, { name: component });
          if (target) {
            return target;
          } else {
            return ({
              name: component,
              projectId: AppState.currentMenuType.id,
            });
          }
        });
        const exitLabels = labels;
        const labelIssueRelDTOList = _.map(values.issueLink, (label) => {
          const target = _.find(exitLabels, { labelName: label });
          if (target) {
            return target;
          } else {
            return ({
              labelName: label,
              projectId: AppState.currentMenuType.id,
            });
          }
        });
        const exitFixVersions = versionList;
        const fixVersionIssueRelDTOList = _.map(values.fixVersionIssueRel, (version) => {
          const target = _.find(exitFixVersions, { name: version });
          if (target) {
            return {
              ...target,
              relationType: 'fix',
            };
          } else {
            return ({
              name: version,
              relationType: 'fix',
              projectId: AppState.currentMenuType.id,
            });
          }
        });
        const issueLinkCreateDTOList = [];
        if (values.linkTypeId) {
          Object.keys(values.linkTypeId).forEach((link, index) => {
            if (values.linkTypeId[link] && values.linkIssues[link]) {
              const currentLinkType = _.find(issueLinkTypes, { linkTypeId: values.linkTypeId[link].split('+')[0] * 1 });
              values.linkIssues[link].forEach((issueId) => {
                if (currentLinkType.inWard === values.linkTypeId[link].split('+')[1]) {
                  issueLinkCreateDTOList.push({
                    linkTypeId: values.linkTypeId[link].split('+')[0] * 1,
                    linkedIssueId: Number(issueId),
                    in: false,
                  });
                } else {
                  issueLinkCreateDTOList.push({
                    linkTypeId: values.linkTypeId[link].split('+')[0] * 1,
                    linkedIssueId: Number(issueId),
                    in: true,
                  });
                }
              });
            }
          });
        }
        const issue = {
          issueTypeId: bugType.id,
          typeCode: bugType.typeCode,
          summary: values.summary,
          priorityId: values.priorityId,
          priorityCode: `priority-${values.priorityId}`,
          sprintId: values.sprintId || 0,
          epicId: values.epicId || 0,
          epicName: values.epicName,
          parentIssueId: 0,
          assigneeId: values.assigneedId,
          labelIssueRelDTOList,
          versionIssueRelDTOList: fixVersionIssueRelDTOList,
          componentIssueRelDTOList,
          storyPoints: values.storyPoints,
          remainingTime: values.estimatedTime,
          issueLinkCreateDTOList,
        };
        this.setState({ createLoading: true });
        const deltaOps = delta;
        if (deltaOps) {
          beforeTextUpload(deltaOps, issue, this.handleSave);
        } else {
          issue.description = '';
          this.handleSave(issue);
        }
      }
    });
  };

  handleSave = (data) => {
    const { fileList } = this.state;
    const { onOk, defectType, id } = this.props;
    const callback = (newFileList) => {
      this.setState({ fileList: newFileList });
    };
    addBugForExecuteOrStep(defectType, id, data)
      .then((res) => {
        if (fileList.length > 0) {
          const config = {
            issueType: res.statusId,
            issueId: res.issueId,
            fileName: fileList[0].name,
            projectId: AppState.currentMenuType.id,
          };
          if (fileList.some(one => !one.url)) {
            handleFileUpload(fileList, callback, config);
          }
        }
        onOk(res);
        ExecuteDetailStore.getInfo();
      })
      .catch(() => {
        onOk();
      });
  };

  onIssueSelectFilterChange(input) {
    // if (!sign) {
    this.setState({
      selectLoading: true,
    });
    loadIssuesInLink(0, 20, undefined, input).then((res) => {
      this.setState({
        issues: res.content,
        selectLoading: false,
      });
    });
    //   sign = true;
    // } else {
    //   this.debounceFilterIssues(input);
    // }
  }

  render() {
    const {
      visible,
      onCancel,
      form,
      intl,
    } = this.props;
    const { getFieldDecorator } = form;
    const {
      priorities, defaultPriority, createLoading,
      fullEdit, delta, users,
      epics, sprints, versionList, components,
      labels, fileList, selectLoading, bugType, issueLinkArr, issues, links,
    } = this.state;
    const callback = (value) => {
      this.setState({
        delta: value,
        fullEdit: false,
      });
    };
    return (
      <Sidebar
        className="c7n-createBug"
        title={<FormattedMessage id="createBug_title" />}
        visible={visible || false}
        onOk={this.handleCreateIssue}
        onCancel={onCancel}
        okText={<FormattedMessage id="createBug_okText" />}
        cancelText={<FormattedMessage id="createBug_cancelText" />}
        confirmLoading={createLoading}
      >
        <Content
          title={<FormattedMessage id="createBug_content_title" values={{ name: getProjectName() }} />}
          description={<FormattedMessage id="createBug_content_description" />}
          link="http://v0-10.choerodon.io/zh/docs/user-guide/test-management/execution-test/execution"
        >
          <div>
            <Form layout="vertical">
              <Select
                style={{ width: 520, marginBottom: 20 }}
                label={<FormattedMessage id="createBug_field_issueType" />}
                getPopupContainer={triggerNode => triggerNode.parentNode}
                disabled
                value={bugType && bugType.id}
              >
                {bugType && (
                  <Option key={bugType.id} value={bugType.id}>
                    <div style={{ display: 'inline-flex', alignItems: 'center', padding: '2px' }}>
                      <TypeTag
                        type={bugType}
                        showName
                      />
                    </div>
                  </Option>
                )}
              </Select>
              <FormItem label={<FormattedMessage id="createBug_field_summary" />} style={{ width: 520 }}>
                {getFieldDecorator('summary', {
                  rules: [{ required: true, message: intl.formatMessage({ id: 'createBug_field_summaryRequire' }) }],
                })(
                  <Input label={<FormattedMessage id="createBug_field_summary" />} maxLength={44} placeholder={<FormattedMessage id="createBug_fielf_summaryPlaceHolder" />} />,
                )}
              </FormItem>
              <FormItem label={<FormattedMessage id="createBug_field_priority" />} style={{ width: 520 }}>
                {getFieldDecorator('priorityId', {
                  rules: [{ required: true, message: intl.formatMessage({ id: 'createBug_field_priorityRequire' }) }],
                  initialValue: defaultPriority ? defaultPriority.id : '',
                })(
                  <Select
                    label={<FormattedMessage id="createBug_field_priority" />}
                    getPopupContainer={triggerNode => triggerNode.parentNode}
                  >
                    {priorities && priorities.length && priorities.map(priority => (
                      <Option key={priority.id} value={priority.id}>
                        <div style={{ display: 'inline-flex', alignItems: 'center', padding: 2 }}>
                          <span>{priority.name}</span>
                        </div>
                      </Option>
                    ))}
                  </Select>,
                )}
              </FormItem>
              <div style={{ width: 520 }}>
                <div style={{ display: 'flex', marginBottom: 3, alignItems: 'center' }}>
                  <div style={{ fontWeight: 'bold' }}>{<FormattedMessage id="createBug_field_description" />}</div>
                  <div style={{ marginLeft: 80 }}>
                    <Button className="leftBtn" funcType="flat" onClick={() => this.setState({ fullEdit: true })} style={{ display: 'flex', alignItems: 'center' }}>
                      <Icon type="zoom_out_map" style={{ color: '#3f51b5', fontSize: '18px', marginRight: 12 }} />
                      <span style={{ color: '#3f51b5' }}>{<FormattedMessage id="createBug_field_descriptionFullEdit" />}</span>
                    </Button>
                  </div>
                </div>
                {
                  !fullEdit && (
                    <div className="clear-p-mw">
                      <WYSIWYGEditor
                        value={delta}
                        style={{ height: 200, width: '100%' }}
                        onChange={(value) => {
                          this.setState({ delta: value });
                        }}
                      />
                    </div>
                  )
                }
              </div>

              <FormItem label={<FormattedMessage id="createBug_field_assignee" />} style={{ width: 520, display: 'inline-block' }}>
                {getFieldDecorator('assigneedId', {})(
                  <Select
                    label={<FormattedMessage id="createBug_field_assignee" />}
                    getPopupContainer={triggerNode => triggerNode.parentNode}
                    filter
                    filterOption={false}
                    allowClear
                    loading={selectLoading}
                    onFilterChange={this.loadUsers}
                    onChange={this.handleUserSelectChange}
                  >
                    {users && users.length && users.map(user => (
                      <Option key={user.id} value={user.id}>
                        <div style={{ display: 'inline-flex', alignItems: 'center', padding: 2 }}>
                          <UserHead
                            user={{
                              id: user.id,
                              loginName: user.loginName,
                              realName: user.realName,
                              avatar: user.imageUrl,
                            }}
                          />
                        </div>
                      </Option>
                    ))}
                  </Select>,
                )}
              </FormItem>

              {
                form.getFieldValue('typeCode') !== 'issue_epic' && (
                  <FormItem label={<FormattedMessage id="createBug_field_epic" />} style={{ width: 520 }}>
                    {getFieldDecorator('epicId', {})(
                      <Select
                        label={<FormattedMessage id="createBug_field_epic" />}
                        allowClear
                        filter
                        loading={selectLoading}
                        filterOption={
                          (input, option) => option.props.children && option.props.children.toLowerCase().indexOf(
                            input.toLowerCase(),
                          ) >= 0
                        }
                        getPopupContainer={triggerNode => triggerNode.parentNode}
                      >
                        {epics && epics.length && epics.map(
                          epic => (
                            <Option
                              key={epic.issueId}
                              value={epic.issueId}
                            >
                              {epic.epicName}
                            </Option>
                          ),
                        )}
                      </Select>,
                    )}
                  </FormItem>
                )
              }

              <FormItem label={<FormattedMessage id="createBug_field_sprint" />} style={{ width: 520 }}>
                {getFieldDecorator('sprintId', {})(
                  <Select
                    label={<FormattedMessage id="createBug_field_sprint" />}
                    allowClear
                    filter
                    loading={selectLoading}
                    filterOption={
                      (input, option) => option.props.children.toLowerCase().indexOf(
                        input.toLowerCase(),
                      ) >= 0
                    }
                    getPopupContainer={triggerNode => triggerNode.parentNode}
                  >
                    {sprints && sprints.length && sprints.map(sprint => (
                      <Option key={sprint.sprintId} value={sprint.sprintId}>
                        {sprint.sprintName}
                      </Option>
                    ))}
                  </Select>,
                )}
              </FormItem>

              <FormItem label={<FormattedMessage id="createBug_field_version" />} style={{ width: 520 }}>
                {getFieldDecorator('fixVersionIssueRel', {
                  rules: [{ transform: value => (value ? value.toString() : value) }],
                })(
                  <Select
                    label={<FormattedMessage id="createBug_field_version" />}
                    mode="tags"
                    loading={selectLoading}
                    getPopupContainer={triggerNode => triggerNode.parentNode}
                    tokenSeparators={[',']}
                  >
                    {
                      versionList && versionList.length && versionList.map(
                        version => (
                          <Option
                            key={version.name}
                            value={version.name}
                          >
                            {version.name}
                          </Option>
                        ),
                      )}
                  </Select>,
                )}
              </FormItem>

              <FormItem label={<FormattedMessage id="createBug_field_component" />} style={{ width: 520 }}>
                {getFieldDecorator('componentIssueRel', {
                  rules: [{ transform: value => (value ? value.toString() : value) }],
                })(
                  <Select
                    label={<FormattedMessage id="createBug_field_component" />}
                    mode="tags"
                    loading={selectLoading}
                    getPopupContainer={triggerNode => triggerNode.parentNode}
                    tokenSeparators={[',']}
                  >
                    {
                      components && components.length && components.map(
                        component => (
                          <Option
                            key={component.name}
                            value={component.name}
                          >
                            {component.name}
                          </Option>
                        ),
                      )}
                  </Select>,
                )}
              </FormItem>

              <FormItem label={<FormattedMessage id="createBug_field_label" />} style={{ width: 520 }}>
                {getFieldDecorator('issueLink', {
                  rules: [{ transform: value => (value ? value.toString() : value) }],
                })(
                  <Select
                    label={<FormattedMessage id="createBug_field_label" />}
                    mode="tags"
                    getPopupContainer={triggerNode => triggerNode.parentNode}
                    tokenSeparators={[',']}
                  >
                    {labels && labels.length && labels.map(label => (
                      <Option key={label.labelName} value={label.labelName}>
                        {label.labelName}
                      </Option>
                    ))}
                  </Select>,
                )}
              </FormItem>
              {issueLinkArr && issueLinkArr.length > 0 && (
                issueLinkArr.map((item, index, arr) => (
                  <div
                    key={item}
                    style={{
                      display: 'flex', width: 520, justifyContent: 'flex-start', alignItems: 'flex-end',
                    }}
                  >
                    <FormItem label="关系" style={{ width: 110, marginRight: 20 }}>
                      {getFieldDecorator(`linkTypeId[${item}]`, {
                      })(
                        <Select
                          label="关系"
                          loading={selectLoading}
                          getPopupContainer={triggerNode => triggerNode.parentNode}
                          tokenSeparators={[',']}
                          onFocus={() => {
                            this.getLinks();
                          }}
                        >
                          {links.map(link => (
                            <Option key={`${link.linkTypeId}+${link.name}`} value={`${link.linkTypeId}+${link.name}`}>
                              {link.name}
                            </Option>
                          ))}
                        </Select>,
                      )}
                    </FormItem>
                    <FormItem label="问题" style={{ width: 290, marginRight: 20 }}>
                      {getFieldDecorator(`linkIssues[${item}]`, {
                      })(
                        <Select
                          label="问题"
                          mode="multiple"
                          loading={selectLoading}
                          dropdownClassName="c7ntest-inline-flex-dropdown"
                          optionLabelProp="showName"
                          filter
                          filterOption={false}
                          onFilterChange={this.onIssueSelectFilterChange.bind(this)}
                          getPopupContainer={triggerNode => triggerNode.parentNode}
                        >
                          {issues.map(issue => (
                            <Option
                              key={issue.issueId}
                              value={issue.issueId}
                              showName={issue.issueNum}
                            >
                              <div style={{
                                display: 'inline-flex',
                                overflow: 'hidden',
                                flex: 1,
                                alignItems: 'center',
                                verticalAlign: 'bottom',
                              }}
                              >
                                <TypeTag
                                  type={issue.issueTypeDTO}
                                />
                                <span style={{
                                  paddingLeft: 12, paddingRight: 12, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap',
                                }}
                                >
                                  {issue.issueNum}
                                </span>
                                <div style={{ overflow: 'hidden', flex: 1 }}>
                                  <p style={{
                                    overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', marginBottom: 0, maxWidth: 'unset',
                                  }}
                                  >
                                    {issue.summary}
                                  </p>
                                </div>
                              </div>
                            </Option>
                          ))}
                        </Select>,
                      )}
                    </FormItem>
                    <Button
                      shape="circle"
                      style={{ marginBottom: 10, marginRight: 10 }}
                      onClick={() => {
                        arr.splice(index + 1, 0, randomString(5));
                        this.setState({
                          issueLinkArr: arr,
                        });
                      }}
                    >
                      <Icon type="add icon" />
                    </Button>
                    {
                      issueLinkArr.length > 1 ? (
                        <Button
                          shape="circle"
                          style={{ marginBottom: 10 }}
                          onClick={() => {
                            arr.splice(index, 1);
                            this.setState({
                              issueLinkArr: arr,
                            });
                          }}
                        >
                          <Icon type="delete" />
                        </Button>
                      ) : null
                    }
                  </div>
                )))}
            </Form>

            <div className="sign-upload" style={{ marginTop: 20 }}>
              <div style={{ display: 'flex', marginBottom: '13px', alignItems: 'center' }}>
                <div style={{ fontWeight: 'bold' }}>{<FormattedMessage id="createBug_field_annex" />}</div>
              </div>
              <div style={{ marginTop: -38 }}>
                <UploadButton
                  onRemove={this.setFileList}
                  onBeforeUpload={this.setFileList}
                  fileList={fileList}
                />
              </div>
            </div>
          </div>

          {
            fullEdit ? (
              <FullEditor
                initValue={delta}
                visible={fullEdit}
                onCancel={() => this.setState({ fullEdit: false })}
                onOk={callback}
              />
            ) : null
          }
        </Content>
      </Sidebar>
    );
  }
}
export default Form.create({})(CreateBug);
