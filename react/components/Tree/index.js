/* eslint-disable react/require-default-props */
import React, {
  useState, useEffect, useCallback, useMemo, useImperativeHandle, forwardRef, Fragment,
} from 'react';
import PropTypes from 'prop-types';
import { find, pickBy } from 'lodash';
import { Choerodon } from '@choerodon/boot';
import { useControllableValue } from 'ahooks';
import Tree, {
  mutateTree,
  moveItemOnTree,
} from '@atlaskit/tree';
import { flattenTree } from '@atlaskit/tree/dist/cjs/utils/tree';
import { getItemById } from '@atlaskit/tree/dist/cjs/utils/flat-tree';
import { Modal } from 'choerodon-ui/pro';
import useAvoidClosure from '@/hooks/useAvoidClosure';
import TreeNode from './TreeNode';
import {
  selectItemWithExpand, usePrevious, removeItem, addItem, createItem, expandTreeBySearch, getItemByPosition, getRootNode, getSiblingOrParent, findParent,
} from './utils';
import FilterInput from './FilterInput';
import './index.less';

const PADDING_PER_LEVEL = 16;
const prefix = 'c7ntest-tree';
function callFunction(prop, ...args) {
  if (typeof prop === 'function') {
    return prop(...args);
  }
  return prop;
}
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
          name: 'root',
        },
      },
    },
  };
  treeFolder.forEach((folder) => {
    treeData.items[folder.id] = folder;
  });
  return treeData;
}
const propTypes = {
  data: PropTypes.shape({
    rootIds: PropTypes.arrayOf(PropTypes.number),
    treeFolder: PropTypes.arrayOf({}),
  }),
  onCreate: PropTypes.func,
  onEdit: PropTypes.func,
  onDelete: PropTypes.func,
  afterDrag: PropTypes.func,
  beforeDrag: PropTypes.func,
  selected: PropTypes.shape({
    id: PropTypes.number,
  }),
  setSelected: PropTypes.func,
  renderTreeNode: PropTypes.func,
  searchAutoFilter: PropTypes.bool,

};
const defaultProps = {

};
function PureTree({
  data,
  onCreate,
  onEdit,
  onDelete,
  afterDrag,
  beforeDrag,
  selected,
  setSelected,
  updateItem,
  renderTreeNode,
  treeNodeProps,
  onMenuClick,
  getDeleteTitle,
  searchAutoFilter = false,
  ...restProps
}, ref) {
  const [tree, setTree] = useState(mapDataToTree(data));
  useEffect(() => {
    setTree(mapDataToTree(data));
  }, [data]);
  const [search, setSearch] = useControllableValue(restProps, {
    defaultValue: '',
    valuePropName: 'search',
    trigger: 'onSearchChange',
  });
  const previous = usePrevious(selected);
  const filteredTree = useMemo(() => {
    if (!search || !searchAutoFilter) {
      return tree;
    }
    const filtered = { ...tree, items: {} };
    const matchedMap = new Map();
    const setMatch = (itemId) => {
      matchedMap.set(itemId, true);
      const item = tree.items[itemId];
      if (item.children) {
        item.children.forEach((childId) => {
          setMatch(childId);
        });
      }
      let current = findParent(tree, itemId);
      while (current) {
        matchedMap.set(current.id, true);
        current = findParent(tree, current.id);
      }
    };
    Object.keys(tree.items).forEach((key) => {
      if (tree.items[key].isMatch) {
        setMatch(key);
      }
    });
    Object.keys(tree.items).forEach((key) => {
      if (matchedMap.has(key)) {
        const item = { ...tree.items[key] };
        if (item.children) {
          item.children = item.children.slice().filter((c) => matchedMap.has(c));
        }
        filtered.items[key] = item;
      }
    });
    return filtered;
  }, [search, searchAutoFilter, tree]);
  const flattenedTree = useMemo(() => flattenTree(filteredTree), [filteredTree]);

  useEffect(() => {
    setTree((oldTree) => selectItemWithExpand(oldTree, selected ? selected.id : undefined, previous ? previous.id : undefined));
  }, [previous, selected]);
  const addFirstLevelItem = (item) => {
    if (getRootNode(tree).children.includes('new')) {
      return;
    }
    const newChild = {
      id: 'new',
      parentId: 0, // 放入父id，方便创建时读取
      rootNode: true,
      children: [],
      hasChildren: false,
      isExpanded: false,
      isChildrenLoading: false,
      isEditing: true,
      data: {
        name: '',
      },
    };
    setTree((oldTree) => addItem(oldTree, getRootNode(oldTree), item || newChild));
  };
  const updateTree = useCallback((itemId, attrs) => {
    setTree((oldTree) => mutateTree(oldTree, itemId, attrs));
  }, []);
  const getItem = useCallback((itemId) => getItemById(flattenedTree, itemId), [flattenedTree]);
  const handleDelete = useCallback((item) => {
    Modal.confirm({
      title: getDeleteTitle ? callFunction(getDeleteTitle, item).split('|')[0] : '确认删除目录',
      children: getDeleteTitle ? callFunction(getDeleteTitle, item).split('|')[1] : undefined,
      okText: '删除',
      okProps: {
        color: 'red',
      },
    }).then(async (button) => {
      if (button === 'ok') {
        try {
          await onDelete(item);
          setTree((oldTree) => removeItem(oldTree, item.path));
          if (selected.id === item.id) {
            let target;
            // 这里用旧的tree获取目标id，用新tree获取数据
            setTree((newTree) => {
              target = getSiblingOrParent(tree, newTree, item);
              return newTree;
            });
            if (target) {
              setSelected(target);
            }
          }
        } catch (error) {
          Choerodon.prompt(error.message);
        }
      }
    });
  }, [getDeleteTitle, onDelete, selected.id, setSelected, tree]);
  useImperativeHandle(ref, () => ({
    addFirstLevelItem,
    updateTree,
    getItem,
    treeData: tree,
    flattenedTree,
    trigger: {
      delete: handleDelete,
    },
  }));
  const onSelect = (item) => {
    setSelected(item);
  };
  const filterTree = useCallback((value) => {
    setTree((oldTree) => expandTreeBySearch(oldTree, value || ''));
    setSearch(value || '');
  }, []);
  const onExpand = (itemId) => {
    setTree((oldTree) => mutateTree(oldTree, itemId, { isExpanded: true }));
  };

  const onCollapse = (itemId) => {
    setTree((oldTree) => mutateTree(oldTree, itemId, { isExpanded: false }));
  };

  const onDragEnd = async (
    source,
    destination,
  ) => {
    if (!destination) {
      return;
    }
    const sourceItem = getItemByPosition(tree, source);
    const destinationParent = tree.items[destination.parentId];
    // 不能拖动到已经有issue的目录下
    if (destinationParent.hasCase) {
      return;
    }
    try {
      const parent = getItemById(flattenedTree, destinationParent.id);
      if (parent && parent.path.length >= 9) {
        return;
      }
      const blockDrag = beforeDrag && await beforeDrag(sourceItem, destination);
      if (typeof (blockDrag) !== 'undefined' && (!!blockDrag) === false) {
        return;
      }
      setTree((oldTree) => moveItemOnTree(oldTree, source, destination));
      const newItem = await afterDrag(sourceItem, destination);
      setTree((oldTree) => mutateTree(oldTree, sourceItem.id, { ...sourceItem, ...newItem }));
    } catch (error) {
      console.log(error);
      setTree((oldTree) => moveItemOnTree(oldTree, destination, source));
    }
  };

  const handleMenuClick = useCallback((node, { key }) => {
    switch (key) {
      case 'rename': {
        setTree((oldTree) => mutateTree(oldTree, node.id, { isEditing: true }));
        break;
      }
      case 'delete': {
        handleDelete(node);
        break;
      }
      case 'add': {
        const newChild = {
          id: 'new',
          parentId: node.id, // 放入父id，方便创建时读取
          children: [],
          hasChildren: false,
          isExpanded: false,
          isChildrenLoading: false,
          isEditing: true,
          data: {
            name: undefined,
          },
        };
        if (find(tree.items, { id: 'new' })) {
          return;
        }
        setTree((oldTree) => addItem(oldTree, node, newChild));
        break;
      }
      default:
        onMenuClick(key, node);
        break;
    }
  }, [handleDelete, onMenuClick, tree.items]);
  const handleCreate = async (value, path, item) => {
    if (value.trim()) {
      try {
        const newItem = await onCreate(value, item.parentId, item);
        if (newItem) {
          setTree((oldTree) => createItem(oldTree, path, {
            ...newItem,
            children: [],
            hasChildren: false,
            isExpanded: false,
            isChildrenLoading: false,
            isEditing: false,
          }));
          setTree((newTree) => {
            if (updateItem && selected.id === item.parentId && selected.children.length === 0) {
              updateItem(newTree.items[selected.id]);
            }
            return newTree;
          });
        }
      } catch (error) {
        setTree((oldTree) => removeItem(oldTree, path));
      }
    } else {
      setTree((oldTree) => removeItem(oldTree, path));
    }
  };
  const handleEdit = useAvoidClosure(async (value, item) => {
    // 值未变，或为空，不编辑，还原
    if (!value.trim() || value === item.data.name) {
      setTree((oldTree) => mutateTree(oldTree, item.id, { isEditing: false }));
    } else {
      try {
        const newItem = await onEdit(value, item);
        if (newItem) {
          setTree((oldTree) => mutateTree(oldTree, item.id, { ...item, ...newItem, isEditing: false }));
          setTree((newTree) => {
            if (updateItem && selected.id === item.id) {
              updateItem(newTree.items[selected.id]);
            }
            return newTree;
          });
        }
      } catch (error) {
        setTree((oldTree) => mutateTree(oldTree, item.id, { isEditing: false }));
      }
    }
  });
  const renderItem = ({
    item, provided,
  }) => {
    const { path } = getItemById(flattenedTree, item.id);
    const treeNode = (
      <TreeNode
        key={item.id}
        path={path}
        provided={provided}
        item={item}
        onExpand={onExpand}
        onCollapse={onCollapse}
        onSelect={onSelect}
        onMenuClick={handleMenuClick}
        onCreate={handleCreate}
        onEdit={handleEdit}
        search={search}
        {...treeNodeProps}
      />
    );
    return renderTreeNode ? renderTreeNode(treeNode, { item: { ...item, path } }) : treeNode;
  };

  const isEmpty = flattenedTree.length === 0;
  return (
    <div className={prefix}>
      <>
        <div className={`${prefix}-top`}>
          <FilterInput
            value={search}
            onChange={filterTree}
          />
        </div>
        {isEmpty ? (
          <div className={`${prefix}-empty`}>
            暂无数据
          </div>
        ) : (
          <div className={`${prefix}-scroll`}>
            <Tree
              tree={filteredTree}
              renderItem={renderItem}
              onExpand={onExpand}
              onCollapse={onCollapse}
              onDragEnd={onDragEnd}
              offsetPerLevel={PADDING_PER_LEVEL}
              isDragEnabled
              isNestingEnabled
              {...restProps}
            />
          </div>
        )}
      </>
    </div>
  );
}
PureTree.propTypes = propTypes;
PureTree.defaultProps = defaultProps;
export default forwardRef(PureTree);
