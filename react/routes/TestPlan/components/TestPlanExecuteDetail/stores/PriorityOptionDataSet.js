
function PriorityOptionDataSet(organizationId) {
  return ({
    autoQuery: true,
    paging: false,
    fields: [
      { name: 'id', type: 'string' },
      { name: 'name', type: 'string' },
    ],
    transport: {
      read: ({ dataSet }) => ({
        url: `/test/v1/organizations/${organizationId}/test_priority`,
        method: 'get',
        transformResponse: (data) => {
          const newData = JSON.parse(data);
          if (!Array.isArray(newData)) {
            throw Error(newData.message);
          }
          return JSON.parse(data).filter(item => item.enableFlag);
        },
      }),
    },
  });
}
export default PriorityOptionDataSet;
