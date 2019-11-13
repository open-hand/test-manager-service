import React from 'react';
import styled from 'styled-components';
import classNames from 'classnames';
import {
  Icon, Button, Menu, Dropdown, 
} from 'choerodon-ui/pro';

const PreTextIcon = styled.span`
  display: inline-block;
  visibility: hidden;
  width: 22px;
  justify-content: center;
  cursor: pointer;
`;
const menu = (
  <Menu>
    <Menu.Item>
      重命名
    </Menu.Item>
    <Menu.Item>
      删除
    </Menu.Item>
  </Menu>
);
const prefix = 'c7nIssueManage-Tree';
const getIcon = (
  item,
  onExpand,
  onCollapse,
) => {
  const exapndIcon = (
    <Icon
      type="baseline-arrow_right"
      className={classNames(`${prefix}-icon`, { [`${prefix}-icon-expanded`]: item.isExpanded })}
      onClick={(e) => {
        e.stopPropagation();
        if (item.isExpanded) {
          onCollapse(item.id);
        } else {
          onExpand(item.id);
        }        
      }}
    />
  );
  const folderIcon = (
    <Icon
      type={item.isExpanded ? 'folder_open2' : 'folder_open'}
      className={`${prefix}-icon-folder ${prefix}-icon-primary`}
    />
  );
  if (item.children && item.children.length > 0) {
    return [exapndIcon, folderIcon];
  }
  return [<PreTextIcon>&bull;</PreTextIcon>, folderIcon];
};
const getAction = item => (
  <div role="none" onClick={(e) => { e.stopPropagation(); }} className={`${prefix}-tree-item-action`}>
    <Icon type="create_new_folder" style={{ marginRight: 6 }} />
    <Dropdown overlay={menu} trigger="click" getPopupContainer={trigger => trigger.parentNode}>
      <Button funcType="flat" icon="more_vert" />
    </Dropdown>
  </div>
);

export default function TreeNode(props) {
  const {
    provided, onSelect, item, onExpand, onCollapse, 
  } = props;

  return (
    <div
      role="none"
      className={classNames(`${prefix}-tree-item`, { [`${prefix}-tree-item-selected`]: item.selected })}
      onClick={() => { onSelect(item.id); }}
      ref={provided.innerRef}
      {...provided.draggableProps}
      {...provided.dragHandleProps}
    >
      <span className={`${prefix}-tree-item-prefix`}>{getIcon(item, onExpand, onCollapse)}</span>
      <span className={`${prefix}-tree-item-title`}>{item.data ? item.data.title : ''}</span>
      {getAction(item)}
    </div>
  );
}
