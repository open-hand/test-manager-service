import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';

const TestHandExecute = asyncRouter(() => import('./components/TestHandExecute'));
const TestPlanIndex = asyncRouter(() => import('./TestPlanIndex'));

const TestExecute = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={TestPlanIndex} />
    <Route exact path={`${match.url}/execute/:id?`} component={TestHandExecute} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default TestExecute;
