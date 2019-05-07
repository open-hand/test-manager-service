import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from 'choerodon-front-boot';

const CustomStatusHome = asyncRouter(() => import('./CustomStatusHome'));

const CustomStatusIndex = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={CustomStatusHome} />        
    <Route path="*" component={nomatch} />
  </Switch>
);

export default CustomStatusIndex;
