import React from 'react';
import { Link } from 'react-router-dom';
import { FormattedMessage } from 'react-intl';
import { createIssueLink } from '../../../common/utils';

const SelectCreateIssueFooter = () => (
  <Link className="primary" to={createIssueLink()}>
    <FormattedMessage id="issue_create_bug" />
  </Link>
);
export default SelectCreateIssueFooter;
