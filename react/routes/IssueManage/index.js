import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';
import { PermissionRoute } from '@choerodon/master';

const IssueManage = asyncRouter(() => (import('./IssueManage')));

const IssueManageIndex = ({ match }) => (
  <Switch>
    <PermissionRoute
      service={[
        'choerodon.code.project.test.manager.ps.default',
      ]}
      exact
      path={match.url}
      component={IssueManage}
    />
    <Route path="*" component={nomatch} />
  </Switch>
);
export default IssueManageIndex;
