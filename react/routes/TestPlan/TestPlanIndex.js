import { LoadingProvider } from '@choerodon/agile/lib/components/Loading';
import React from 'react';
import { StoreProvider } from './stores';
import TestPlan from './TestPlanHome';

export default function Index(props) {
  return (
    <LoadingProvider>
      <StoreProvider {...props}>
        <TestPlan />
      </StoreProvider>
    </LoadingProvider>
  );
}
