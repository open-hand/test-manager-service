import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';
import { PermissionRoute } from '@choerodon/master';

const ReportHome = asyncRouter(() => import('./ReportHome'));
const ReportStory = asyncRouter(() => import('./ReportStory'));
const ReportTest = asyncRouter(() => import('./ReportTest'));
const ReportProgress = asyncRouter(() => import('./ReportProgress'));
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
