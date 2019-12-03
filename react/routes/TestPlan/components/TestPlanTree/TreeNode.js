import React from 'react';
import { observer } from 'mobx-react-lite';
import { Progress, Icon } from 'choerodon-ui/pro';
import SmartTooltip from '@/components/SmartTooltip';

const prefix = 'c7ntest-tree';
function TreeNode({ children, nodeProps, item }) {
  const { provided } = nodeProps;
  if (item.data.initStatus === 'doing') {
    console.log(item.data.initStatus);
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
              <Icon type="insert_invitation" style={{ marginRight: 5, marginLeft: 21 }} />
            </span>
            <span className={`${prefix}-tree-item-title`}><SmartTooltip title={item.data.name}>{item.data.name}</SmartTooltip></span>
            <Progress type="loading" size="small" />
          </div>
        </div>
      </div>
    );
  }
  return (
    <div
      role="none"
    >
      {children}
    </div>
  );
}
export default observer(TreeNode);
