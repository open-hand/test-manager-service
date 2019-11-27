import { mutateTree } from '@atlaskit/tree';
import { getTreePosition } from '@atlaskit/tree/dist/cjs/utils/tree';
import { useEffect, useRef } from 'react';

const hasLoadedChildren = item => !!item.hasChildren && item.children.length > 0;

const isLeafItem = item => !item.hasChildren;
export function getRootNode(tree) {
  return tree.items['0'];
}
// 选中一项
export function selectItem(tree, id, previous) {
  let newTree = tree;
  if (previous) {
    // const previousItem = tree.items[previous];
    newTree = mutateTree(newTree, previous, { selected: false });
  }
  if (id) {
    newTree = mutateTree(newTree, id, { selected: true });
  }
  return newTree;
}
function findParent(tree, id) {
  const keys = Object.keys(tree.items);
  for (const key of keys) {
    const item = tree.items[key];   
    if (item.children.includes(id)) {
      return item;
    }
  }
  return null;
}
function autoExpandParent(tree, id) {
  let newTree = tree;
  let parent = findParent(tree, id);
  while (parent) {
    if (!parent.isExpanded) {
      newTree = mutateTree(newTree, parent.id, { isExpanded: true });
    }
    parent = findParent(tree, parent.id);
  }
  return newTree;
}
export function selectItemWithExpand(tree, id, previous) {  
  let newTree = tree;
  newTree = selectItem(newTree, id, previous);
  newTree = autoExpandParent(newTree, id);
  return newTree;
}
// 从树中删除一项
export function removeItem(
  tree,
  path,
) {
  const position = getTreePosition(tree, path);
  const sourceParent = tree.items[position.parentId];
  const newSourceChildren = [...sourceParent.children];
  const childId = newSourceChildren[position.index];
  const itemRemoved = newSourceChildren.splice(position.index, 1)[0];
  // eslint-disable-next-line no-param-reassign
  delete tree.items[childId];
  const newTree = mutateTree(tree, position.parentId, {
    children: newSourceChildren,
    hasChildren: newSourceChildren.length > 0,
    isExpanded: newSourceChildren.length > 0 && sourceParent.isExpanded,
  });

  return newTree;
}
// 往树中添加一项
export function addItem(
  tree,
  parent,
  item,
) {
  const position = {
    parentId: parent.id,
    index: 0,
  };
  const itemId = item.id;
  const newDestinationChildren = [...parent.children];
  if (typeof position.index === 'undefined') {
    if (hasLoadedChildren(parent) || isLeafItem(parent)) {
      newDestinationChildren.push(itemId);
    }
  } else {
    newDestinationChildren.splice(position.index, 0, itemId);
  }
  // eslint-disable-next-line no-param-reassign
  tree.items[itemId] = item;
  return mutateTree(tree, position.parentId, {
    children: newDestinationChildren,
    hasChildren: true,
    isExpanded: true,
  });
}
export function getItemByPosition(tree, position) {
  const id = tree.items[position.parentId].children[position.index];
  return tree.items[id];
}
// 更新自身，并且更新父元素的children里的id
export function createItem(tree, path, item) {
  // 先移除临时的
  let newTree = tree;
  newTree = removeItem(newTree, path);
  const position = getTreePosition(newTree, path);
  // 再添加新的
  const sourceParent = newTree.items[position.parentId];
  newTree = addItem(newTree, sourceParent, item);
  return newTree;
}
// 根据搜索的值，展开父元素
export function expandTreeBySearch(tree, search) {
  let newTree = tree;
  Object.keys(tree.items).forEach((itemId) => {
    const item = tree.items[itemId];
    // 更新数据，使tree的组件会更新
    if (search && item.data.name.indexOf(search) > -1) {
      newTree = mutateTree(newTree, itemId, { isMatch: true });
      // 展开父级
      newTree = autoExpandParent(newTree, item.id);
    } else if (item.isMatch) {
      newTree = mutateTree(newTree, itemId, { isMatch: false });
    }
  });

  return newTree;
}

export const usePrevious = (value) => {
  const ref = useRef();
  useEffect(() => {
    ref.current = value;
  });
  return ref.current;
};
