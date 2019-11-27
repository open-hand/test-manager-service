import moment from 'moment';
import { getProjectId } from '@/common/utils';

export default function DataSetFactory({ initValue = {}, edit = false } = {}) {
  const { id, objectVersionNumber } = initValue;
  return {
    autoCreate: true,
    data: [initValue],
    transport: {
      submit: {
        url: `/test/v1/projects/${getProjectId()}/test_plan`,
        method: edit ? 'put' : 'post',
        transformRequest: ([data]) => {
          const {
            range, sendDate, __id, __status, users, ...rest
          } = data;
          const event = {
            ...rest,    
            id,
            objectVersionNumber,
            startDate: moment(range[0]).format('YYYY-MM-DD HH:mm:ss'),
            endDate: moment(range[1]).format('YYYY-MM-DD HH:mm:ss'),
            sendDate: moment(sendDate).format('YYYY-MM-DD HH:mm:ss'),
            users: users ? users.join(',') : '',
          };
          return JSON.stringify(event);
        },
      },
    },
    fields: [
      {
        name: 'planName', type: 'string', label: '计划名称', required: true, 
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
        name: 'assignId',
        type: 'number',
        label: '负责人',       
        lookupAxiosConfig: ({ record, dataSet: ds }) => {
          let assignId = null;
          if (record && record.data.assignId) {
            // eslint-disable-next-line prefer-destructuring
            assignId = record.data.assignId;
          }
          return {
            url: `/base/v1/projects/${getProjectId()}/users${assignId && assignId === initValue.assignId ? `?id=${assignId}` : ''}`,
          };          
        },
        textField: 'realName',
        valueField: 'id',
      },
      {
        name: 'importMode', type: 'string', label: '导入方式',
      },
    ],
  };
}
