import React from 'react';
import { Route, Switch, RouteChildrenProps } from 'react-router-dom';
import { nomatch } from '@choerodon/boot';

const ReportPage = React.lazy(() => import('./report-page'));

const PlanReport: React.FC<RouteChildrenProps> = ({ match }) => {
  // @ts-ignore
  const id = match?.params?.id as string;
  return (
    <Switch>
      <Route exact path={match?.url}>
        <ReportPage planId={id} />
      </Route>
      <Route path="*" component={nomatch} />
    </Switch>
  );
};

export default PlanReport;
