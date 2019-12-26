
function ExecuteHistoryDataSet(projectId, intl, caseId) {
  const user = intl.formatMessage({ id: 'execute_executive' });

  const lastUpdateDate = intl.formatMessage({ id: 'execute_executeTime' });
  const oldValue = intl.formatMessage({ id: 'execute_history_oldValue' });
  const newValue = intl.formatMessage({ id: 'execute_history_newValue' });
  return {
    autoQuery: true,
    selection: false,
    paging: true,
    primaryKey: 'executeId',
    fields: [
      {
        name: 'user', type: 'object', label: user,
      },
      {
        name: 'lastUpdateDate', type: 'string', label: lastUpdateDate,
      },
      {
        name: 'field', type: 'string', label: '字段',
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
        transformResponse: data => ({
          ...JSON.parse(data),
        }),
      },
    },
  };
}
export default ExecuteHistoryDataSet;
