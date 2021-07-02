import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { nomatch } from '@choerodon/boot';
import { PermissionRoute } from '@choerodon/master';

const TestPlanExecuteDetail = React.lazy(() => import('./components/TestPlanExecuteDetail'));
const TestPlanIndex = React.lazy(() => import('./TestPlanIndex'));
const TestPlanReport = React.lazy(() => import('./plan-report'));

const TestPlan = ({ match }) => (
  <Switch>
    <PermissionRoute
      service={[
        'choerodon.code.project.test.test-plan.ps.default',
      ]}
      exact
      path={match.url}
      component={TestPlanIndex}
    />
    <PermissionRoute
      service={[
        'choerodon.code.project.test.test-plan.ps.default',
      ]}
      exact
      path={`${match.url}/execute/:id?`}
      component={TestPlanExecuteDetail}
    />
    <Route exact path={`${match.url}/report/:id?`} component={TestPlanReport} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default TestPlan;
