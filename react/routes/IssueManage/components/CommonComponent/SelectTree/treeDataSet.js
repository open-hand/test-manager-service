import { DataSet } from 'choerodon-ui/pro/lib';
import { useMemo } from 'react';

/**
 * 
 * @param {*} pDataSet  控制Select下拉框 DataSet
 */
const treeDataSet = (pDataSet, name) => new DataSet({
  primaryKey: 'id',
  paging: false,
  autoQuery: true,
  selection: 'single',
  parentField: 'parentId',
  expandField: 'expand',
  idField: 'id',
  fields: [
    { name: 'fileName', type: 'string' },
    { name: 'fileId', type: 'number' },
    { name: 'id', type: 'number' },
    { name: 'expand', type: 'boolean' },
    { name: 'parentId', type: 'number' },
  ],
  data: [
    { fileName: '文件夹1', id: 1, expand: false },
    { fileName: '文件夹2', id: 2, expand: true },
    { fileName: '文件夹3', id: 3, expand: false },
    { fileName: '文件夹4', id: 4, expand: false },
    { fileName: '文件夹5', id: 5, expand: false },
    { fileName: '文件夹6', id: 6, expand: false },
    { fileName: '文件夹7', id: 7, expand: false },
    {
      fileName: '文件夹12', id: 12, expand: false, parentId: 1,
    },
    {
      fileName: '文件夹14', id: 14, expand: false, parentId: 1,
    },

  ],
  events: {
    select: ({ record, dataSet }) => {
    //   console.log('selected', record, { fileName: record.get('fileName'), fileId: record.get('id') }, pDataSet);
      dataSet.select(record);
      pDataSet.current.set(name, { fileName: record.get('fileName'), fileId: record.get('id') });
    },
    // unSelect: ({ record, dataSet }) => console.log('unSelect', record),
  },
});

export default treeDataSet;
