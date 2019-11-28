import React, { useState, useContext } from 'react';
import { observer } from 'mobx-react-lite';
import IssueStore from '../../stores/IssueTreeStore';
import Store from '../../stores';

function TreeNode({ children, item }) {
  console.log(children, item);
  const { testPlanStore } = useContext(Store);
  const [dragEnter, setDragEnter] = useState(false);
  const hasChildren = item.children && item.children.length > 0;
  const canDrop = !hasChildren && IssueStore.tableDraging;
  const handleMouseUp = (e) => {
    setDragEnter(false);
    const isCopy = e.ctrlKey || e.metaKey;
    IssueStore.moveOrCopyIssues(item.id, isCopy);
  };
  return (
    <div
      style={{ border: dragEnter ? '2px dashed green' : '' }}
      role="none"
      {...canDrop ? {
        onMouseEnter: () => {
          setDragEnter(true);
        },
        onMouseLeave: () => {
          setDragEnter(false);
        },
        onMouseUp: handleMouseUp,
      } : {}}
    >
      {children}
    </div>
  );
}
export default observer(TreeNode);
