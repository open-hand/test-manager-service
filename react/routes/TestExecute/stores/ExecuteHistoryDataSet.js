
function ExecuteHistoryDataSet(projectId, intl, caseId) {
  const user = intl.formatMessage({ id: 'execute_executive' });

  const lastUpdateDate = intl.formatMessage({ id: 'execute_executeTime' });
  const oldValue = intl.formatMessage({ id: 'execute_history_oldValue' });
  const newValue = intl.formatMessage({ id: 'execute_history_newValue' });
  return {
    autoQuery: true,
    selection: false,
    paging: true,
    dataKey: null,
    fields: [
      {
        name: 'user', type: 'string', label: user,
      },
      {
        name: 'lastUpdateDate', type: 'string', label: lastUpdateDate,
      },
      {
        name: 'oldValue', type: 'string', label: oldValue,
      },
      {
        name: 'newValue', type: 'string', label: newValue,
      },

    ],

    transport: {
      read: {
        url: `test/v1/projects/${projectId}/cycle/case/history/${caseId}`,
        method: 'get',
      },
    },
  };
}
export default ExecuteHistoryDataSet;
