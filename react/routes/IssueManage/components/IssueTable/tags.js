/* eslint-disable react/jsx-no-bind */
import React from 'react';
import { Choerodon } from '@choerodon/boot';
import {
  Tooltip, Menu, Dropdown, Icon,
} from 'choerodon-ui';
import { C7NFormat } from '@choerodon/master';
import { Modal } from 'choerodon-ui/pro';
import { FormattedMessage } from 'react-intl';
import { copyIssues, deleteIssue } from '../../../../api/IssueManageApi';
import './tags.less';

export function renderIssueNum(caseNum) {
  return (
    <Tooltip mouseEnterDelay={0.5} title={<FormattedMessage id="issue_issueNum" values={{ num: caseNum }} />}>
      <span className="c7ntest-text-dot" style={{ wordBreak: 'break-all', display: 'block' }}>
        {caseNum}
      </span>
    </Tooltip>
  );
}
/**
 *  自动化测试无法复制与删除
 * @param {*} record
 * @param {*} history
 * @param {*} reLoadTable
 */
export function renderAction(record, history, reLoadTable) {
  const { caseId, caseNum } = record;
  const handleDeleteIssue = () => {
    Modal.open({
      width: 560,
      title: '确认删除',
      children: `确认删除测试用例${caseNum}？`,
      onOk: () => deleteIssue(caseId)
        .then((res) => {
          reLoadTable();
          Choerodon.prompt('删除成功');
        }),
      okText: '确认',
    });
  };

  function handleItemClick(e) {
    // const { issueInfo, enterLoad, leaveLoad, history } = this.props;
    switch (e.key) {
      case 'copy': {
        copyIssues([{
          caseId: record.caseId,
          folderId: record.folderId,
        }], record.folderId).then(() => {
          reLoadTable();
          Choerodon.prompt('复制成功');
        }).catch(() => {
          Choerodon.prompt('网络错误');
        });
        break;
      }
      case 'delete': {
        handleDeleteIssue(caseId);
        break;
      }
      default: break;
    }
  }

  const menu = (
    <Menu onClick={handleItemClick}>
      <Menu.Item key="copy">
        <C7NFormat
          intlPrefix="test.caseLibrary"
          id="copy.case"
        />
      </Menu.Item>
      <Menu.Item key="delete">
        <C7NFormat
          intlPrefix="boot"
          id="delete"
        />
      </Menu.Item>
    </Menu>
  );
  return (
    <Dropdown overlay={menu} trigger={['click']}>
      <Icon type="more_vert" className="action-icon" />
    </Dropdown>
  );
}
export function renderSummary(summary, record, onClick) {
  return (
    <span style={{ overflow: 'hidden' }}>
      <Tooltip mouseEnterDelay={0.5} placement="topLeft" title={<FormattedMessage id="issue_issueSummary" values={{ summary }} />}>
        <p
          role="none"
          className="c7n-table-issueTreeTtile-table-p"
          style={{
            marginBottom: 0,
          }}
        >
          <span
            role="none"
            onClick={() => onClick(record)}
            className="c7n-agile-table-cell-click"
            style={{
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              whiteSpace: 'nowrap',
              marginBottom: 0,
              display: 'inline-block',
              width: '100%',
              maxWidth: '100%',
            }}
          >
            {summary}
          </span>
        </p>
      </Tooltip>
    </span>
  );
}
