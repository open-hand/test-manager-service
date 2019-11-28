import moment from 'moment';
import { getProjectId } from '@/common/utils';

export default function DataSetFactory({ initValue = {}, edit = false, SelectIssueStore } = {}) {
  const { id, objectVersionNumber } = initValue;
  return {
    autoCreate: true,
    data: edit ? [initValue] : undefined,
    transport: {
      submit: {
        url: `/test/v1/projects/${getProjectId()}/plan`,
        method: edit ? 'put' : 'post',
        transformRequest: ([data]) => {
          const {
            range, custom, __id, __status, ...rest
          } = data;
          const plan = {
            ...rest,    
            custom,
            id,
            objectVersionNumber,
            startDate: moment(range[0]).format('YYYY-MM-DD HH:mm:ss'),
            endDate: moment(range[1]).format('YYYY-MM-DD HH:mm:ss'),
            projectId: getProjectId(),
            caseSelected: custom ? SelectIssueStore.getSelectedFolders() : undefined,
          };
          // console.log(plan);
          return JSON.stringify(plan);
        },
      },
    },
    fields: [
      {
        name: 'name', type: 'string', label: '计划名称', required: true, 
      },
      {
        name: 'range',
        type: 'dateTime',
        range: true,
        label: '持续时间',
        required: true,
      },
      {
        name: 'description', type: 'string', label: '描述',
      },
      {
        name: 'managerId',
        type: 'number',
        label: '负责人', 
        required: true,      
        lookupAxiosConfig: ({ record, dataSet: ds }) => {
          let managerId = null;
          if (record && record.data.managerId) {
            // eslint-disable-next-line prefer-destructuring
            managerId = record.data.managerId;
          }
          return {
            url: `/base/v1/projects/${getProjectId()}/users${managerId && managerId === initValue.managerId ? `?id=${managerId}` : ''}`,
          };          
        },
        textField: 'realName',
        valueField: 'id',
      },
      {
        name: 'custom', type: 'boolean', label: '导入方式',
      },
      {
        name: 'autoSync', type: 'boolean', label: '自动同步', defaultValue: true,
      },
    ],
  };
}
