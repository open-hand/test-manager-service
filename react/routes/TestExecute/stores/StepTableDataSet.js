
function StepTableDataSet(projectId, orgId, intl, caseId) {
  const testStep = intl.formatMessage({ id: 'execute_testStep' });
  const testData = intl.formatMessage({ id: 'execute_testData' });
  const expectedResult = intl.formatMessage({ id: 'execute_expectedOutcome' });
  const stepStatus = intl.formatMessage({ id: 'execute_stepStatus' });
  const stepAttachment = intl.formatMessage({ id: 'attachment' });
  const defects = intl.formatMessage({ id: 'bug' });
  const comment = intl.formatMessage({ id: 'execute_comment' });

  return {
    autoQuery: true,
    selection: false,
    paging: true,
    dataKey: null,
    data: [{
      testData: 'testData', expectedResult: 'expectedResult', stepStatus: '3', stepAttachment: [], defects: [], comment: '2222',
    }],
    fields: [
      {
        name: 'index', type: 'string', label: '编号',
      },
      {
        name: 'testStep', type: 'string', label: testStep,
      },
      {
        name: 'testData', type: 'string', label: testData,
      },
      {
        name: 'expectedResult', type: 'string', label: expectedResult,
      },
      {
        name: 'stepStatus', type: 'string', label: stepStatus,
      },
      {
        name: 'stepAttachment', type: 'object', label: stepAttachment,
      },
      {
        name: 'defects',
        type: 'object', 
        label: defects,
      },
      { name: 'comment', type: 'string', label: comment },

    ],

    transport: {
      read: {
        url: `test/v1/projects/${projectId}/cycle/case/step/query/${caseId}?organizationId=${orgId}`,
        method: 'get',
        // transformResponse: data => ({
        //   ...JSON.parse(data),
        // }),
      },
    },
  };
}
export default StepTableDataSet;
