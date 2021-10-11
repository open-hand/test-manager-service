import React from 'react';
import { map } from 'lodash';
import LinkList from '@/components/LinkList';
/**
 * 工作项链接
 *
 * @param {*} linkIssues  工作项链接集
 */
const LinkIssues = (props) => {
  const { linkIssues } = props;

  const renderLinkList = (link, i) => (
    <LinkList
      key={link.linkId}
      issue={link}
      i={i}
    />
  );
  return map(linkIssues, (linkIssue, i) => renderLinkList(linkIssue, i));
};
export default LinkIssues;
