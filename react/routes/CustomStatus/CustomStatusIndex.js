import React from 'react';
import { Route, Switch } from 'react-router-dom';
import {  nomatch } from '@choerodon/boot';
import { PermissionRoute } from '@choerodon/master';

const CustomStatusHome = React.lazy(() => import('./CustomStatusHome'));

const CustomStatusIndex = ({ match }) => (
  <Switch>
    <PermissionRoute
      service={[
        'choerodon.code.project.setting.test.ps.default',
      ]}
      exact
      path={match.url}
      component={CustomStatusHome}
    />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default CustomStatusIndex;
