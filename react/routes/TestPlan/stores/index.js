import React, { createContext } from 'react';
import CreateAutoTestStore from './CreateAutoTestStore';
import TestPlanStore from './TestPlanStore';

const Store = createContext();
export default Store;

export const StoreProvider = (props) => {
  const { children } = props;
  const createAutoTestStore = CreateAutoTestStore;
  const testPlanStore = TestPlanStore;

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
