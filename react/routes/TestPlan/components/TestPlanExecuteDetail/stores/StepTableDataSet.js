import { Choerodon } from '@choerodon/boot';
import { editCycleStep, removeDefect } from '@/api/ExecuteDetailApi';
import { deleteFile } from '@/api/FileApi';

function updateRecordData(data, dataSet, executeHistoryDataSet, record, name, oldValue) {
  // eslint-disable-next-line no-param-reassign
  delete data.defects;
  // eslint-disable-next-line no-param-reassign
  delete data.stepAttachment;
  editCycleStep(data).then(() => {
    dataSet.query(dataSet.currentPage);
    executeHistoryDataSet.query();
  }).catch((error) => {
    window.console.log(error);
    record.set(name, oldValue);
    Choerodon.prompt('网络错误');
  });
}
function StepTableDataSet(projectId, orgId, intl, caseId, testStatusDataSet, executeHistoryDataSet) {
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
        type: 'string',
        label: stepStatus,
        textField: 'statusName',
        valueField: 'statusId',
        options: testStatusDataSet,
        required: true,
        defaultValidationMessages: {
          valueMissing: '请选择{label}',
        },
        // lookupAxiosConfig: () => ({
        //   url: `/test/v1/projects/${projectId}/status/query?project=${projectId}`,
        //   method: 'post',
        //   data: {
        //     statusType: 'CASE_STEP',
        //   },
        // }),  
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
        const data = record.toData();
        switch (name) {
          case 'defects': {
            const arrIDs = [...value].map(i => i.id); // 缺陷,附件使用
            if (value.length < oldValue.length) {
              removeDefect(oldValue.find(item => !arrIDs.includes(item.id)).id).then(() => { executeHistoryDataSet.query(); }).catch((error) => {
                record.set(name, oldValue);
                Choerodon.prompt(`${error || '网络异常'}`);
              });
            }
            break;
          }
          case 'stepStatus': {
            if (oldValue === value) {
              break;
            }
            data.stepStatus = value;
            const statusItem = testStatusDataSet.find(item => item.get('statusId') === value);
            data.statusName = statusItem.get('statusName');
            updateRecordData(data, dataSet, executeHistoryDataSet, record, name, oldValue);
            break;
          }
          case 'description': {
            data.description = value || '';
            updateRecordData(data, dataSet, executeHistoryDataSet, record, name, oldValue);
            break;
          }
          case 'stepAttachment': {
            const arrIDs = [...value].map(i => i.id); // 缺陷,附件使用
            if (value.length < oldValue.length) {
              deleteFile(oldValue.find(item => !arrIDs.includes(item.id)).id).then(() => {
                executeHistoryDataSet.query();
              }).catch((error) => {
                window.console.log(error);
                record.set(name, oldValue);
                Choerodon.prompt(`删除失败 ${error}`);
              });
            }

            break;
          }
          default:
            break;
        }
      },
    },
    transport: {
      read: {
        url: `test/v1/projects/${projectId}/cycle/case/step/query/${caseId}?organizationId=${orgId}`,
        method: 'get',
        transformResponse: (res) => {
          if (typeof res === 'string') {
            const newRes = JSON.parse(res);
            newRes.content.map(item => ({
              ...item,
              stepStatus: item.stepStatus === null ? 4 : item.stepStatus,
            }));
            return newRes;
          }
          return res;
        },
      },
    },
  };
}
export default StepTableDataSet;
