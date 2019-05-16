import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';


const ReportHome = asyncRouter(() => import('./ReportHome'));
const ReportStory = asyncRouter(() => import('./ReportStory'));
const ReportTest = asyncRouter(() => import('./ReportTest'));
const ReportProgress = asyncRouter(() => import('./ReportProgress'));
const CycleIndex = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={ReportHome} />
    <Route exact path={`${match.url}/story`} component={ReportStory} />
    <Route exact path={`${match.url}/test`} component={ReportTest} />
    <Route exact path={`${match.url}/progress`} component={ReportProgress} />
    <Route path={'*'} component={nomatch} />
  </Switch>
);

export default CycleIndex;
