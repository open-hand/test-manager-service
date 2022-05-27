import { LoadingProvider } from '@choerodon/agile/lib/components/Loading';
import React from 'react';
import TableCache from '@/components/table-cache';
import { StoreProvider } from './stores';
import TestPlan from './TestPlanHome';

export default function Index(props) {
  return (
    <LoadingProvider style={{ height: '100%' }} loadId="plan">
      <TableCache type="testPlan">
        {(cacheProps) => (
          <StoreProvider {...props} {...cacheProps}>
            <TestPlan />
          </StoreProvider>
        )}
      </TableCache>
    </LoadingProvider>
  );
}
