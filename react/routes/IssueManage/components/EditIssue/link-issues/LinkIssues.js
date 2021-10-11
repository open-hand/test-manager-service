import React from 'react';
import { map } from 'lodash';
import LinkList from '@/components/LinkList';
import { deleteLink } from '@/api/IssueManageApi';
/**
 * 工作项链接
 *
 * @param {*} linkIssues  工作项链接集
 * @param {*} reloadIssue 重载工作项函数
 * @param {*} issueId
 */
const LinkIssues = (props) => {
  const { linkIssues, reloadIssue, issueId } = props;
  const handleDeleteIssue = (linkId) => {
    deleteLink(linkId)
      .then((res) => {
        reloadIssue(issueId);
      });
  };
  const renderLinkList = (link, i) => (
    <LinkList
      key={link.linkId}
      issue={link}
      i={i}
      deleteLink={handleDeleteIssue.bind(this, link.linkId)}
    />
  );
  return map(linkIssues, (linkIssue, i) => renderLinkList(linkIssue, i));
};
export default LinkIssues;
