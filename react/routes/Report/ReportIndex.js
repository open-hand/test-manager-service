import React from 'react';
import { Route, Switch } from 'react-router-dom';
import {  nomatch } from '@choerodon/boot';
import { PermissionRoute } from '@choerodon/master';

const ReportHome = React.lazy(() => import('./ReportHome'));
const ReportStory = React.lazy(() => import('./ReportStory'));
const ReportTest = React.lazy(() => import('./ReportTest'));
const ReportProgress = React.lazy(() => import('./ReportProgress'));
const CycleIndex = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={ReportHome} />
    <PermissionRoute
      service={[
        'choerodon.code.project.operation.chart.ps.choerodon.code.project.operation.chart.ps.reportfromissue',
      ]}
      exact
      path={`${match.url}/story`}
      component={ReportStory}
    />
    <PermissionRoute
      service={[
        'choerodon.code.project.operation.chart.ps.choerodon.code.project.operation.chart.ps.reportfromdefect',
      ]}
      exact
      path={`${match.url}/test`}
      component={ReportTest}
    />
    <Route exact path={`${match.url}/progress`} component={ReportProgress} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default CycleIndex;
