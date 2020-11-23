import { stores } from '@choerodon/boot';
import { message, DataSet } from 'choerodon-ui/pro';
import { getProjectId, beforeTextUpload, getOrganizationId } from '@/common/utils';

const { AppState } = stores;
function priorityOptionDataSet() {
  return new DataSet({
    autoQuery: true,
    paging: false,
    fields: [
      { name: 'id', type: 'string' },
      { name: 'name', type: 'string' },
    ],
    transport: {
      read: ({ dataSet }) => ({
        url: `/test/v1/organizations/${getOrganizationId()}/test_priority`,
        method: 'get',
        transformResponse: (data) => {
          const newData = JSON.parse(data);
          if (!Array.isArray(newData)) {
            throw Error(newData.message);
          }
          return JSON.parse(data).filter((item) => item.enableFlag);
        },
      }),
    },
  });
}
function CreateIssueDataSet(intlPrefix, intl, priorityOptionsDataSet) {
  const summary = intl.formatMessage({ id: `${intlPrefix}_issueFilterBySummary` });
  const description = '描述';
  const priority = intl.formatMessage({ id: `${intlPrefix}_issueFilterByPriority` });
  const folderId = intl.formatMessage({ id: `${intlPrefix}_folder` });
  const Issuelabel = intl.formatMessage({ id: 'summary_label' });
  return {
    autoQuery: false,
    selection: false,
    autoCreate: true,
    paging: false,
    dataKey: null,
    fields: [
      {
        name: 'summary', type: 'string', label: summary, required: true,
      },
      { name: 'description', type: 'object', label: description },
      {
        name: 'priorityId',
        type: 'string',
        label: priority,
        required: true,
        textField: 'name',
        valueField: 'id',
        options: priorityOptionsDataSet,
        // dynamicProps: {
        //   defaultValue: ({ record }) => {
        //     console.log('defaultValue....', priorityOptionsDataSet);
        //     // const defaultRecord = priorityOptionsDataSet.find(item => item.get('defaultFlag'));
        //     // console.log('defaultRecord', defaultRecord);
        //     // if (defaultRecord) {
        //     //   record.set('priorityId', defaultRecord.get('id'));
        //     //   return defaultRecord.get('id');
        //     // }
        //     return undefined;
        //   },
        // },
      },

      {
        name: 'fileList',
        type: 'object',
        label: '附件',

      },
      {
        name: 'folder',
        type: 'object',
        label: folderId,
        required: true,
        textField: 'fileName',
        valueField: 'folderId',
        ignore: 'always',
      },
      {
        name: 'folderId',
        type: 'string',
        bind: 'folder.folderId',
      },
      {
        name: 'issueLink',
        type: 'object',
        label: Issuelabel,
        textField: 'labelName',
        valueField: 'labelId',
        multiple: ',',
        // options: linkOptions,
      },
      {
        name: 'caseStepVOS',
        type: 'object',
      },

    ],
    feedback: {
      submitSuccess: (resp) => {
        message.success('创建成功');
      },
    },
    transport: {
      submit: ({ data, dataSet }) => {
        const newData = {
          ...data[0],
          caseStepVOS: data[0].caseStepVOS.filter((i) => i.stepIsCreating !== true).map((i) => ({
            testStep: i.testStep,
            testData: i.testData,
            expectedResult: i.expectedResult,
          })),
        };
        return ({
          url: `test/v1/projects/${getProjectId()}/case/create`,
          method: 'post',
          data: newData,
        });
      },
    },
  };
}
export default CreateIssueDataSet;
