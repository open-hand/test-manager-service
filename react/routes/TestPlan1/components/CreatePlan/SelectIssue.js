import React from 'react';
import Tree from '@/components/Tree';
import './SelectIssue.scss';

const prefix = 'c7ntest-TestPlan-SelectIssue';
function SelectIssue() {
  return (
    <div
      className={prefix}
    >
      ss
      <Tree
        data={{ rootIds: [], treeFolder: [] }}
        isDragEnabled={false}
      />
    </div>
  );
}
export default SelectIssue;
