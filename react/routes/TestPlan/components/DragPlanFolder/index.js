import React, { useCallback } from 'react';
import {
  Modal,
} from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { handleRequestFailed } from '@/common/utils';
import { getPlanTreeById, editFolder } from '@/api/TestPlanApi';
import Tree from '@/components/Tree';
import './index.scss';

const key = Modal.key();

const propTypes = {

};

function DragPlanFolder({
  treeData, 
}) {
  const handleDrag = useCallback(async (sourceItem, destination) => { 
    const { objectVersionNumber, name } = sourceItem.data;
    const data = {
      cycleName: name,
      cycleId: sourceItem.id,
      parentCycleId: Number(destination.parentId),
      objectVersionNumber,
    };
    const result = await handleRequestFailed(editFolder(data));
    return {
      data: {
        ...sourceItem.data,      
        objectVersionNumber: result.objectVersionNumber,
      },
    };
  }, []);
  return (
    <Tree
      data={treeData}
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
  const treeData = {
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
    children: <ObserverDragPlanFolder treeData={treeData} />,
    okText: '关闭',
    footer: okBtn => okBtn,
    onOk: handleOk,
  });
}
