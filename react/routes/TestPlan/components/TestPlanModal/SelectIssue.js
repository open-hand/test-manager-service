import React, {
  useEffect, useCallback, useRef, useContext,
} from 'react';
import { observer } from 'mobx-react-lite';
import Tree from '@/components/Tree';
import Context from './context';
import IssueTable from './IssueTable';
import CheckBox from './CheckBox';
import { autoSelect } from './utils';

import './SelectIssue.less';

const prefix = 'c7ntest-TestPlan-SelectIssue';
function SelectIssue() {
  const dataSetRef = useRef();
  const { SelectIssueStore } = useContext(Context);
  const { currentCycle, treeData, treeMap } = SelectIssueStore;
  const { id: folderId } = currentCycle;
  const saveDataSet = useCallback((dataSet) => {
    dataSetRef.current = dataSet;
  }, []);
  const setSelected = useCallback((item) => {
    SelectIssueStore.setCurrentCycle(item);
  }, [SelectIssueStore]);
  const handleCheckChange = useCallback((checked, item) => {
    SelectIssueStore.handleCheckChange(checked, item.id);
    const dataSet = dataSetRef.current;
    // 如果选中，跑一遍自动选中
    if (checked && dataSet) {
      if (folderId === item.id) {
        // dataSet.selectAll();
        autoSelect(dataSet, treeMap);
      } else {
        autoSelect(dataSet, treeMap);
      }
    } else if (!checked && dataSet) {
      if (folderId === item.id) {
        // dataSet.unSelectAll();
        autoSelect(dataSet, treeMap);
      } else {
        autoSelect(dataSet, treeMap);
      }
    }
  }, [SelectIssueStore, folderId, treeMap]);
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
      <div className={`${prefix}-header`}>
        已添加用例:
        <span style={{ fontSize: '16px', color: '#3F51B5', marginLeft: 5 }}>
          {SelectIssueStore.getSelectedIssueNum}
        </span>
        条
      </div>
      <div className={`${prefix}-content`}>
        <div className={`${prefix}-tree`}>
          <Tree
            data={treeData}
            isDragEnabled={false}
            selected={currentCycle}
            setSelected={setSelected}
            treeNodeProps={{
              enableAction: false,
            }}
            renderTreeNode={renderTreeNode}
          />
        </div>
        {folderId && (
          <div className={`${prefix}-table`}>
            <div className={`${prefix}-table-title`}>{currentCycle.data.name}</div>
            <IssueTable folderId={folderId} saveDataSet={saveDataSet} />
          </div>
        )}
      </div>
    </div>
  );
}
export default observer(SelectIssue);
