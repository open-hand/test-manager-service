import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from 'choerodon-front-boot';


const SummaryHome = asyncRouter(() => import('./SummaryHome'));
const SummaryIndex = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={SummaryHome} />
    <Route path={'*'} component={nomatch} />
  </Switch>
);

export default SummaryIndex;
