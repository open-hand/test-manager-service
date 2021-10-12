import React, { createContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import useGetAnnouncementHeight from '@choerodon/agile/lib/hooks/useGetAnnouncementHeight';
import EditIssueStore from './EditIssueStore';

const EditIssueContext = createContext();
export default EditIssueContext;

export const EditIssueContextProvider = injectIntl(inject('AppState', 'HeaderStore')((props) => {
  const announcementHeight = useGetAnnouncementHeight();

  const value = {
    ...props,
    prefixCls: 'c7n-test-EditIssue',
    store: useMemo(() => new EditIssueStore(), []),
    announcementHeight,
  };

  return (
    <EditIssueContext.Provider value={value}>
      {props.children}
    </EditIssueContext.Provider>
  );
}));
