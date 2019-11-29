import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import CreateAutoTestStore from './CreateAutoTestStore';
import TestPlanStore from './TestPlanStore';
import TestPlanTreeStore from './TestPlanTreeStore';
import UpdateStepTableDataSet from './UpdateStepTableDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = (props) => {
  const { children } = props;
  const createAutoTestStore = useMemo(() => new CreateAutoTestStore(), []);
  const testPlanStore = useMemo(() => new TestPlanStore(), []);
  const issueTreeStore = useMemo(() => new TestPlanTreeStore(), []);
  const oldStepTableDataSet = useMemo(() => new DataSet(UpdateStepTableDataSet({ stepData: testPlanStore.executeOldData.stepData })), [testPlanStore.executeOldData.stepData]);
  const newStepTableDataSet = useMemo(() => new DataSet(UpdateStepTableDataSet({ stepData: testPlanStore.executeNewData.stepData })), [testPlanStore.executeNewData.stepData]);
  
  const value = {
    ...props,
    createAutoTestStore,
    testPlanStore,
    issueTreeStore,
    oldStepTableDataSet,
    newStepTableDataSet,
    prefixCls: 'c7ntest-testPlan',
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
};
