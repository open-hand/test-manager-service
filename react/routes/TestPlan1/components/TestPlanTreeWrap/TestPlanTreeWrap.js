import React, { Component, useState, useContext } from 'react';
import { observer } from 'mobx-react-lite';
import TestPlanTreeToggle from '../TestPlanTreeToggle';
import Store from '../../stores';

function getParentKey(key) { return key.split('-').slice(0, -1).join('-'); }

export default observer((props) => {
  const { testPlanStore } = useContext(Store);
  const { dataList } = testPlanStore;
  const { onTreeNodeSelect } = props;
  const [treeSearchValue, setTreeSearchValue] = useState('');
  const [autoExpandParent, setAutoExpandParent] = useState(true);

  const filterCycle = (value) => {
    if (value !== '') {
      const expandedKeys = dataList.map((item) => {
        if (item.title.indexOf(value) > -1) {
          return getParentKey(item.key);
        }
        return null;
      }).filter((item, i, self) => item && self.indexOf(item) === i);
      testPlanStore.setExpandedKeys(expandedKeys);
    }
    setTreeSearchValue(value);
    setAutoExpandParent(true);
  };

  const handleTreeNodeExpand = (expandedKeys) => {
    testPlanStore.setExpandedKeys(expandedKeys);
    setAutoExpandParent(true);
  };

  const {
    loading, treeData, selectedKeys, expandedKeys, 
  } = testPlanStore;

  return (
    <div className="c7ntest-testPlan-tree">
      <TestPlanTreeToggle
        loading={loading}
        filterCycle={filterCycle}
        treeData={treeData}
        treeSearchValue={treeSearchValue}
        selectedKeys={selectedKeys}
        expandedKeys={expandedKeys}
        onTreeNodeExpand={handleTreeNodeExpand}
        onTreeNodeSelect={onTreeNodeSelect}
        autoExpandParent={autoExpandParent}
      />
    </div>
  );
});
