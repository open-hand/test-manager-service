import React, { useMemo } from 'react';
import { omit } from 'lodash';

function TreePlanNode({
  children, onSelect, data, selected, onExpandCollapse,
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

  const Children = useMemo(() => {
    const { onExpand, onCollapse, ...otherOriginProps } = children.props;
    function handleExpand(itemId) {
      onExpandCollapse();
      onExpand(itemId);
    }
    function handleCollapse(itemId) {
      onExpandCollapse();
      onCollapse(itemId);
    }
    const newChildren = omit(children, 'props');
    return React.cloneElement(newChildren, { ...otherOriginProps, onExpand: handleExpand, onCollapse: handleCollapse });
  }, [children, onExpandCollapse]);
  return (
    <div
      role="none"
      onClick={handleClick}
      onKeyDown={(e) => e.isPropagationStopped()}
      className={selected ? 'c7ntest-DragPlanFolder-item-selected' : ''}
    >
      {Children}
    </div>
  );
}
export default TreePlanNode;
