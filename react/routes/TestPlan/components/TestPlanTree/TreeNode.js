import React from 'react';
import { observer } from 'mobx-react-lite';
import {
  Progress, Icon, Button, Tooltip,
} from 'choerodon-ui/pro';
import { Menu, Dropdown } from 'choerodon-ui';
import SmartTooltip from '@/components/SmartTooltip';

const prefix = 'c7ntest-tree';
function TreeNode({
  children, nodeProps, item, onMenuClick,
}) {
  const { provided } = nodeProps;
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
              <Icon type="insert_invitation" style={{ marginRight: 5, marginLeft: 22 }} />
            </span>
            <span className={`${prefix}-tree-item-title`} style={{ color: 'var(--text-color3)' }}><SmartTooltip title={item.data.name}>{item.data.name}</SmartTooltip></span>
            <Tooltip title="创建计划需要一定的时间，请手动刷新">
              <Icon type="error" style={{ color: 'var(--text-color3)' }} />
            </Tooltip>
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
              <Icon type="insert_invitation" style={{ marginRight: 5, marginLeft: 22 }} />
            </span>
            <span className={`${prefix}-tree-item-title`} style={{ color: 'var(--text-color3)' }}><SmartTooltip title={item.data.name}>{item.data.name}</SmartTooltip></span>
            <Tooltip title="创建计划失败">
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
              getPopupContainer={(trigger) => trigger.parentNode}
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
      role="none"
    >
      {children}
    </div>
  );
}
export default observer(TreeNode);
