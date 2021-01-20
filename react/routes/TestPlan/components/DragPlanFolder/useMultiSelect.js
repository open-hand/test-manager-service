import { useMemo, useReducer, useState } from 'react';
import { toJS } from 'mobx';
/**
 *
 *
 *
 */
const defaultConfig = { primaryKey: 'id', onLoadNode: () => undefined };
function useMultiSelect({ primaryKey = 'id', onLoadNode } = defaultConfig) {
  function handleOperationNode(key, maps = new Map(), operationType = 'del', { localValue } = { localValue: undefined }) {
    if (operationType === 'del') {
      const delItem = localValue || maps.get(key);
      if (delItem) {
        const delItemParentId = delItem.data.parentId;
        const delItemChildren = [...(delItem.children || [])];
        // 如果父节点有选中，则代表此节点没有加入maps， 更新兄弟节点，兄弟节点状态为选中
        if (maps.has(delItemParentId)) {
          const delItemParent = maps.get(delItemParentId);
          maps.delete(delItemParentId);
          delItemParent.children.forEach((item) => {
            item !== key && handleOperationNode(item, maps, 'add');
          });
        }
        // 更新子节点状态 删除
        delItemChildren.forEach((childrenKey) => {
          handleOperationNode(childrenKey, maps, 'del');
        });
      }
      maps.delete(key);
    }
    if (operationType === 'add' && !maps.has(key)) {
      const addItem = localValue || onLoadNode(key);
      if (!addItem || maps.has(addItem.data.parentId)) {
        return maps;
      }
      // 增添完成后 判断是否有子节点，即此父节点下的所有子节点 全部删除
      maps.set(key, addItem);
      const addItemPathMinLength = addItem.path.length;
      const addItemPathParentIndex = addItem.path[addItemPathMinLength - 1];
      // 更新父节点状态
      // ………………
      maps.forEach((value, childrenKey, selfMaps) => {
        if (value.path.length > addItemPathMinLength && value.path[addItemPathMinLength - 1] === addItemPathParentIndex) {
          selfMaps.delete(childrenKey);
        }
      });
    }
    return maps;
  }
  const [{ lastSelectNode, selectedNodeMaps: originSelectedNodeMaps }, dispatch] = useReducer((state, action) => {
    const { type, ...otherData } = action;
    switch (type) {
      case 'add': {
        const {
          key, value, values,
        } = otherData;
        const newMaps = new Map(state.selectedNodeMaps.entries());
        let newLastSelectNode = value;
        if (values && Array.isArray(values)) {
          values.forEach((itemValue = {}) => {
            const iKey = itemValue[primaryKey];
            handleOperationNode(iKey, newMaps, 'add', { addValue: itemValue });
          });
          newLastSelectNode = values[values.length - 1];
        } else if (typeof (key) !== 'undefined') {
          if (handleCheckHasNode(key, value.path, newMaps)) {
            // 已选则删除
            handleOperationNode(key, newMaps, 'del', { localValue: value });
          } else {
            handleOperationNode(key, newMaps, 'add', { localValue: value });
          }
        }
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
    set: (key, value) => dispatch({ type: 'add', key, value }),
    delete: (key) => dispatch({ key }),
    clear: () => dispatch({ type: 'clear' }),
    list: () => [...originSelectedNodeMaps.values()],
    get: (key) => originSelectedNodeMaps.get(key),
    has: handleCheckHasNode,
    originSelectedNodeMaps,
  };

  /**
   * 多选 不去重  单选去重
   * @param {*} value
   */
  const handleSelect = (value = {}) => {
    if (Array.isArray(toJS(value))) {
      dispatch({ type: 'add', values: value });
    } else {
      dispatch({
        type: 'add', key: value[primaryKey], value,
      });
    }
  };

  return [{ selectedNodeMaps, lastSelectNode }, { select: handleSelect }];
}
export default useMultiSelect;
