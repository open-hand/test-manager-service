import React, { useCallback, useRef } from 'react';
import {
  Modal,
} from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { handleRequestFailed } from '@/common/utils';
import { getPlanTreeById, moveFolder } from '@/api/TestPlanApi';
import Tree from '@/components/Tree';
import './index.scss';

const key = Modal.key();

const propTypes = {

};

function DragPlanFolder({
  data, 
}) {
  const treeRef = useRef();
  const handleDrag = useCallback(async (sourceItem, destination) => {
    const folderId = sourceItem.id;
    const { parentId } = destination; 
    const { treeData } = treeRef.current;   
    const parent = treeData.items[destination.parentId];
    const { index = parent.children.length } = destination;
    const lastId = parent.children[index - 1];
    const nextId = parent.children[index];    
    const lastRank = lastId ? treeData.items[lastId].data.rank : null;
    const nextRank = nextId ? treeData.items[nextId].data.rank : null;   
    const rank = await handleRequestFailed(moveFolder(folderId, parentId, lastRank, nextRank));
    return {
      data: {
        ...sourceItem.data,
        rank,
      },
    };
  }, []);
  return (
    <Tree
      ref={treeRef}
      data={data}
      selected={{}}
      setSelected={() => { }}
      treeNodeProps={{
        enableAction: false,
      }}
      afterDrag={handleDrag}
    />
  );
}
DragPlanFolder.propTypes = propTypes;
const ObserverDragPlanFolder = observer(DragPlanFolder);
export default async function openDragPlanFolder({ planId, handleOk }) {
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
    children: <ObserverDragPlanFolder data={data} />,
    okText: '关闭',
    footer: okBtn => okBtn,
    onOk: handleOk,
  });
}
