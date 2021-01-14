import { useMemo, useReducer, useState } from 'react';
import { toJS } from 'mobx';
/**
 *
 *
 *1-2--
 */
const defaultConfig = { primaryKey: 'id', onLoadNode: () => undefined, onFilterNode: (key) => undefined };
function useMultiSelect({ primaryKey = 'id', onLoadNode, onFilterNode } = defaultConfig) {
  const selectedPaths = useMemo(() => new Array(10).fill([]), []);
  const indeterminateNodes = useMemo(() => new Set(), []);
  function handleFilterNode(parentKey, maps = new Map(), operationType = 'del', { addValue, deleteValue, ignoreKeys = [] } = { addValue: undefined, deleteValue: undefined }) {
    if (operationType === 'del') {
      const delItem = maps.get(parentKey) || deleteValue;
      if (delItem) {
        const delItemParentId = delItem.data.parentId;
        const delItemChildren = [...(delItem.children || [])];

        // 如果父节点有选中，则代表此节点没有加入maps， 更新父兄弟节点，兄弟节点状态
        maps.has(delItemParentId) && handleFilterNode(delItemParentId, maps, 'update-parent-other', { ignoreKeys: [parentKey] });
        maps.delete(parentKey);
        console.log('del...', delItemChildren, delItem);
        // 更新子节点状态 增添
        delItemChildren.forEach((childrenKey) => {
          handleFilterNode(childrenKey, maps, 'del');
        });
      }
      maps.delete(parentKey);
      // return delItem ? handleFilterNode(delItemParentId, maps, 'del') : maps;
    }
    if (operationType === 'add' && !maps.has(parentKey)) {
      const addItem = addValue || onLoadNode(parentKey);
      console.log('add ...', addItem);
      if (!addItem || maps.has(addItem.data.parentId)) {
        return maps;
      }
      // 增添完成后 判断是否有子节点，即此父节点下的所有子节点 全部删除
      maps.set(parentKey, addItem);
      const addItemPathMinLength = addItem.path.length;
      const addItemPathParentIndex = addItem.path[addItemPathMinLength - 1];
      // 更新父节点状态
      // ………………
      maps.forEach((value, key, selfMaps) => {
        if (value.path.length > addItemPathMinLength && value.path[addItemPathMinLength - 1] === addItemPathParentIndex) {
          selfMaps.delete(key);
        }
      });
    }
    if (operationType === 'update-parent-other' && maps.has(parentKey)) {
      const parentItem = maps.get(parentKey);
      const addItems = [...(parentItem.children || [])];
      maps.delete(parentKey);

      // debugger;
      addItems.forEach((item) => {
        !ignoreKeys.some((i) => i === item) && handleFilterNode(item, maps, 'add');
      });
    }
    return maps;
  }
  const [{ lastSelectNode, selectedNodeMaps: originSelectedNodeMaps }, dispatch] = useReducer((state, action) => {
    const { type, ...otherData } = action;
    switch (type) {
      case 'add': {
        const {
          key, value, values, uniq,
        } = otherData;
        const newMaps = new Map(state.selectedNodeMaps.entries());
        let newLastSelectNode = value;
        if (values && Array.isArray(values)) {
          values.forEach((itemValue = {}) => {
            const iKey = itemValue[primaryKey];
            handleFilterNode(iKey, newMaps, 'add', { addValue: itemValue });
            // handleCheckHasNode(iKey, itemValue.path, newMaps) ? handleFilterNode(iKey, newMaps, 'del', { deleteValue: itemValue }) : handleFilterNode(iKey, newMaps, 'add', { addValue: itemValue });
          });
          newLastSelectNode = values[values.length - 1];
        } else if (typeof (key) !== 'undefined') {
          if (handleCheckHasNode(key, value.path, newMaps)) {
            // const deleteItem = newMaps.get(key);
            // 非根节点 则判断父元素有没有选上  有的话 则去掉父节点选择 并选上节点的兄弟节点
            console.log('del dom', value);
            // handleFilterNode(value.data.parentId, newMaps, 'del');
            handleFilterNode(key, newMaps, 'del', { deleteValue: value });
          } else {
            handleFilterNode(key, newMaps, 'add', { addValue: value });
          }
          //  ?  : newMaps.set(key, value);
        }
        console.log('newMaps..', newMaps);
        return { ...state, lastSelectNode: newLastSelectNode, selectedNodeMaps: newMaps };
      }
      case 'clear': {
        return { lastSelectNode: undefined, selectedNodeMaps: new Map() };
      }
      default:
        return state;
    }
  }, {
    lastSelectNode: undefined,
    selectedNodeMaps: new Map(),
  });
  function handleCheckHasNode(key, nodePath = [], maps) {
    const sourceMaps = maps || originSelectedNodeMaps;
    if (sourceMaps.has(key)) {
      return true;
    }
    for (const [, item] of sourceMaps) {
      if (nodePath.length > item.path.length && nodePath[item.path.length - 1] === item.path[item.path.length - 1]) {
        return true;
      }
    }
    return false;
  }
  const selectedNodeMaps = {
    set: (key, value) => dispatch({ key, value }),
    delete: (key) => dispatch({ key }),
    clear: () => dispatch({ type: 'clear' }),
    list: () => [...originSelectedNodeMaps.values()],
    get: (key) => originSelectedNodeMaps.get(key),
    has: (key, nodePath = []) => {
      if (originSelectedNodeMaps.has(key)) {
        return true;
      }
      for (const [, item] of originSelectedNodeMaps) {
        if (nodePath.length > item.path.length && nodePath[item.path.length - 1] === item.path[item.path.length - 1]) {
          return true;
        }
      }
      return false;
    },
    originSelectedNodeMaps,
  };
  const handleSelect = (value = {}) => {
    if (Array.isArray(value)) {
      dispatch({ values: value });
    } else {
      const key = value[primaryKey];
      selectedNodeMaps.set(key, value);
    }
  };
  /**
   * 多选 不去重  单选去重
   * @param {*} value
   */
  const handleUniqSelect = (value = {}) => {
    if (Array.isArray(toJS(value))) {
      dispatch({ type: 'add', values: value });
    } else {
      dispatch({
        type: 'add', key: value[primaryKey], value,
      });
    }
  };

  return [{ selectedNodeMaps, lastSelectNode }, { select: handleUniqSelect }];
}
export default useMultiSelect;
