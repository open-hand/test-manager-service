import { DataSet } from 'choerodon-ui/pro/lib';
import { stores } from '@choerodon/boot';
import { getProjectId } from '@/common/utils';

const { AppState } = stores;

function EditExecuteCaseDataSet(executeId, intlPrefix, intl) {
  const summary = intl.formatMessage({ id: `${intlPrefix}_issueFilterBySummary` });
  const description = '描述';

  return {
    autoQuery: false,
    selection: false,
    autoCreate: false,
    paging: false,
    dataKey: null,
    fields: [
      {
        name: 'summary', type: 'string', label: summary, required: true,
      },
      { name: 'description', type: 'any', label: description },

      // 读取的原始附件列表
      {
        name: 'cycleCaseAttachmentRelVOList',
        type: 'object',
        label: '附件',
        ignore: 'always',

      },
      // 修改操作时的附件列表
      {
        name: 'fileList',
        type: 'object',
        label: '附件',
        ignore: 'always',
      },
      {
        name: 'testCycleCaseStepUpdateVOS',
        type: 'object',
      },
      {
        name: 'caseStepVOS',
        type: 'object',
        ignore: 'always',
      },

    ],

    transport: {
      // /v1/projects/{project_id}/cycle/case/case_step/{execute_id}  
      read: () => ({
        url: `test/v1/projects/${getProjectId()}/cycle/case/case_step/${executeId}`,
        method: 'get',
        transformResponse: (data) => {
          const newData = JSON.parse(data);
          return {
            ...newData,
            // description: text2Delta(newData.description), 
            testCycleCaseStepUpdateVOS: [],
            caseStepVOS: newData.testCycleCaseStepUpdateVOS,
          };
        },
      }),
    },
  };
}

export default EditExecuteCaseDataSet;
