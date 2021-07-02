import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import {  nomatch } from '@choerodon/boot';
import { PermissionRoute } from '@choerodon/master';

const CreateAutoTest = React.lazy(() => (import('./CreateAutoTest')));
const AutoTestList = React.lazy(() => import('./AutoTestList'));
const TestReport = React.lazy(() => import('./TestReport'));
const TestIndex = ({ match }) => (
  <Switch>
    <Route exact path={`${match.url}/create`} component={CreateAutoTest} />
    <PermissionRoute
      service={[
        'choerodon.code.project.test.autotest.ps.default',
      ]}
      exact
      path={`${match.url}/list`}
      component={AutoTestList}
    />
    <Route exact path={`${match.url}/report/:id?`} component={TestReport} />
    <Route path="*" component={nomatch} />
  </Switch>
);
export default TestIndex;
