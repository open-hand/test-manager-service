export default projectId => ({
  autoQuery: true,
  pageSize: 0,
  paging: false,
  transport: {
    read: {
      url: `/test/v1/projects/${projectId}/status/query?project=${projectId}`,
      method: 'post',
      data: {
        statusType: 'CASE_STEP',  
      },
    },
  },
  fields: [
    { name: 'statusName', type: 'string' },
    { name: 'statusId', type: 'string' },
    { name: 'projectId', type: 'number' },
  ],
});
