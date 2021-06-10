import { getProjectId } from '@/common/utils';
import { toJS } from 'mobx';
import { checkPlanName } from '@/api/TestPlanApi';

export default function DataSetFactory({ initValue = {} }, mode, dataSetUpdate = () => {}) {
  return {
    autoCreate: false,
    fields: [
      {
        name: 'name',
        type: 'string',
        label: '计划名称',
        required: true,
        validator: async (value) => {
          if (mode === 'edit' && value === initValue.name) {
            return true;
          }
          const hasSame = await checkPlanName(value);
          return hasSame ? '计划名称重复' : true;
        },
      },
      {
        name: 'range',
        type: 'date',
        range: true,
        label: '持续时间',
        required: true,
        validator: (value) => {
          if (!value || (value && toJS(value).some((item) => !item))) {
            return '请输入持续时间';
          }
          return true;
        },
      },
      {
        name: 'description', type: 'string', label: '描述',
      },
      {
        name: 'sprintId', label: '所属冲刺',
      },
      {
        name: 'productVersionId', label: '所属版本',
      },
      {
        name: 'managerId',
        type: 'string',
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
              url: `/iam/choerodon/v1/projects/${getProjectId()}/users${managerId && managerId === initValue.managerId ? `?id=${managerId}` : ''}`,
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
    events: {
      update: dataSetUpdate,
    },
  };
}
