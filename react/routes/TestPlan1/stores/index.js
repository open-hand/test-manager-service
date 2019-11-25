import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import CreateAutoTestStore from './CreateAutoTestStore';

const Store = createContext();

export default Store;

export const StoreProvider = (props) => {
  const { children } = props;
  const createAutoTestStore = useMemo(() => new CreateAutoTestStore(), []);
 
  const value = {
    ...props,
    createAutoTestStore,
    prefixCls: 'c7ntest-testPlan',
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
};
