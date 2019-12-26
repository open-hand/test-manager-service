import React, { createContext, useMemo } from 'react';
import { withRouter } from 'react-router-dom';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import ExecuteDetailStoreObject from './ExecuteDetailStore';
import StepTableDataSet from './StepTableDataSet';
import ExecuteHistoryDataSet from './ExecuteHistoryDataSet';
import EditExecuteCaseDataSet from './EditExecuteCaseDataSet';
import TestStatusDataSet from './TestStatusDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = withRouter(injectIntl(inject('AppState')(
  (props) => {
    const {
      AppState: { currentMenuType: { id, orgId } }, intl, children, match: { params: { id: caseId } },
    } = props;
    const testStatusDataSet = useMemo(() => new DataSet(TestStatusDataSet(id)), [id]);
    const ExecuteDetailStore = useMemo(() => new ExecuteDetailStoreObject(), []);
    const executeHistoryDataSet = useMemo(() => new DataSet(ExecuteHistoryDataSet(id, intl, caseId)), [caseId, id, intl]);
    const stepTableDataSet = useMemo(() => new DataSet(StepTableDataSet(id, orgId, intl, caseId, testStatusDataSet, executeHistoryDataSet)), [caseId, executeHistoryDataSet, id, intl, orgId, testStatusDataSet]);
    const editExecuteCaseDataSet = useMemo(() => new DataSet(EditExecuteCaseDataSet(caseId, 'issue', intl)), [caseId, intl]);

    const value = {
      ...props,
      testStatusDataSet,
      executeId: caseId,
      ExecuteDetailStore,
      stepTableDataSet,
      executeHistoryDataSet,
      editExecuteCaseDataSet,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
)));
