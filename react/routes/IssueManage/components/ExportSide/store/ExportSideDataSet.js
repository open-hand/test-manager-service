import { DataSet } from 'choerodon-ui/pro/lib';
import { useMemo } from 'react';

const ExportSideDataSet = () => new DataSet({
  autoQuery: true,
  paging: true,
  selection: false,
  fields: [
    {
      label: '导出来源',
      name: 'sourceType',
      type: 'number',
            
    },
    {
      label: '用例个数',
      name: 'successfulCount',
      type: 'number',
    },
    {
      label: '导出时间',
      name: 'creationDate',
      type: 'string',
    },
    {
      label: '耗时',
      name: 'during',
      // width: 100,
    },
    {
      label: '进度',
      name: 'rate',

    },
    {
      label: '',
      name: 'fileUrl',
      type: 'string',
    },
  ],
  transport: {
    read: {
      url: '导出',
      method: 'get',
      transformResponse(data) {
        return ({
          ...JSON.parse(data),
        });
      },
    },
  },
});
export default ExportSideDataSet;
