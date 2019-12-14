import React, { createContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import EditIssueStore from './EditIssueStore';

const EditIssueContext = createContext();
export default EditIssueContext;

export const EditIssueContextProvider = injectIntl(inject('AppState', 'HeaderStore')((props) => {
  const value = {
    ...props,
    prefixCls: 'c7n-test-EditIssue',
    store: useMemo(() => new EditIssueStore(), []),
    announcementHeight: props.HeaderStore.announcementClosed ? 0 : 50,
  };

  return (
    <EditIssueContext.Provider value={value}>
      {props.children}
    </EditIssueContext.Provider>
  );
}));
