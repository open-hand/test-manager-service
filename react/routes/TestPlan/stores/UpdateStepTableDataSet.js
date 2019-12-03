export default ({ stepData }) => ({
  autoQuery: true,
  selection: false,
  data: stepData,
  paging: false,
  fields: [
    { name: 'stepId', type: 'string', label: '' },
    { name: 'testStep', type: 'string', label: '测试步骤' },
    { name: 'testData', type: 'string', label: '测试数据' },
    {
      name: 'expectedResult', type: 'string', label: '预期结果', 
    },
  ],
});
