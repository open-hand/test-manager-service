import React, { useCallback } from 'react';
import {
  Modal,
} from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { handleRequestFailed } from '@/common/utils';
import { getPlanTreeById, moveFolders } from '@/api/TestPlanApi';
import Tree from '@/components/Tree';
import './index.scss';
import test from './test.json';

const key = Modal.key();

const propTypes = {

};

function DragPlanFolder({
  treeData,
}) {
  const handleDrag = useCallback((sourceItem, destination) => {
    // handleRequestFailed(moveFolders([sourceItem.id], destination.parentId));
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
export default async function openDragPlanFolder({ planId }) {
  // const planTree = await getPlanTreeById(planId);
  const planTree = test;
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
  });
}
