import React, {
  useState, useEffect, useCallback, useMemo, useImperativeHandle, forwardRef,
} from 'react';
import PropTypes from 'prop-types';
import Tree, {
  mutateTree,
  moveItemOnTree,
} from '@atlaskit/tree';
import { flattenTree } from '@atlaskit/tree/dist/cjs/utils/tree';
import { getItemById } from '@atlaskit/tree/dist/cjs/utils/flat-tree';
import { Modal } from 'choerodon-ui/pro';
import TreeNode from './TreeNode';
import {
  selectItemWithExpand, usePrevious, removeItem, addItem, createItem, expandTreeBySearch, getItemByPosition, getRootNode, getSiblingOrParent,
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
  selected: PropTypes.shape({
    id: PropTypes.number,
  }),
  setSelected: PropTypes.func,
  renderTreeNode: PropTypes.func,

};
const defaultProps = {

};
function PureTree({
  data,
  onCreate,
  onEdit,
  onDelete,
  afterDrag,
  selected,
  setSelected,
  renderTreeNode,
  treeNodeProps,
  onMenuClick,
  getDeleteTitle,
  ...restProps
}, ref) {
  const [tree, setTree] = useState(mapDataToTree(data));
  useEffect(() => {
    setTree(mapDataToTree(data));
  }, [data]);
  const [search, setSearch] = useState('');
  const previous = usePrevious(selected);
  const flattenedTree = useMemo(() => flattenTree(tree), [tree]);
  useEffect(() => {
    setTree(oldTree => selectItemWithExpand(oldTree, selected ? selected.id : undefined, previous ? previous.id : undefined));
  }, [previous, selected]);
  const addFirstLevelItem = (item) => {
    if (getRootNode(tree).children.includes('new')) {
      return;
    }
    const newChild = {
      id: 'new',
      parentId: 0, // 放入父id，方便创建时读取
      children: [],
      hasChildren: false,
      isExpanded: false,
      isChildrenLoading: false,
      isEditing: true,
      data: {
        name: '',
      },
    };
    setTree(oldTree => addItem(oldTree, getRootNode(oldTree), item || newChild));
  };
  const updateTree = useCallback((itemId, attrs) => {   
    setTree(oldTree => mutateTree(oldTree, itemId, attrs));
  }, []);
  const getItem = useCallback(itemId => getItemById(flattenedTree, itemId), [flattenedTree]);
  const handleDelete = useCallback((item) => {   
    Modal.confirm({
      title: getDeleteTitle ? callFunction(getDeleteTitle, item) : '确认删除文件夹',
    }).then(async (button) => {
      if (button === 'ok') {
        try {
          await onDelete(item);
          setTree(oldTree => removeItem(oldTree, item.path));
          if (selected.id === item.id) {
            // 这里取旧的tree数据
            setSelected(getSiblingOrParent(tree, item));          
          }
        } catch (error) {
          // console.log(error);
        }
      }
    });
  }, [getDeleteTitle, onDelete, selected.id, setSelected, tree]);
  useImperativeHandle(ref, () => ({
    addFirstLevelItem,
    updateTree,
    getItem,
    trigger: {
      delete: handleDelete,
    },
  }));
  const onSelect = (item) => {
    setSelected(item);
  };
  const filterTree = useCallback((value) => {
    setTree(oldTree => expandTreeBySearch(oldTree, value || ''));
    setSearch(value || '');
  }, []);
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
    const sourceItem = getItemByPosition(tree, source);
    const destinationParent = tree.items[destination.parentId];
    // 不能拖动到已经有issue的文件夹下
    if (destinationParent.hasCase) {
      return;
    }
    setTree(oldTree => moveItemOnTree(oldTree, source, destination));
    try {
      const newItem = await afterDrag(sourceItem, destination);
      setTree(oldTree => mutateTree(oldTree, sourceItem.id, { ...sourceItem, ...newItem }));
    } catch (error) {
      setTree(oldTree => moveItemOnTree(oldTree, destination, source));
    }
  };

  const handleMenuClick = useCallback((node, { key }) => {
    switch (key) {
      case 'rename': {
        setTree(oldTree => mutateTree(oldTree, node.id, { isEditing: true }));
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
        setTree(oldTree => addItem(oldTree, node, newChild));
        break;
      }
      default:
        onMenuClick(key, node);
        break;
    }
  }, [handleDelete, onMenuClick]);
  const handleCreate = async (value, path, item) => {
    if (value.trim()) {
      try {
        const newItem = await onCreate(value, item.parentId, item);
        setTree(oldTree => createItem(oldTree, path, {
          ...newItem,  
          children: [],
          hasChildren: false,
          isExpanded: false,
          isChildrenLoading: false,
          isEditing: false,
        }));
      } catch (error) {
        setTree(oldTree => removeItem(oldTree, path));
      }
    } else {
      setTree(oldTree => removeItem(oldTree, path));
    }
  };
  const handleEdit = async (value, item) => {
    // 值未变，或为空，不编辑，还原
    if (!value.trim() || value === item.data.name) {
      setTree(oldTree => mutateTree(oldTree, item.id, { isEditing: false }));
    } else {
      try {
        const newItem = await onEdit(value, item);     
        setTree(oldTree => mutateTree(oldTree, item.id, { ...item, ...newItem, isEditing: false }));
      } catch (error) {
        setTree(oldTree => mutateTree(oldTree, item.id, { isEditing: false }));
      }
    }
  };
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
  return (
    <div className={prefix}>
      <div className={`${prefix}-top`}>
        <FilterInput
          onChange={filterTree}
        />
      </div>
      <div className={`${prefix}-scroll`}>
        <Tree
          tree={tree}
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
    </div>
  );
}
PureTree.propTypes = propTypes;
PureTree.defaultProps = defaultProps;
export default forwardRef(PureTree);
