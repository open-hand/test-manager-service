import React, { useMemo, useContext } from 'react';
import { DataSet, Table } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { getProjectId } from '@/common/utils';
import PriorityTag from '@/components/PriorityTag';
import { usePersistFn } from 'ahooks';
import Context from './context';
import { autoSelect } from './utils';

const { Column } = Table;

function IssueTable({
  folderId, saveDataSet,
}) {
  const handleSelect = usePersistFn(({ record }) => {
    const source = record.get('source');
    // 如果是自动选中的，不做处理
    if (source === 'auto') {
      record.set('source', undefined);
      return;
    }
    const caseId = record.get('caseId');
    const caseFolderId = record.get('folderId');
    // 选中树
    SelectIssueStore.handleCheckChange(true, caseFolderId);
    SelectIssueStore.addFolderSelectedCase(caseFolderId, caseId);
  });
  const handleUnSelect = usePersistFn(({ record }) => {
    const caseId = record.get('caseId');
    const caseFolderId = record.get('folderId');
    SelectIssueStore.removeFolderSelectedCase(caseFolderId, caseId);
  });
  const { SelectIssueStore } = useContext(Context);
  const { treeMap } = SelectIssueStore;
  const dataSet = useMemo(() => new DataSet({
    primaryKey: 'caseId',
    autoQuery: true,
    selection: 'multiple',
    transport: {
      read: {
        url: `/test/v1/projects/${getProjectId()}/case/list_by_folder_id?folder_id=${folderId}`,
        method: 'post',
        transformRequest: (data) => {
          const {
            params, summary, caseNum, customNum,
          } = data;
          return JSON.stringify({
            contents: params ? [params] : [],
            searchArgs: {
              summary,
              caseNum,
              customNum,
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
      select: handleSelect,
      unSelect: handleUnSelect,
      selectAll: () => {
        if (dataSet.length > 0) {
          console.log('all');
          // SelectIssueStore.handleCheckChange(true, folderId);
          dataSet.forEach((record) => handleSelect({ record }));
        }
      },
      unSelectAll: () => {
        // SelectIssueStore.handleCheckChange(false, folderId);
        dataSet.forEach((record) => handleUnSelect({ record }));
      },
    },
    fields: [
      { name: 'summary', type: 'string', label: '用例名称' },
      { name: 'caseNum', type: 'string', label: '用例编号' },
      { name: 'customNum', type: 'string', label: '自定义编号' },
      { name: 'priorityVO', type: 'string', label: '优先级' },
      { name: 'folderName', type: 'string', label: '目录' },
    ],
    queryFields: [
      { name: 'summary', type: 'string', label: '用例名称' },
      { name: 'caseNum', type: 'string', label: '用例编号' },
      { name: 'customNum', type: 'string', label: '自定义编号' },
    ],
  }), [SelectIssueStore, folderId, treeMap]);
  // 让父组件访问dataSet
  saveDataSet(dataSet);
  return (
    <div style={{ height: 450, width: 700 }}>
      <Table
        dataSet={dataSet}
        style={{ height: 384 }}
      >
        <Column name="summary" className="c7n-agile-table-cell" tooltip="overflow" />
        <Column name="caseNum" className="c7n-agile-table-cell" width={100} />
        <Column name="customNum" className="c7n-agile-table-cell" width={100} />
        <Column name="priorityVO" width={70} renderer={({ record }) => <PriorityTag priority={record.get('priorityVO')} />} />
        <Column name="folderName" className="c7n-agile-table-cell" />
      </Table>
    </div>

  );
}

export default observer(IssueTable);
