/*eslint-disable */
import React, { Component, useState, useEffect } from 'react';
import { Choerodon } from '@choerodon/boot';
import {
  Input, Icon, Select,
} from 'choerodon-ui';
import { TextField, Button } from 'choerodon-ui/pro'
import { observer } from 'mobx-react-lite';
import { FormattedMessage } from 'react-intl';
import _ from 'lodash';
import { getProjectId } from '../../../../common/utils';
import { createIssue } from '../../../../api/IssueManageApi';
import IssueStore from '../../stores/IssueStore';
import IssueTreeStore from '../../stores/IssueTreeStore';
import { PromptInput } from '@/components';
import './CreateIssueTiny.less';

const { Option } = Select;

export default observer(() => {
  const [creating, setCreating] = useState(false);
  const [createLoading, setCreateLoading] = useState(false);
  const [createIssueValue, setCreateIssueValue] = useState('');

  const onCancel = () => {
    setCreating(false);
    setCreateIssueValue('');
  }

  const handleBlurCreateIssue = (e) => {
    let createValue = '';
    if (e.target.value) { // 如果是textField enterDown
      setCreateIssueValue(e.target.value);
      createValue = e.target.value;
    } else {
      createValue = createIssueValue;
    }
    if (createValue !== '' && createValue.trim() !== '') {  // 不等于''并且 不能只是空格
      const folderId = IssueTreeStore.currentFolder.id;
      const data = {
        folderId,
        summary: createValue,
      };
      setCreateLoading(true);
      createIssue(data)
        .then((res) => {
          const { issues } = IssueStore;
          issues.unshift(res); // 直接在store中添加一条，省得重新加载一遍
          IssueStore.setIssues(issues);
          IssueStore.setClickIssue(res); // 将新创建的测试用例打开
          IssueTreeStore.updateHasCase(folderId, true);// 设置含有用例
          IssueStore.setPagination({
            current: 1,
            pageSize: 10,
            total: IssueStore.pagination.total,
          });
          IssueStore.loadIssues(1, 10);
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

  return creating ? (
    <div className="c7ntest-add" style={{ display: 'block', width: '100%' }}>
      <div className="c7ntest-add-testCase">
        <div style={{ flexGrow: 1 }}>
          <PromptInput
            autoFocus
            placeholder="请输入用例概要"
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
            funcType="raised"
            loading={createLoading}
            onClick={handleBlurCreateIssue}
            color="primary"
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
      funcType="flat"
      color="primary"
      onClick={() => {
        setCreating(true);
      }}
    >
      <Icon type="playlist_add icon" style={{ marginRight: 2, marginTop: -1 }} />
      <span><FormattedMessage id="issue_issueCreate" /></span>
    </Button>
  );
}
)
