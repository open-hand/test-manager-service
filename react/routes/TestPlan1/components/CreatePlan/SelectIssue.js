import React, { useEffect, useCallback, useRef } from 'react';
import { Button } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import Tree from '@/components/Tree';
import SelectIssueStore from './SelectIssueStore';
import IssueTable from './IssueTable';
import CheckBox from './CheckBox';
import { autoSelect } from './utils';

import './SelectIssue.scss';

const prefix = 'c7ntest-TestPlan-SelectIssue';
function SelectIssue() {
  const dataSetRef = useRef();
  const { currentCycle, treeData, treeMap } = SelectIssueStore;
  const { id: folderId } = currentCycle;
  useEffect(() => {
    SelectIssueStore.loadIssueTree();
  }, []);
  const saveDataSet = useCallback((dataSet) => {
    dataSetRef.current = dataSet;
  }, []);
  const setSelected = useCallback((item) => {
    SelectIssueStore.setCurrentCycle(item);
  }, []);
  const handleCheckChange = useCallback((checked, item) => {
    SelectIssueStore.handleCheckChange(checked, item.id);
    const dataSet = dataSetRef.current;
    // 如果选中，跑一遍自动选中
    if (checked && dataSet) {
      if (folderId === item.id) {
        dataSet.selectAll();
      } else {
        autoSelect(dataSet, treeMap);
      }
    } else if (!checked && dataSet) {
      if (folderId === item.id) {
        dataSet.unSelectAll();
      } else {
        autoSelect(dataSet, treeMap);
      }
    }
  }, [folderId, treeMap]);
  const renderTreeNode = useCallback((node, { item }) => (
    <div className={`${prefix}-TreeNode`}>
      <CheckBox
        item={treeMap.get(item.id)}
        onChange={handleCheckChange}
      />
      <div style={{ flex: 1, overflow: 'hidden' }}>
        {node}
      </div>
    </div>
  ), [handleCheckChange, treeMap]);
  const log = () => {
    // eslint-disable-next-line no-console
    console.log(SelectIssueStore.getSelectedFolders());
  };
  return (
    <div className={prefix}>
      <div className={`${prefix}-tree`}>
        <Tree
          data={treeData}
          isDragEnabled={false}
          selected={currentCycle}
          setSelected={setSelected}
          enableAction={false}
          renderTreeNode={renderTreeNode}
        />
      </div>
      {folderId && (
      <div className={`${prefix}-table`}>
        <div className={`${prefix}-table-title`}>{currentCycle.data.name}</div>
        <IssueTable folderId={folderId} saveDataSet={saveDataSet} />
      </div>
      )}
      <Button onClick={log}>查看结果</Button>
    </div>
  );
}
export default observer(SelectIssue);
