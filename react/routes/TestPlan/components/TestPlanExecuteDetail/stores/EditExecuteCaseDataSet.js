import { getProjectId } from '@/common/utils';

function EditExecuteCaseDataSet(executeId, intlPrefix, intl, priorityOptionDataSet) {
  const summary = intl.formatMessage({ id: `${intlPrefix}_issueFilterBySummary` });
  const priority = intl.formatMessage({ id: `${intlPrefix}_issueFilterByPriority` });
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
      {
        name: 'description', type: 'any', label: description, trim: 'none', 
      },
      {
        name: 'priorityId',
        type: 'string',
        label: priority,
        required: true,
        textField: 'name',
        valueField: 'id',
        options: priorityOptionDataSet,

      },

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
          const fileList = newData.cycleCaseAttachmentRelVOList.map((file) => ({
            uid: -file.id, // 文件唯一标识，建议设置为负数，防止和内部产生的 id 冲突
            name: file.attachmentName, // 文件名 
            status: 'done', // 状态有：uploading done error removed
            ...file,
            url: file.url.substr(0, 1) === '/' ? file.url.substring(1) : file.url,
          }));
          return {
            ...newData,
            // description: text2Delta(newData.description), 
            testCycleCaseStepUpdateVOS: [],
            caseStepVOS: newData.testCycleCaseStepUpdateVOS,
            cycleCaseAttachmentRelVOList: fileList,
          };
        },
      }),
    },
  };
}

export default EditExecuteCaseDataSet;
