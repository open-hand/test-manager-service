import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';

const TestPlanExecuteDetail = asyncRouter(() => import('./components/TestPlanExecuteDetail'));
const TestPlanIndex = asyncRouter(() => import('./TestPlanIndex'));

const TestPlan = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={TestPlanIndex} />
    <Route exact path={`${match.url}/execute/:id?`} component={TestPlanExecuteDetail} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default TestPlan;
