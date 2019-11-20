import React from 'react';
import { map } from 'lodash';
import LinkList from '../Component/LinkList';

/**
 * 问题链接
 * 
 * @param {*} linkIssues  问题链接集
 * @param {*} reloadIssue 重载问题函数
 * @param {*} issueId 
 */
const LinkIssues = (props) => {
  const { linkIssues, reloadIssue } = props;
  const renderLinkList = (link, i) => {
    const { issueId } = props;
    return (
      <LinkList
        key={link.linkId}
        issue={link}
        i={i}
        onRefresh={() => reloadIssue(issueId)}
      />
    );
  };
  return map(linkIssues, (linkIssue, i) => renderLinkList(linkIssue, i));
};
export default LinkIssues;
