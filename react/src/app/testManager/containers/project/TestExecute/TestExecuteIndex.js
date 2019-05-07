import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from 'choerodon-front-boot';

const TestExecuteHome = asyncRouter(() => import('./TestExecuteHome'));
const ExecuteDetail = asyncRouter(() => import('./ExecuteDetail'));

const TestExecute = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={TestExecuteHome} />
    <Route exact path={`${match.url}/execute/:id?`} component={ExecuteDetail} />    
    <Route path="*" component={nomatch} />
  </Switch>
);

export default TestExecute;
