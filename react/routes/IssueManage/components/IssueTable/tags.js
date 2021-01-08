import React from 'react';
import { Choerodon } from '@choerodon/boot';
import {
  Tooltip, Menu, Modal, Dropdown, Button,
} from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import { copyIssues, deleteIssue } from '../../../../api/IssueManageApi';
import './tags.less';

const { confirm } = Modal;

export function renderIssueNum(caseNum) {
  return (
    <Tooltip mouseEnterDelay={0.5} title={<FormattedMessage id="issue_issueNum" values={{ num: caseNum }} />}>
      <span style={{ color: 'rgba(0, 0, 0, 0.65)' }} className="c7n-table-issueTreeTtile-table-span">
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
    confirm({
      width: 560,
      title: '确认删除',
      content: `确认删除测试用例${caseNum}？`,
      onOk: () => deleteIssue(caseId)
        .then((res) => {
          reLoadTable();
          Choerodon.prompt('删除成功');
        }),
      okText: '确认',
      okType: 'danger',
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
        复制用例
      </Menu.Item>
      <Menu.Item key="delete">
        删除
      </Menu.Item>
    </Menu>
  );
  return (
    <Dropdown overlay={menu} trigger={['click']} getPopupContainer={(trigger) => trigger.parentNode}>
      <Button shape="circle" icon="more_vert" />
    </Dropdown>
  );
}
export function renderSummary(summary, record, onClick) {
  return (
    <span style={{ overflow: 'hidden' }}>
      <Tooltip mouseEnterDelay={0.5} placement="topLeft" title={<FormattedMessage id="issue_issueSummary" values={{ summary }} />}>
        <p
          role="none"
          style={{
            overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', marginBottom: 0, maxWidth: 'unset',
          }}
          className="c7n-table-issueTreeTtile-table-p"
        >
          <span
            onClick={() => onClick(record)}
            className="c7n-agile-table-cell-click"
          >
            {summary}
          </span>
        </p>
      </Tooltip>
    </span>
  );
}
