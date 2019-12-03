import React from 'react';
import TestHandExecute from './TestHandExecute';
import { StoreProvider } from './stores';

export default props => (
  <StoreProvider {...props}>
    <TestHandExecute />
  </StoreProvider>
);
