import React from 'react';
import { toJS } from 'mobx';
import { C7NFormat } from '@choerodon/master';
import { getProjectId } from '@/common/utils';
import { checkPlanName } from '@/api/TestPlanApi';

export default function DataSetFactory({ initValue = {} }, mode, dataSetUpdate = () => {}) {
  return {
    autoCreate: false,
    fields: [
      {
        name: 'name',
        type: 'string',
        label: (
          <span>
            <C7NFormat
              intlPrefix="test.plan"
              id="name"
            />
          </span>),
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
        name: 'description',
        type: 'string',
        label: (
          <span>
            <C7NFormat
              intlPrefix="test.plan"
              id="description"
            />
          </span>),
      },
      {
        name: 'sprintId',
        label: (
          <span>
            <C7NFormat
              intlPrefix="test.plan"
              id="belong.sprint"
            />
          </span>),
      },
      {
        name: 'productVersionId',
        label: (
          <span>
            <C7NFormat
              intlPrefix="test.plan"
              id="belong.version"
            />
          </span>),
      },
      {
        name: 'managerId',
        type: 'string',
        label: (
          <span>
            <C7NFormat
              intlPrefix="test.plan"
              id="responsible"
            />
          </span>),
        required: true,
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
