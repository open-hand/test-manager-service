/* eslint-disable react/jsx-no-bind */
import React, {
  useCallback, useRef, useState,
} from 'react';
import {
  Modal,
} from 'choerodon-ui/pro';
import { findIndex, sortBy } from 'lodash';
import { observer } from 'mobx-react-lite';
import { handleRequestFailed } from '@/common/utils';
import { getPlanTreeById, moveFolder } from '@/api/TestPlanApi';
import Tree from '@/components/Tree';
import Loading from '@/components/Loading';
import './index.less';
import TreePlanNode from './TreePlanNode';
import useMultiSelect from './useMultiSelect';

const key = Modal.key();

const propTypes = {

};

function DragPlanFolder({
  data: propsData, planId,
}) {
  const treeRef = useRef();
  const [data, setTreeData] = useState(propsData);
  const [loading, setLoading] = useState(false);
  const [{ selectedNodeMaps, lastSelectNode }, { select }] = useMultiSelect({
    onLoadNode: (searchId) => {
      if (!treeRef.current || !treeRef.current.getItem) {
        return undefined;
      }
      const { item, path } = treeRef.current.getItem(searchId);
      const valueIndex = findIndex(treeRef.current.flattenedTree, (i) => String(i.path) === String(path));

      return { ...item, path, index: valueIndex };
    },
  });
  /**   父节点检查 */
  function handleSelectNode(value, trigger = 'click') {
    if (trigger === 'ctrl') {
      const valueIndex = findIndex(treeRef.current.flattenedTree, (item) => String(item.path) === String(value.path));
      select({ ...value, index: valueIndex });
      return;
    }
    if (trigger === 'shift') { /** 1.  选第一个节点到 第三个  检查父节点是否是选中状态 是的话， 单选去除，多选  */
      if (lastSelectNode) {
        const lastSelectNodeIndex = findIndex(treeRef.current.flattenedTree, (item) => String(item.path) === String(lastSelectNode.path));//  getTreePosition(treeRef.current.treeData, lastSelectNode.path);
        const valueIndex = findIndex(treeRef.current.flattenedTree, (item) => String(item.path) === String(value.path));
        const startIndex = Math.min(lastSelectNodeIndex, valueIndex);
        const originNodes = treeRef.current.flattenedTree.slice(startIndex, Math.max(lastSelectNodeIndex, valueIndex) + 1);
        originNodes.length > 0 && select(originNodes.map(({ item, path }, index) => ({ ...item, path, index: index + startIndex })));
      } else {
        select(value);
      }
    }
    // select(value);
  }
  const renderTreeNode = (treeNode, { item }) => {
    const { id } = item;
    return (
      <TreePlanNode data={item} onSelect={handleSelectNode} onExpandCollapse={() => selectedNodeMaps.clear()} selected={selectedNodeMaps.has(id, item.path)}>
        {treeNode}
      </TreePlanNode>
    );
  };
  const move = useCallback(async (sourceItem, destination) => {
    setLoading(true);
    const folderId = sourceItem.id;
    const { parentId } = destination;
    const { treeData } = treeRef.current;
    const parent = treeData.items[destination.parentId];
    const { index = parent.children.length } = destination;
    let lastId = parent.children[index - 1];
    let nextId = parent.children[index];
    // 解决树的拖拽排序 无法从上往下拖拽排序
    if (sourceItem.parentId === destination.parentId && sourceItem.index < index) {
      lastId = parent.children[index];
      nextId = parent.children.length !== index ? parent.children[index + 1] : null;
    }
    const lastRank = lastId ? treeData.items[lastId].data.rank : null;
    const nextRank = nextId ? treeData.items[nextId].data.rank : null;
    const folderIds = [];
    if (selectedNodeMaps.has(folderId)) {
      const folderList = sortBy(selectedNodeMaps.list(), ['index']);
      // moveItemOnTree(treeRef.treeData);
      const shows = [];
      folderList.forEach((item) => {
        folderIds.push(item.id);
        shows.push(item);
      });
      selectedNodeMaps.clear();
      // return false;
    } else {
      folderIds.push(folderId);
    }

    selectedNodeMaps.clear();
    try {
      const rank = await handleRequestFailed(moveFolder(folderIds, parentId, lastRank, nextRank));
      const planTree = await getPlanTreeById(planId);
      const { rootIds, treeFolder } = planTree;
      const newData = {
        rootIds,
        treeFolder: treeFolder.map((folder) => {
          const {
            issueFolderVO, expanded, children, ...other
          } = folder;
          const oldFolder = treeData.items[folder.id];
          const result = {
            children: children || [],
            data: issueFolderVO,
            isExpanded: expanded || oldFolder?.isExpanded,
            ...other,
          };
          return result;
        }),
      };
      setTreeData(newData);

      setLoading(false);
    } catch (error) {
      setLoading(false);
      return false;
    }
    return true;
  }, [planId, selectedNodeMaps]);
  const handleDrag = useCallback(async (sourceItem, destination) => {
    const res = move(sourceItem, destination);
    return res;
  }, [move]);
  return (
    <div className="c7ntest-DragPlanFolder-content">
      <Loading loading={loading} />
      <Tree
        ref={treeRef}
        data={data}
        selected={{}}
        renderTreeNode={renderTreeNode}
        setSelected={() => { }}
        treeNodeProps={{
          enableAction: false,
        }}
        beforeDrag={handleDrag}
        afterDrag={(s) => s}
      />
    </div>
  );
}
DragPlanFolder.propTypes = propTypes;
const ObserverDragPlanFolder = observer(DragPlanFolder);
export default async function openDragPlanFolder({ planId, handleOk, beforeOpen }) {
  beforeOpen([planId]);
  const planTree = await getPlanTreeById(planId);
  const { rootIds, treeFolder } = planTree;
  const data = {
    rootIds,
    treeFolder: treeFolder.map((folder) => {
      const {
        issueFolderVO, expanded, children, ...other
      } = folder;
      const result = {
        children: children || [],
        data: issueFolderVO,
        isExpanded: expanded,
        ...other,
      };
      return result;
    }),
  };
  Modal.open({
    title: '调整计划结构',
    key,
    drawer: true,
    className: 'c7ntest-DragPlanFolder',
    style: {
      width: 340,
      padding: 0,
    },
    children: <ObserverDragPlanFolder data={data} planId={planId} />,
    okText: '关闭',
    footer: (okBtn) => okBtn,
    onOk: handleOk,
  });
}
