/*eslint-disable */
import React, { Component } from 'react';
import {
  Button, Input, Icon, Select, 
} from 'choerodon-ui';
import { observer } from 'mobx-react';
import { FormattedMessage } from 'react-intl';
import _ from 'lodash';
import { getProjectId } from '../../../common/utils';
import { createIssue } from '../../../api/IssueManageApi';
import IssueStore from '../../../store/project/IssueManage/IssueStore';
import IssueTreeStore from '../../../store/project/IssueManage/IssueTreeStore';

const { Option } = Select;
@observer
class CreateIssueTiny extends Component {
  state={
    creating: false,
    createLoading: false,
    createIssueValue: '',
  }

  handleBlurCreateIssue() {
    if (this.state.createIssueValue !== '') {
      const versionIssueRelDTOList = [];
      const selectedVersion = IssueTreeStore.currentCycle.versionId || IssueStore.getSeletedVersion;
      const folderId = IssueTreeStore.currentCycle.cycleId;
      // 判断是否选择版本
      const versions = IssueStore.getVersions;
      if (!selectedVersion || !_.find(versions, { versionId: selectedVersion })) {
        Choerodon.prompt('请选择版本');
        return;
      }
      versionIssueRelDTOList.push({
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
        versionIssueRelDTOList,
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
            const {versionId} = data.versionIssueRelDTOList[0];
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

  render() {
    const { creating } = this.state;
    const versions = IssueStore.getVersions;
    const selectedVersion = IssueTreeStore.currentCycle.versionId || IssueStore.getSeletedVersion;
    return creating ? (
      <div className="c7ntest-add" style={{ display: 'block', width: '100%' }}>
        <div className="c7ntest-add-select-version">
          {/* 创建issue选择版本 */}
          {
            _.find(versions, { versionId: selectedVersion })
              ? (
                <div style={{ display: 'flex', alignItems: 'center', marginTop: -8 }}>                 
                  <Select
                    disabled={IssueTreeStore.currentCycle.versionId}
                    onChange={(value) => {
                      IssueStore.selectVersion(value);
                    }}
                    value={selectedVersion}
                    style={{ minWidth: 50 }}
                    dropdownMatchSelectWidth={false}
                  >
                    {
                      versions.map(version => <Option value={version.versionId}>{version.name}</Option>)
                    }
                  </Select>
                </div>
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
              placeholder={<FormattedMessage id="issue_whatToDo" />}
              onChange={(e) => {
                this.setState({
                  createIssueValue: e.target.value,
                });
              }}
              maxLength={44}
              onPressEnter={this.handleBlurCreateIssue.bind(this)}
            />
          </div>
        </div>
        <div style={{
          marginTop: 10, display: 'flex', marginLeft: 50, paddingRight: 70,
        }}
        >
          <Button
            type="primary"
            onClick={() => {
              this.setState({
                creating: false,
              });
            }}
          >
            <FormattedMessage id="cancel" />
          </Button>
          <Button
            type="primary"
            loading={this.state.createLoading}
            onClick={this.handleBlurCreateIssue.bind(this)}
          >
            <FormattedMessage id="ok" />
          </Button>
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
        }}
      >
        <Icon type="playlist_add icon" style={{ marginRight: -2 }} />
        <span><FormattedMessage id="issue_issueCreate" /></span>
      </Button>
    );
  }
}

export default CreateIssueTiny;
