import React from 'react';
import { StoreProvider } from './stores';
import TestPlan from './TestPlanHome';


export default function Index(props) {
  return (
    <StoreProvider {...props}>
      <TestPlan />
    </StoreProvider>
  );
}
