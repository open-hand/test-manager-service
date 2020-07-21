import React, { useState } from 'react';
import { observer } from 'mobx-react-lite';
import IssueStore from '../../stores/IssueStore';
import IssueTreeStore from '../../stores/IssueTreeStore';

let task;
export function getTask() {
  return task;
}
function TreeNode({ children, item }) {
  const [dragEnter, setDragEnter] = useState(false);
  const hasChildren = item.children && item.children.length > 0;
  const canDrop = !hasChildren && IssueStore.tableDraging;
  // mouseup和onDragEnd之间有几秒的时间差，会导致dragEnd不执行，这里mouseUp等dragEnd之后再调用mouseup
  // https://github.com/atlassian/react-beautiful-dnd/issues/180
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
          task = null;
        },
        onMouseUp: (e) => {
          e.persist();
          task = () => handleMouseUp(e);
        },
      } : {}}
    >
      {children}
    </div>
  );
}
export default observer(TreeNode);
