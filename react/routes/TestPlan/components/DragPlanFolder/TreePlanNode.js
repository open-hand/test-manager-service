import React from 'react';
import { set } from 'lodash';

function TreePlanNode({
  children, onSelect, data, selected,
}) {
  function handleSelect() {

  }
  function handleClick(e) {
    e.stopPropagation();
    if (!(e.shiftKey && (e.ctrlKey || e.metaKey))) {
      if (e.shiftKey) {
        console.log('shift');
        onSelect(data, 'shift');
        // this.dealWithMultiSelect(sprintId, item, 'shift');
      } else if (e.ctrlKey || e.metaKey) {
        console.log('ctrl');
        onSelect(data, 'ctrl');

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
      onKeyDown={(e) => e.isPropagationStopped()}
      className={selected ? 'c7ntest-DragPlanFolder-item-selected' : ''}
    >
      {children}
    </div>
  );
}
export default TreePlanNode;
