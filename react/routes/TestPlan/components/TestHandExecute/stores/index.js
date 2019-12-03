import React, { createContext, useMemo, useState } from 'react';
import { withRouter } from 'react-router-dom';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import ExecuteDetailStoreObject from './ExecuteDetailStore';
import StepTableDataSet from './StepTableDataSet';
import ExecuteHistoryDataSet from './ExecuteHistoryDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = withRouter(injectIntl(inject('AppState')(
  (props) => {
    const {
      AppState: { currentMenuType: { type, id, orgId } }, intl, children, match: { params: { id: caseId } },
    } = props;
    const ExecuteDetailStore = useMemo(() => new ExecuteDetailStoreObject(), []);
    const stepTableDataSet = useMemo(() => new DataSet(StepTableDataSet(id, orgId, intl, caseId)), [caseId, id, intl, orgId]);
    const executeHistoryDataSet = useMemo(() => new DataSet(ExecuteHistoryDataSet(id, intl, caseId)), [caseId, id, intl]);
    const value = {
      ...props,
      ExecuteDetailStore,
      stepTableDataSet,
      executeHistoryDataSet,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
)));
