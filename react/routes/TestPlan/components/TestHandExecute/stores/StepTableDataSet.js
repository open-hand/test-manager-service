import { Choerodon } from '@choerodon/boot';
import { editCycleStep, addDefects, removeDefect } from '@/api/ExecuteDetailApi';

function updateRecordData(data, record, name, oldValue) {
  // eslint-disable-next-line no-param-reassign
  delete data.defects;
  // eslint-disable-next-line no-param-reassign
  delete data.stepAttachment;
  editCycleStep(data).then((Data) => {
  }).catch((error) => {
    window.console.log(error);
    record.set(name, oldValue);
    Choerodon.prompt('网络错误');
  });
}
function StepTableDataSet(projectId, orgId, intl, caseId) {
  const testStep = intl.formatMessage({ id: 'execute_testStep' });
  const testData = intl.formatMessage({ id: 'execute_testData' });
  const expectedResult = intl.formatMessage({ id: 'execute_expectedOutcome' });
  const stepStatus = intl.formatMessage({ id: 'execute_stepStatus' });
  const stepAttachment = intl.formatMessage({ id: 'attachment' });
  const defects = intl.formatMessage({ id: 'bug' });
  const description = intl.formatMessage({ id: 'execute_comment' });

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
        name: 'stepStatus',
        type: 'number',
        label: stepStatus,
        lookupAxiosConfig: () => ({
          url: `/test/v1/projects/${projectId}/status/query?project=${projectId}`,
          method: 'post',
          data: {
            statusType: 'CASE_STEP',
          },
        }),
      },
      {
        name: 'stepAttachment', type: 'object', label: stepAttachment,
      },
      {
        name: 'defects',
        type: 'object',
        label: defects,
      },
      {
        name: 'tempDefects',
        type: 'object',
        label: defects,
      },
      {
        name: 'description', type: 'string', label: description,
      },

    ],
    events: {
      update: ({
        dataSet, record, name, value, oldValue,
      }) => {
        // eslint-disable-next-line no-console
        console.log('update', {
          dataSet, record, name, value, oldValue,
        });

        const data = record.toData();
        switch (name) {
          case 'defects':
            // eslint-disable-next-line no-case-declarations
            const arrIDs = value.map(i => i.id);
            removeDefect(oldValue.find(item => !arrIDs.includes(item.id)).id).catch((error) => {
              record.set(name, oldValue);
              Choerodon.prompt(error);
            });
            break;
          case 'stepStatus':
            if (oldValue === value) {
              break;
            }
            data.stepStatus = value;
            updateRecordData(data, record, name, oldValue);
            break;
          case 'description':
            data.description = value;
            updateRecordData(data, record, name, oldValue);
            break;
          case 'stepAttachment':
            // deleteAttachment(value.id).then(() => {
            // }).catch((error) => {
            //   window.console.log(error);
            //   Choerodon.prompt('网络异常');
            // });
            break;
          default:
            break;
        }
      },
    },
    transport: {
      read: {
        url: `test/v1/projects/${projectId}/cycle/case/step/query/${caseId}?organizationId=${orgId}`,
        method: 'get',
        transformResponse: (data) => {
          const newData = JSON.parse(data).map((i, index) => ({
            ...i,
            index: index + 1,
          }));
          return newData;
        },
      },
    },
  };
}
export default StepTableDataSet;
