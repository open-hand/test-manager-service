import React, { useState, useEffect } from 'react';
import Tree, {
  mutateTree,
  moveItemOnTree,
} from '@atlaskit/tree';
import TreeNode from './TreeNode';
import { treeWithTwoBranches } from './treeWithTwoBranches';
import { selectItem, usePrevious } from './utils';
import './index.less';

const PADDING_PER_LEVEL = 16;
const prefix = 'c7nIssueManage-Tree';
export default function PureTree() {
  const [tree, setTree] = useState(treeWithTwoBranches);
  const [selected, setSelected] = useState();
  const previous = usePrevious(selected);
  useEffect(() => {
    if (selected) {
      // console.log(selected);
      setTree(oldTree => selectItem(oldTree, selected, previous));
    }
  }, [selected]);
  const onSelect = (itemId) => {
    // console.log('select', itemId)
    setSelected(itemId);
  };


  const onExpand = (itemId) => {
    setTree(oldTree => mutateTree(oldTree, itemId, { isExpanded: true }));
  };

  const onCollapse = (itemId) => {
    setTree(oldTree => mutateTree(oldTree, itemId, { isExpanded: false }));
  };

  const onDragEnd = (
    source,
    destination,
  ) => {
    if (!destination) {
      return;
    }
    const newTree = moveItemOnTree(tree, source, destination);
    setTree(newTree);
  };

  const renderItem = ({
    item, provided,
  }) => (
    <TreeNode
      provided={provided}
      item={item}
      onExpand={onExpand}
      onCollapse={onCollapse}
      onSelect={onSelect}
    />
  );
  return (
    <div className={prefix}>
      <Tree
        tree={tree}
        renderItem={renderItem}
        onExpand={onExpand}
        onCollapse={onCollapse}
        onDragEnd={onDragEnd}
        offsetPerLevel={PADDING_PER_LEVEL}
        isDragEnabled
        isNestingEnabled
      />
    </div>
  );
}
