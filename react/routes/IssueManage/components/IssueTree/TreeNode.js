import React, { useState } from 'react';
import { observer } from 'mobx-react-lite';
import IssueStore from '../../stores/IssueStore';
import IssueTreeStore from '../../stores/IssueTreeStore';

function TreeNode({ children, item }) {
  const [dragEnter, setDragEnter] = useState(false);
  const hasChildren = item.children && item.children.length > 0;
  const canDrop = !hasChildren && IssueStore.tableDraging;
  const handleMouseUp = async (e) => {
    setDragEnter(false);
    const isCopy = e.ctrlKey || e.metaKey;
    await IssueStore.moveOrCopyIssues(item.id, isCopy);
    IssueTreeStore.updateHasCase(item.id, true);
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
