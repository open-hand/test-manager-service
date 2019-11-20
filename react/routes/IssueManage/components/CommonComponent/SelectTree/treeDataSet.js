import { DataSet } from 'choerodon-ui/pro/lib';
import { useMemo } from 'react';// ../../common/utils
import { getProjectId } from '../../../../../common/utils';

/**
 * 
 * @param {*} pDataSet  控制Select下拉框 DataSet
 */
const treeDataSet = (pDataSet, name) => new DataSet({
  primaryKey: 'folderId',
  paging: false,
  autoQuery: true,
  selection: 'single',
  parentField: 'parentId',
  expandField: 'expanded',
  idField: 'folderId',
  fields: [
    { name: 'name', type: 'string' },
    { name: 'folderId', type: 'number' },
    { name: 'expand', type: 'boolean' },
    { name: 'parentId', type: 'number' },
    { name: 'versionId', type: 'number' },
  ],
  // data: [
  //   { fileName: '文件夹1', id: 1, expand: false },
  //   { fileName: '文件夹2', id: 2, expand: true },
  //   { fileName: '文件夹3', id: 3, expand: false },
  //   { fileName: '文件夹4', id: 4, expand: false },
  //   { fileName: '文件夹5', id: 5, expand: false },
  //   { fileName: '文件夹6', id: 6, expand: false },
  //   { fileName: '文件夹7', id: 7, expand: false },
  //   {
  //     fileName: '文件夹12', id: 12, expand: false, parentId: 1,
  //   },
  //   {
  //     fileName: '文件夹14', id: 14, expand: false, parentId: 1,
  //   },
  //   //

  // ],
  transport: {
    read: () => ({
      url: `/test/v1/projects/${getProjectId()}/issueFolder/query`,
      method: 'get',
      transformResponse: (res) => {
        const newArr = JSON.parse(res).treeFolder.map(item => ({
          expanded: item.expanded,
          ...item.issueFolderVO,
        }));
        return newArr;
      },
    }),
  },
  events: {
    select: ({ record, dataSet }) => {
      // console.log('selected', record);
      dataSet.select(record);
      pDataSet.current.set(name, { fileName: record.get('name'), folderId: record.get('folderId'), versionId: record.get('versionId') });
    },
    // unSelect: ({ r  ecord, dataSet }) => console.log('unSelect', record),
  },
});

export default treeDataSet;
