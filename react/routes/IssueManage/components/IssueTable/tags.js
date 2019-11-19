import React, { Fragment } from 'react';
import { Choerodon } from '@choerodon/boot';
import {
  Tooltip, Tag, Menu, Modal, Dropdown, Icon,
} from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import UserHead from '../UserHead';
import PriorityTag from '../PriorityTag';
import StatusTag from '../StatusTag';
import TypeTag from '../TypeTag';
import { cloneIssue, deleteIssue } from '../../../../api/IssueManageApi';
import {
  commonLink, testCaseTableLink,
} from '../../../../common/utils';
import TableDropMenu from '../../../../common/TableDropMenu';
import './tags.less';

const { confirm } = Modal;

const styles = {
  issueNum: {
    padding: '0 12px 0 5px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap',
  },
};
const testTypes = {
  issue_auto_test: '自动化测试',
  issue_test: '测试',
};

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

  const handleLinkToTestCase = () => {
    history.push(testCaseTableLink());
  };

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
        const copyConditionVO = {
          issueLink: false,
          sprintValues: false,
          subTask: false,
          summary: false,
        };
        cloneIssue(caseId, copyConditionVO).then((res) => {
          reLoadTable();
          Choerodon.prompt('复制成功');
        }).catch((err) => {
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
    <Dropdown overlay={menu} trigger="click" className="test-issue-tags-drop-dwon">
      <Icon shape="circle" type="more_vert" style={{ cursor: 'pointer' }} />
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
export function renderUser(name, loginName, realName, imageUrl, hiddenText) {
  return (
    loginName ? (
      <div>
        <UserHead
          hiddenText={hiddenText}
          user={{
            name,
            loginName,
            realName,
            avatar: imageUrl,
          }}
        />
      </div>
    ) : null
  );
}