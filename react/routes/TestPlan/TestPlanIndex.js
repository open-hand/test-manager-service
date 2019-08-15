import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/master';

const TestPlanHome = asyncRouter(() => (import('./TestPlanHome')));
const ExecuteDetailShow = asyncRouter(() => import('./ExecuteDetailShow'));
const TestIndex = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={TestPlanHome} />
    <Route exact path={`${match.url}/executeShow/:id?`} component={ExecuteDetailShow} />
    <Route path="*" component={nomatch} />
  </Switch>
);
export default TestIndex;
