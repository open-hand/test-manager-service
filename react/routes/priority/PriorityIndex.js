import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { nomatch } from '@choerodon/boot';
import { PermissionRoute } from '@choerodon/master';

const PriorityList = React.lazy(() => import('./priorityList'));

const PriorityIndex = ({ match }) => (
  <Switch>
    <PermissionRoute
      service={[
        'choerodon.code.organization.setting.issue.priority.ps.default',
      ]}
      exact
      path={match.url}
      component={PriorityList}
    />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default PriorityIndex;
