import React, {
  useState, useEffect, useCallback, useMemo,
} from 'react';
import Tree, {
  mutateTree,
  moveItemOnTree,
} from '@atlaskit/tree';
import { flattenTree } from '@atlaskit/tree/dist/cjs/utils/tree';
import { getItemById } from '@atlaskit/tree/dist/cjs/utils/flat-tree';
import TreeNode from './TreeNode';
import { treeWithTwoBranches } from './treeWithTwoBranches';
import {
  selectItem, usePrevious, removeItem, addItem, createItem, expandTreeBySearch,
} from './utils';
import FilterInput from './FilterInput';
import './index.less';

const PADDING_PER_LEVEL = 16;
const prefix = 'c7nIssueManage-Tree';
export default function PureTree() {
  const [tree, setTree] = useState(treeWithTwoBranches);
  const [selected, setSelected] = useState();
  const [search, setSearch] = useState('');
  const previous = usePrevious(selected);
  const flattenedTree = useMemo(() => flattenTree(tree), [tree]);
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
  const filterTree = useCallback((value) => {
    setTree(oldTree => expandTreeBySearch(oldTree, value || ''));
    setSearch(value || '');
  }, [tree]);

  const getItem = id => tree.items[id];
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
    const { parentId: targetId } = destination;
    const item = getItem(targetId);
    // console.log(item);
    const newTree = moveItemOnTree(tree, source, destination);
    setTree(newTree);
  };
  const handleMenuClick = useCallback((node, { item, key, keyPath }) => {
    switch (key) {
      case 'rename': {
        // console.log('rename', node);
        setTree(oldTree => mutateTree(oldTree, node.id, { isEditing: true }));
        break;
      }
      case 'delete': {
        // console.log('delete', node);
        setTree(oldTree => removeItem(oldTree, node.path));
        break;
      }
      case 'add': {
        // console.log('add', node);
        const newChild = {
          id: 'new',
          children: [],
          hasChildren: false,
          isExpanded: false,
          isChildrenLoading: false,
          isEditing: true,
          data: {
            title: '新的',
          },
        };
        setTree(oldTree => addItem(oldTree, node, newChild));
        break;
      }
      default: break;
    }
  }, [tree]);
  const handleCreate = async (value, path) => {
    if (value.trim()) {
      // await  
      setTree(oldTree => createItem(oldTree, path, {
        id: Math.random(),
        data: { title: value },
        children: [],
        hasChildren: false,
        isExpanded: false,
        isChildrenLoading: false,
      }));
    } else {
      setTree(oldTree => removeItem(oldTree, path));
    }
  };
  const handleEdit = (value, item) => {
    // 值未变，或为空，不编辑，还原
    if (!value.trim() || value === item.data.title) {
      setTree(oldTree => mutateTree(oldTree, item.id, { isEditing: false }));
    } else {
      // await
      setTree(oldTree => mutateTree(oldTree, item.id, { isEditing: false, data: { title: value } }));
    }
  };
  const renderItem = ({
    item, provided,
  }) => (
    <TreeNode
      path={getItemById(flattenedTree, item.id).path}
      provided={provided}
      item={item}
      onExpand={onExpand}
      onCollapse={onCollapse}
      onSelect={onSelect}
      onMenuClick={handleMenuClick}
      onCreate={handleCreate}
      onEdit={handleEdit}
      search={search}
    />
  );
  // console.log(flattenedTree);
  return (
    <div className={prefix}>
      <FilterInput
        onChange={filterTree}
      />
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
