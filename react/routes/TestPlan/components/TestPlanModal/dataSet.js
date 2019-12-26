import { getProjectId } from '@/common/utils';

export default function DataSetFactory({ initValue = {} } = {}) {
  const {
    startDate, endDate,
  } = initValue;
  if (startDate && endDate) {
    // eslint-disable-next-line no-param-reassign
    initValue.range = [startDate, endDate];
  }
  return {
    autoCreate: true,
    data: [initValue],
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
        dynamicProps: {
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
