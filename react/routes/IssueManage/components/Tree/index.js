import React, {
  useState, useEffect, useCallback, useMemo, useImperativeHandle, forwardRef,
} from 'react';
import Tree, {
  mutateTree,
  moveItemOnTree,
} from '@atlaskit/tree';
import { flattenTree, getTreePosition } from '@atlaskit/tree/dist/cjs/utils/tree';
import { getItemById } from '@atlaskit/tree/dist/cjs/utils/flat-tree';
import { getRootNode } from './utils';
import TreeNode from './TreeNode';

import {
  selectItem, usePrevious, removeItem, addItem, createItem, expandTreeBySearch, getItemByPosition,
} from './utils';
import FilterInput from './FilterInput';
import './index.less';

const PADDING_PER_LEVEL = 16;
const prefix = 'c7nIssueManage-Tree';
function mapDataToTree(data) {
  const { rootIds, treeFolder } = data;
  const treeData = {
    rootId: '0',
    items: {
      0: {
        id: '0',
        children: rootIds, // 一级目录
        hasChildren: false,
        isExpanded: true,
        isChildrenLoading: false,
        data: {
          title: 'root',
        },
      },
    },
  };
  treeFolder.forEach((folder) => {
    treeData.items[folder.id] = folder;
  });
  return treeData;
}
function PureTree({
  data,
  onCreate,
  afterDrag,
}, ref) {
  const [tree, setTree] = useState(mapDataToTree(data));
  useEffect(() => {
    setTree(mapDataToTree(data));
  }, [data]);
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
  const addFirstLevelItem = () => {
    const newChild = {
      id: 'new',
      parentId: 0, // 放入父id，方便创建时读取
      children: [],
      hasChildren: false,
      isExpanded: false,
      isChildrenLoading: false,
      isEditing: true,
      data: {
        name: '新的',
      },
    };
    setTree(oldTree => addItem(oldTree, getRootNode(oldTree), newChild));
  };
  useImperativeHandle(ref, () => ({
    addFirstLevelItem,
  }));
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

  const onDragEnd = async (
    source,
    destination,
  ) => {
    if (!destination) {
      return;
    }
    const { parentId: targetId } = destination;
    const sourceItem = getItemByPosition(tree, source);
    // console.log(source, destination);
    setTree(oldTree => moveItemOnTree(oldTree, source, destination));
    try {
      await afterDrag(sourceItem, destination);
    } catch (error) {
      setTree(oldTree => moveItemOnTree(oldTree, destination, source));
    }
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
          parentId: node.id, // 放入父id，方便创建时读取
          children: [],
          hasChildren: false,
          isExpanded: false,
          isChildrenLoading: false,
          isEditing: true,
          data: {
            name: '新的',
          },
        };
        setTree(oldTree => addItem(oldTree, node, newChild));
        break;
      }
      default: break;
    }
  }, [tree]);
  const handleCreate = async (value, path, item) => {
    if (value.trim()) {
      try {
        const newItem = await onCreate(value, item.parentId);
        setTree(oldTree => createItem(oldTree, path, {
          id: Math.random(),
          data: { name: value },
          children: [],
          hasChildren: false,
          isExpanded: false,
          isChildrenLoading: false,
        }));
      } catch (error) {
        setTree(oldTree => removeItem(oldTree, path));
      }
    } else {
      setTree(oldTree => removeItem(oldTree, path));
    }
  };
  const handleEdit = (value, item) => {
    // 值未变，或为空，不编辑，还原
    if (!value.trim() || value === item.data.name) {
      setTree(oldTree => mutateTree(oldTree, item.id, { isEditing: false }));
    } else {
      // await
      setTree(oldTree => mutateTree(oldTree, item.id, { isEditing: false, data: { name: value } }));
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
export default forwardRef(PureTree);
