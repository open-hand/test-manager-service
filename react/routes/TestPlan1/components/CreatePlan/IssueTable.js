import React, { useMemo } from 'react';
import { DataSet, Table } from 'choerodon-ui/pro';
import { getProjectId } from '@/common/utils';
import SelectIssueStore from './SelectIssueStore';
import { autoSelect } from './utils';

const { Column } = Table;

function IssueTable({
  folderId, saveDataSet,
}) {
  const { treeMap } = SelectIssueStore;
  const dataSet = useMemo(() => new DataSet({
    primaryKey: 'caseId',
    autoQuery: true,
    selection: 'multiple',
    cacheSelection: true,
    transport: {
      read: {
        url: `/test/v1/projects/${getProjectId()}/case/list_by_folder_id?folder_id=${folderId}`,
        method: 'post',
        transformRequest: (data) => {
          // console.log(data);
          const { summary, caseNum } = data;
          return JSON.stringify({
            // contents:
            searchArgs: {
              summary,
              caseNum,
            },
          });
        },
      },
    },
    events: {
      // 数据加载完后，自动选中
      load: ({ dataSet: ds }) => {
        autoSelect(ds, treeMap);
      },
      select: ({ record }) => {
        const source = record.get('source');
        // 如果是自动选中的，不做处理
        if (source === 'auto') {
          record.set('source', undefined);
          return;
        }
        // console.log('select');
        SelectIssueStore.handleCheckChange(true, record.get('folderId'));
      },
      unSelect: ({ record }) => {
        // console.log('unselect');
      },
      selectAll: () => {
        // console.log('selectAll');
        SelectIssueStore.handleCheckChange(true, folderId);
      },
      unSelectAll: () => {
        // console.log('unSelectAll');
        SelectIssueStore.handleCheckChange(false, folderId);
      },
    },
    fields: [
      { name: 'summary', type: 'string', label: '用例名称' },
      { name: 'caseNum', type: 'string', label: '用例编号' },
      { name: 'folder', type: 'string', label: '文件夹' },
    ],
    queryFields: [
      { name: 'summary', type: 'string', label: '用例名称' },
      { name: 'caseNum', type: 'string', label: '用例编号' },
    ],
  }), [folderId, treeMap]);
  // 让父组件访问dataSet
  saveDataSet(dataSet);
  return (
    <Table dataSet={dataSet} labelLayout="none">
      <Column name="summary" />
      <Column name="caseNum" />
      <Column name="folder" />
    </Table>
  );
}

export default IssueTable;
