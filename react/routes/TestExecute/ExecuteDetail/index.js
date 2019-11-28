import React from 'react';
import ExecuteDetail from './ExecuteDetail';
import { StoreProvider } from '../stores';

export default props => (
  <StoreProvider {...props}>
    <ExecuteDetail />
  </StoreProvider>
);
