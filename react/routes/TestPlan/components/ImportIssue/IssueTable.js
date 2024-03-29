import React, { useMemo, useContext, useEffect } from 'react';
import { DataSet, Table } from 'choerodon-ui/pro';
import { usePersistFn } from 'ahooks';
import { getProjectId } from '@/common/utils';
import priorityApi from '@/api/priority';

import PriorityTag from '@/components/PriorityTag';

import Context from './context';
import { autoSelect } from './utils';

const { Column } = Table;

function IssueTable({
  folderId, saveDataSet,
}) {
  const handleSelect = usePersistFn(({ record }) => {
    // 禁用说明已经选上了，不处理
    if (record.get('hasDisable')) {
      return;
    }
    const source = record.get('source');
    // 如果是自动选中的，不做处理
    if (source === 'auto') {
      record.set('source', undefined);
      return;
    }
    record.setState('current_isSelected', true);
    const caseId = record.get('caseId');
    const caseFolderId = record.get('folderId');
    // 选中树
    SelectIssueStore.handleCheckChange(true, caseFolderId);
    SelectIssueStore.addFolderSelectedCase(caseFolderId, caseId);
  });
  const handleUnSelect = usePersistFn(({ record }) => {
    const caseId = record.get('caseId');
    const caseFolderId = record.get('folderId');
    record.setState('current_isSelected', false);
    SelectIssueStore.removeFolderSelectedCase(caseFolderId, caseId);
  });
  const { SelectIssueStore, planId } = useContext(Context);
  const { treeMap } = SelectIssueStore;
  const priorityDs = useMemo(() => new DataSet({
    autoCreate: false,
    autoQuery: false,
  }), []);
  useEffect(() => {
    priorityApi.load().then((res) => {
      priorityDs.loadData(res?.map((item) => ({ meaning: item.name, value: item.id })));
    });
  }, [priorityDs]);
  const dataSet = useMemo(() => new DataSet({
    primaryKey: 'caseId',
    autoQuery: true,
    selection: 'multiple',
    transport: {
      read: {
        url: `/test/v1/projects/${getProjectId()}/case/list_by_folder_id?folder_id=${folderId}&plan_id=${planId}`,
        method: 'post',
        transformRequest: (data) => {
          const {
            params, summary, caseNum, priorityId,
          } = data;
          return JSON.stringify({
            contents: params ? [params] : [],
            searchArgs: {
              summary,
              caseNum,
              priorityId,

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
        // const selectedFolders = SelectIssueStore.getSelectedFolders();
        // // 有禁用且选中的项时，全选之后会无法取消全选，这里做一些判断
        // const shouldUnSelectAll = dataSet.some((record) => {
        //   // eslint-disable-next-line no-shadow
        //   const folderId = record.get('folderId');
        //   if (record.selectable && selectedFolders[folderId]) {
        //     const folder = selectedFolders[folderId];
        //     if (!folder.custom || folder.selected.includes(record.get('id'))) {
        //       return true;
        //     }
        //   }
        //   return false;
        // });
        // if (shouldUnSelectAll) {
        //   dataSet.unSelectAll();
        // } else if (dataSet.length > 0) {
        //   SelectIssueStore.handleCheckChange(true, folderId);
        // }
        dataSet.forEach((record) => { !record.getState('current_isSelected') && handleSelect({ record }); });
      },
      unSelectAll: () => {
        // SelectIssueStore.handleCheckChange(false, folderId);
        dataSet.forEach((record) => handleUnSelect({ record }));
      },
    },
    fields: [
      { name: 'summary', type: 'string', label: '用例名称' },
      { name: 'caseNum', type: 'string', label: '用例编号' },
      { name: 'folderName', type: 'string', label: '目录' },
      { name: 'priorityVO', type: 'string', label: '优先级' },
    ],
    queryFields: [
      { name: 'summary', type: 'string', label: '用例名称' },
      { name: 'caseNum', type: 'string', label: '用例编号' },
      {
        name: 'priorityId', type: 'string', label: '优先级', options: priorityDs,
      },
    ],
  }), [SelectIssueStore, folderId, planId, treeMap, priorityDs]);
  // 让父组件访问dataSet
  saveDataSet(dataSet);
  return (
    <div style={{ height: 450, width: 701 }}>
      <Table dataSet={dataSet} autoHeight>
        <Column name="summary" />
        <Column name="caseNum" />
        <Column name="priorityVO" width={70} renderer={({ record }) => <PriorityTag priority={record.get('priorityVO')} />} />
        <Column name="folderName" />

      </Table>
    </div>

  );
}

export default IssueTable;
