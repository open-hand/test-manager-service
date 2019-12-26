import React from 'react';
import TestPlanExecuteDetail from './TestPlanExecuteDetail';
import { StoreProvider } from './stores';

export default props => (
  <StoreProvider {...props}>
    <TestPlanExecuteDetail />
  </StoreProvider>
);
