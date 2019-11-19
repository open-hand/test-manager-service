/*eslint-disable */
import React, { Component, useState, useEffect } from 'react';
import { Choerodon } from '@choerodon/boot';
import {
  Button, Input, Icon, Select,
} from 'choerodon-ui';
import { TextField } from 'choerodon-ui/pro'
import { observer } from 'mobx-react-lite';
import { FormattedMessage } from 'react-intl';
import _ from 'lodash';
import { getProjectId } from '../../../../common/utils';
import { createIssue } from '../../../../api/IssueManageApi';
import IssueStore from '../../stores/IssueStore';
import IssueTreeStore from '../../stores/IssueTreeStore';
import './CreateIssueTiny.less';

const { Option } = Select;

export default observer(() => {
  const [ creating, setCreating] = useState(false);
  const [createLoading, setCreateLoading] = useState(false);
  const [createIssueValue, setCreateIssueValue] = useState('');

  const onCancel = () => {
    setCreating(false);
    setCreateIssueValue('');
  }

  const handleBlurCreateIssue = (e) => {
    let createValue = '';
    if(e.target.value) { // 如果是textField enterDown
      setCreateIssueValue(e.target.value);
      createValue = e.target.value;
    } else {
      createValue = createIssueValue;
    }
    if (createValue !== '' && createValue.trim() !== '') {  // 不等于''并且 不能只是空格
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
        summary: createValue,
        epicId: 0,
        parentIssueId: 0,
        versionIssueRelVOList,
      };
      setCreateLoading(true);
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
          setCreating(false);
          setCreateLoading(false);
          setCreateIssueValue('');
        })
        .catch((error) => {
          console.log(error);
          setCreateLoading(false);
        });
    } else {
      onCancel();
    }
  }

    const versions = IssueStore.getVersions;
    const selectedVersion = IssueTreeStore.currentCycle.versionId || IssueStore.getSeletedVersion;
    return creating ? (
      <div className="c7ntest-add" style={{ display: 'block', width: '100%' }}>
        <div className="c7ntest-add-select-version">
          <div style={{ marginLeft: 8, flexGrow: 1 }}>
            <TextField
              autoFocus
              placeholder="请输入问题概要"
              onChange={(value) => { // 失焦才会触发onChange
                setCreateIssueValue(value);
              }}
              maxLength={44}
              onEnterDown={handleBlurCreateIssue}
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
              loading={createLoading}
              onClick={handleBlurCreateIssue}
              color="blue"
            >
              <FormattedMessage id="ok" />
            </Button>
            <Button
              style={{ marginLeft: 10 }}
              funcType="raised"
              onClick={onCancel}
            >
              <FormattedMessage id="cancel" />
            </Button>

          </div>
        </div>
      </div>
    ) : (
        <Button
          type="primary"
          funcType="flat"
          onClick={() => {
            setCreating(true);
          }}
        >
          <Icon type="playlist_add icon" style={{ marginRight: -2 }} />
          <span><FormattedMessage id="issue_issueCreate" /></span>
        </Button>
      );
  }
)