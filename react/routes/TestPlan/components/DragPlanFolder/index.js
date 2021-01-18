import React, {
  useCallback, useMemo, useRef, useState,
} from 'react';
import {
  Modal,
} from 'choerodon-ui/pro';
import { findIndex, sortBy } from 'lodash';
import { observer } from 'mobx-react-lite';
import { handleRequestFailed } from '@/common/utils';
import { getPlanTreeById, moveFolder } from '@/api/TestPlanApi';
import Tree from '@/components/Tree';
import './index.less';
import { moveItemOnTree } from '@atlaskit/tree';
import { getTreePosition } from '@atlaskit/tree/dist/cjs/utils/tree';
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
  const handleDrag = useCallback(async (sourceItem, destination) => {
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
    // const rank = '111';
    console.log('...', selectedNodeMaps.list(), treeRef.current);
    const folderIds = [];
    if (selectedNodeMaps.has(folderId)) {
      const folderList = sortBy(selectedNodeMaps.list(), ['index']);
      // moveItemOnTree(treeRef.treeData);
      const shows = [];
      folderList.forEach((item) => {
        folderIds.push(item.id);
        shows.push(item);
        // const pos = getTreePosition(treeRef.current.treeData, item.path);
        // const newTreeData = moveItemOnTree(treeRef.current.treeData, pos, destination);
      });
      console.log('select nodes:', shows);
      console.log('select folder names:', shows.map((item) => item.data.name), shows.map((item) => item.index));

      // const rank = await handleRequestFailed(moveFolder(folderIds, parentId, lastRank, nextRank));
      selectedNodeMaps.clear();
      // return false;
    } else {
      folderIds.push(folderId);
    }

    selectedNodeMaps.clear();
    try {
      const rank = await handleRequestFailed(moveFolder(folderIds, parentId, lastRank, nextRank));
      // if (Array.isArray(rank) && rank.length === 1) {
      //   return {
      //     data: {
      //       ...sourceItem.data,
      //       rank: rank[0].rank,
      //     },
      //   };
      // }
      const planTree = await getPlanTreeById(planId);
      const { rootIds, treeFolder } = planTree;
      const newData = {
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
      setTreeData(newData);

      return false;
    } catch (error) {
      return false;
    }
    // return {
    //   data: {
    //     ...sourceItem.data,
    //     rank: rank[0].rank,
    //   },
    // };
  }, [planId, selectedNodeMaps]);
  return (
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
    />
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
