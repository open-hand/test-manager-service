import React from 'react';

function TreePlanNode({ children }) {
  function handleSelect() {

  }
  function handleClick(e) {
    e.stopPropagation();
    if (!(e.shiftKey && (e.ctrlKey || e.metaKey))) {
      if (e.shiftKey) {
        console.log('shift');
        // this.dealWithMultiSelect(sprintId, item, 'shift');
      } else if (e.ctrlKey || e.metaKey) {
        console.log('ctrl');

        // this.dealWithMultiSelect(sprintId, item, 'ctrl');
      } else {
        console.log('click');

        // handleSelect(sprintId, item, e.shiftKey || e.ctrlKey || e.metaKey);
      }
    }
  }
  return (
    <div
      role="none"
      onClick={handleClick}
    >
      {children}
    </div>
  );
}
export default TreePlanNode;
