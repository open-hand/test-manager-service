import React from 'react';
import { Link } from 'react-router-dom';
import { FormattedMessage } from 'react-intl';
import { createIssueLink } from '../../../common/utils';

const SelectCreateIssueFooter = () => (
  <Link style={{ color: ' #3F51B5' }} to={createIssueLink()} target="_blank">
    <FormattedMessage id="issue_create_bug" />
  </Link>
);
export default SelectCreateIssueFooter;
