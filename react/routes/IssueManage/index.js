import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';

const IssueManage = asyncRouter(() => (import('./IssueManage')));

const IssueManageIndex = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={IssueManage} />
    <Route path="*" component={nomatch} />
  </Switch>
);
export default IssueManageIndex;
