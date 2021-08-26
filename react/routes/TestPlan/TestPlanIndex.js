import { LoadingProvider } from '@choerodon/agile/lib/components/Loading';
import React from 'react';
import { StoreProvider } from './stores';
import TestPlan from './TestPlanHome';

export default function Index(props) {
  return (
    <LoadingProvider style={{ height: '100%' }} loadId="plan">
      <StoreProvider {...props}>
        <TestPlan />
      </StoreProvider>
    </LoadingProvider>
  );
}
