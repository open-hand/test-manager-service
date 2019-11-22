import React from 'react';
import { Choerodon } from '@choerodon/boot';
import {
  Tooltip, Menu, Modal, Dropdown, Button,
} from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import { cloneIssue, deleteIssue } from '../../../../api/IssueManageApi';
import './tags.less';

const { confirm } = Modal;

export function renderIssueNum(issueNum) {
  return (
    <Tooltip mouseEnterDelay={0.5} title={<FormattedMessage id="issue_issueNum" values={{ num: issueNum }} />}>
      {/* <a style={styles.issueNum}>
        {issueNum}
      </a> */}
      <span className="c7n-table-issueTreeTtile-table-span">
        {issueNum}
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
  const { caseId, issueNum } = record;  
  const handleDeleteIssue = () => {
    confirm({
      width: 560,
      title: `删除测试用例${issueNum}`,
      content: '这个测试用例将会被彻底删除。包括所有步骤和相关执行',
      onOk: () => deleteIssue(caseId)
        .then((res) => {
          reLoadTable();
          Choerodon.prompt('删除成功');
        }),
      okText: '删除',
      okType: 'danger',
    });
  };

  function handleItemClick(e) {
    // const { issueInfo, enterLoad, leaveLoad, history } = this.props;
    switch (e.key) {
      case 'copy': {
        cloneIssue([{
          caseId: record.caseId,
          folderId: record.folderId,
        }]).then(() => {
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
    <Dropdown overlay={menu} trigger="click" getPopupContainer={trigger => trigger.parentNode}>
      <Button shape="circle" icon="more_vert" />
    </Dropdown>
  );
}
export function renderSummary(summary, record, onClick) {
  return (
    <div style={{ overflow: 'hidden' }}>
      <Tooltip mouseEnterDelay={0.5} placement="topLeft" title={<FormattedMessage id="issue_issueSummary" values={{ summary }} />}>
        <p
          role="none"
          style={{
            overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', marginBottom: 0, maxWidth: 'unset', cursor: 'pointer',
          }}
          className="c7n-table-issueTreeTtile-table-p"
          onClick={() => onClick(record)}
        >

          {summary}
        </p>
      </Tooltip>
    </div>
  );
}
