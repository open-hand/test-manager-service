import { mutateTree } from '@atlaskit/tree';
import { getTreePosition, getParent } from '@atlaskit/tree/dist/cjs/utils/tree';
import { useEffect, useRef, useCallback } from 'react';

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
export function selectItemWithExpand(tree, id, previous) {
  let newTree = tree;
  newTree = selectItem(newTree, id, previous);
  Object.keys(tree.items).forEach((itemId) => {
    const item = tree.items[itemId];    
    if (item.children && item.children.length > 0) {
      const hasChildMatch = item.children.some((childId) => {
        const child = tree.items[childId];
        return child.id === id;
      });
      // 自动展开
      if (hasChildMatch && !item.isExpanded) {
        newTree = mutateTree(newTree, itemId, { isExpanded: true });
      }
    }
  });

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
    if (item.data.name.indexOf(search) > -1) {
      newTree = mutateTree(newTree, itemId, { isMatch: true });
    }
    if (item.children && item.children.length > 0) {
      const hasChildMatch = item.children.some((childId) => {
        const child = tree.items[childId];
        return child.data.name.indexOf(search) > -1;
      });
      // 自动展开
      if (hasChildMatch && !item.isExpanded) {
        newTree = mutateTree(newTree, itemId, { isExpanded: true });
        // 自动折叠
      } else if (!hasChildMatch && item.isExpanded) {
        newTree = mutateTree(newTree, itemId, { isExpanded: false });
      }
    }
  });

  return newTree;
}
// 根据id获取数据
export function getItem(tree, id) {
  return tree.items[id];
}
export const usePrevious = (value) => {
  const ref = useRef();
  useEffect(() => {
    ref.current = value;
  });
  return ref.current;
};
