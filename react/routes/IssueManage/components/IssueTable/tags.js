/* eslint-disable react/jsx-no-bind */
import React from 'react';
import { Choerodon } from '@choerodon/boot';
import {
  Tooltip, Menu, Dropdown, Icon,
} from 'choerodon-ui';
import { C7NFormat } from '@choerodon/master';
import TableDropMenu from '@choerodon/agile/lib/components/table-drop-menu';
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

export function renderSummary(summary, record, onClick, reLoadTable) {
  return (
    <TableDropMenu
      text={summary}
      style={{ minWidth: 60 }}
      onTextClick={() => onClick(record)}
      menuData={[
        {
          text: <C7NFormat
            intlPrefix="test.caseLibrary"
            id="copy.case"
          />,
          action: () => {
            copyIssues([{
              caseId: record.caseId,
              folderId: record.folderId,
            }], record.folderId).then(() => {
              reLoadTable();
              Choerodon.prompt('复制成功');
            }).catch(() => {
              Choerodon.prompt('网络错误');
            });
          },
        },
        {
          text: <C7NFormat
            intlPrefix="boot"
            id="delete"
          />,
          action: () => {
            const { caseId, caseNum } = record;
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
          },
        },
      ]}
    />
  );
}
