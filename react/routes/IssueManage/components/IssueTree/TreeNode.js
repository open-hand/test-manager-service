import React, { useState } from 'react';
import { observer } from 'mobx-react-lite';
import {
  Button, Menu, Dropdown, Tooltip, Icon,
} from 'choerodon-ui/pro/lib';
import { Progress } from 'choerodon-ui';
import { SmartTooltip } from '@/components';
import IssueStore from '../../stores/IssueStore';
import IssueTreeStore from '../../stores/IssueTreeStore';

let task;
export function getTask() {
  return task;
}
const prefix = 'c7ntest-tree';
function TreeNode({
  children, nodeProps, onMenuClick, item, 
}) {
  const { provided } = nodeProps;

  const [dragEnter, setDragEnter] = useState(false);
  const hasChildren = item.children && item.children.length > 0;
  const canDrop = !hasChildren && IssueStore.tableDraging;
  // mouseup和onDragEnd之间有几秒的时间差，会导致dragEnd不执行，这里mouseUp等dragEnd之后再调用mouseup
  // https://github.com/atlassian/react-beautiful-dnd/issues/180
  const handleMouseUp = async (e) => {
    setDragEnter(false);
    const isCopy = e.ctrlKey || e.metaKey;
    await IssueStore.moveOrCopyIssues(item.id, isCopy);
    IssueTreeStore.updateHasCase(item.id, true);
  };
  if (item.data.initStatus === 'creating') {
    return (
      <div
        ref={provided.innerRef}

        {...provided.draggableProps}
        {...provided.dragHandleProps}
      >
        <div
          className={`${prefix}-tree-item-wrapper`}
        >
          <div
            role="none"
          
            className={`${prefix}-tree-item`}
          >
            <span className={`${prefix}-tree-item-prefix`}>
              <Icon type="folder_open" className="c7ntest-tree-icon-primary" style={{ marginRight: 5, marginLeft: 22 }} />
            </span>
            <span className={`${prefix}-tree-item-title`} style={{ color: 'rgba(0,0,0,0.54)' }}><SmartTooltip title={item.data.name}>{item.data.name}</SmartTooltip></span>
            <Progress type="loading" size="small" />
          </div>
        </div>
      </div>
    );
  }
  if (item.data.initStatus === 'fail') {
    return (
      <div
        ref={provided.innerRef}
        {...provided.draggableProps}
        {...provided.dragHandleProps}
      >
        <div
          className={`${prefix}-tree-item-wrapper`}
        >
          <div
            role="none"
       
            className={`${prefix}-tree-item`}
          >
            <span className={`${prefix}-tree-item-prefix`}>
              <Icon type="folder_open" className="c7ntest-tree-icon-primary" style={{ marginRight: 5, marginLeft: 22 }} />
            </span>
            <span className={`${prefix}-tree-item-title`} style={{ color: 'rgba(0,0,0,0.54)' }}><SmartTooltip title={item.data.name}>{item.data.name}</SmartTooltip></span>
            <Tooltip title="复制失败">
              <Icon type="error" style={{ color: 'red' }} />
            </Tooltip>
            <Dropdown
              overlay={(
                <Menu onClick={({ key }) => { onMenuClick(key, item); }}>
                  <Menu.Item key="delete">
                    删除
                  </Menu.Item>
                </Menu>
              )}
              trigger={['click']}
              getPopupContainer={trigger => trigger.parentNode}
            >
              <Button funcType="flat" icon="more_vert" size="small" />
            </Dropdown>
          </div>
        </div>
      </div>
    );
  }
  return (
    <div
      style={{ border: dragEnter ? '2px dashed green' : '' }}
      role="none"
      {...canDrop ? {
        onMouseEnter: () => {
          setDragEnter(true);
        },
        onMouseLeave: () => {
          setDragEnter(false);
          task = null;
        },
        onMouseUp: (e) => {
          e.persist();
          task = () => handleMouseUp(e);
        },
      } : {}}
    >
      {children}

    </div>
  );
}
export default observer(TreeNode);
