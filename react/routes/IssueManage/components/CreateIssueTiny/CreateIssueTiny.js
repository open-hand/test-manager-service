/*eslint-disable */
import React, { Component } from 'react';
import {
  Button, Input, Icon, Select,
} from 'choerodon-ui';
import { observer } from 'mobx-react';
import { FormattedMessage } from 'react-intl';
import _ from 'lodash';
import { getProjectId } from '../../../../common/utils';
import { createIssue } from '../../../../api/IssueManageApi';
import IssueStore from '../../stores/IssueStore';
import IssueTreeStore from '../../stores/IssueTreeStore';
import './CreateIssueTiny.less';

const { Option } = Select;
function contains(root, n) {
  let node = n;
  while (node) {
    if (node === root) {
      return true;
    }
    node = node.parentNode;
  }

  return false;
}
@observer
class CreateIssueTiny extends Component {
  constructor() {
    super();
    this.containerRef = React.createRef();
    this.state = {
      creating: false,
      createLoading: false,
      createIssueValue: '',
    }
  }
  addEventListener = () => {
    document.addEventListener('click', this.handleDocumentClick)
  }
  removeEventListener = () => {
    document.removeEventListener('click', this.handleDocumentClick)
  }
  handleDocumentClick = (e) => {
    const { creating } = this.state;
    if (!contains(this.containerRef.current, e.target)) {
      this.onBlurCreateInput()
    }
  }
  handleBlurCreateIssue() {
    if (this.state.createIssueValue !== '') {
      const versionIssueRelVOList = [];
      const selectedVersion = IssueTreeStore.currentCycle.versionId || IssueStore.getSeletedVersion;
      const folderId = IssueTreeStore.currentCycle.cycleId;
      // 判断是否选择版本
      const versions = IssueStore.getVersions;
      if (!selectedVersion || !_.find(versions, { versionId: selectedVersion })) {
        Choerodon.prompt('请选择版本');
        return;
      }
      versionIssueRelVOList.push({
        versionId: selectedVersion,
        relationType: 'fix',
      });
      const testType = IssueStore.getTestType;
      const defaultPriority = IssueStore.getDefaultPriority;
      if (!defaultPriority) {
        Choerodon.prompt('未找到优先级');
        return;
      }
      const data = {
        priorityCode: `priority-${defaultPriority}`,
        priorityId: defaultPriority,
        typeCode: 'issue_test',
        issueTypeId: testType,
        projectId: getProjectId(),
        sprintId: 0,
        summary: this.state.createIssueValue,
        epicId: 0,
        parentIssueId: 0,
        versionIssueRelVOList,
      };
      this.setState({
        createLoading: true,
      });
      createIssue(data, folderId)
        .then((res) => {
          let targetCycle = null;
          // 如果指定了文件夹就设置文件夹，否则设置版本
          if (folderId) {
            targetCycle = _.find(IssueTreeStore.dataList, { cycleId: folderId });
          } else {
            const { versionId } = data.versionIssueRelVOList[0];
            targetCycle = _.find(IssueTreeStore.dataList, { versionId });
          }
          if (targetCycle) {
            const expandKeys = IssueTreeStore.getExpandedKeys;
            // 设置当前选中项
            IssueTreeStore.setCurrentCycle(targetCycle);
            // 设置当前选中项
            IssueTreeStore.setSelectedKeys([targetCycle.key]);
            // 设置展开项，展开父元素
            IssueTreeStore.setExpandedKeys([...expandKeys, targetCycle.key.split('-').slice(0, -1).join('-')]);
          }
          IssueStore.loadIssues();
          this.setState({
            createIssueValue: '',
            createLoading: false,
          });
        })
        .catch((error) => {
          console.log(error);
          this.setState({
            createLoading: false,
          });
        });
    }
  }

  onBlurCreateInput = () => {
    const { createIssueValue } = this.state;
    this.removeEventListener()
    if (createIssueValue !== '' && !createIssueValue.match(/^[ ]+$/)) {      
      this.handleBlurCreateIssue();
    } else {
      this.onCancel();
    }
  }

  onCancel = () => {
    this.setState({
      creating: false,
    });
  }
  //               onBlur={this.onBlurCreateInput}
  render() {
    const { creating } = this.state;
    const versions = IssueStore.getVersions;
    const selectedVersion = IssueTreeStore.currentCycle.versionId || IssueStore.getSeletedVersion;
    return creating ? (
      <div className="c7ntest-add" style={{ display: 'block', width: '100%' }} ref={this.containerRef}>
        <div className="c7ntest-add-select-version">
          {/* 创建issue选择版本 */}
          {
            _.find(versions, { versionId: selectedVersion })
              ? (
                <Select
                  disabled={IssueTreeStore.currentCycle.versionId}
                  onChange={(value) => {
                    IssueStore.selectVersion(value);
                  }}
                  value={selectedVersion}
                  style={{ minWidth: 50, height: 36 }}
                  dropdownMatchSelectWidth={false}
                  getPopupContainer={trigger => trigger.parentNode}
                >
                  {
                    versions.map(version => <Option value={version.versionId}>{version.name}</Option>)
                  }
                </Select>
              )
              : (
                <div style={{ color: 'gray', marginTop: -3 }}>
                  {'暂无版本'}
                </div>
              )
          }

          <div style={{ marginLeft: 8, flexGrow: 1 }}>
            <Input
              autoFocus
              value={this.state.createIssueValue}
              // placeholder={<FormattedMessage id="issue_whatToDo" />}
              onChange={(e) => {
                this.setState({
                  createIssueValue: e.target.value,
                });
              }}
              maxLength={44}
              onPressEnter={this.handleBlurCreateIssue.bind(this)}
              style={{ width: '97%' }}
            />
          </div>
          <div style={{
            display: 'flex',
          }}
          >
            <Button
              type="primary"
              funcType="raised"
              loading={this.state.createLoading}
              onClick={this.handleBlurCreateIssue.bind(this)}
              color="blue"
            >
              <FormattedMessage id="ok" />
            </Button>
            <Button
              style={{ marginLeft: 10 }}
              funcType="raised"
              onClick={this.onCancel}
            >
              <FormattedMessage id="cancel" />
            </Button>

          </div>
        </div>
      </div>
    ) : (
        <Button
          className="leftBtn"
          style={{ color: '#3f51b5' }}
          funcType="flat"
          onClick={() => {
            this.setState({
              creating: true,
              createIssueValue: '',
            });
            this.addEventListener()
          }}
        >
          <Icon type="playlist_add icon" style={{ marginRight: -2 }} />
          <span><FormattedMessage id="issue_issueCreate" /></span>
        </Button>
      );
  }
}

export default CreateIssueTiny;
