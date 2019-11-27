import React, { useEffect, useCallback } from 'react';
import { observer } from 'mobx-react-lite';
import Tree from '@/components/Tree';
import SelectIssueStore from './SelectIssueStore';
import IssueTable from './IssueTable';
import CheckBox from './CheckBox';
import './SelectIssue.scss';

const prefix = 'c7ntest-TestPlan-SelectIssue';
function SelectIssue() {
  const { currentCycle, treeData, treeMap } = SelectIssueStore;
  const { id: folderId } = currentCycle;
  useEffect(() => {
    SelectIssueStore.loadIssueTree();
  }, []);
  const setSelected = useCallback((item) => {
    SelectIssueStore.setCurrentCycle(item);
  }, []);
  const handleCheckChange = useCallback((checked, item) => {
    SelectIssueStore.handleCheckChange(checked, item);
  }, []);
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
          <div className={`${prefix}-table-title`}>敏捷管理</div>
          <IssueTable folderId={folderId} />
        </div>
      )}
    </div>
  );
}
export default observer(SelectIssue);
