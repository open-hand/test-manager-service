import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import CreateAutoTestStore from './CreateAutoTestStore';
import TestPlanStore from './TestPlanStore';

const Store = createContext();

export default Store;

export const StoreProvider = (props) => {
  const { children } = props;
  const createAutoTestStore = useMemo(() => new CreateAutoTestStore(), []);
  const testPlanStore = useMemo(() => new TestPlanStore(), []);
 
  const value = {
    ...props,
    createAutoTestStore,
    testPlanStore,
    prefixCls: 'c7ntest-testPlan',
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
};
