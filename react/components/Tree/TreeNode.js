import React, { Fragment } from 'react';
import styled from 'styled-components';
import classNames from 'classnames';
import {
  Icon, Button, TextField,
} from 'choerodon-ui/pro';
import { Menu, Dropdown } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import SmartTooltip from '@/components/SmartTooltip';

const defaultProps = {
  enableAction: true,
  enableAddFolder: () => false,
};
const PreTextIcon = styled.span`
  display: inline-block;
  visibility: hidden;
  width: 22px;
  justify-content: center;
  cursor: pointer;
`;

const prefix = 'c7ntest-tree';
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
    return (
      <Fragment>
        {exapndIcon}
        {folderIcon}
      </Fragment>
    );
  }
  return (
    <Fragment>
      <PreTextIcon>&bull;</PreTextIcon>
      {folderIcon}
    </Fragment>
  );
};
const getAction = (item, menuItems, enableAddFolder, onMenuClick) => {
  const menu = (
    <Menu onClick={(target) => { onMenuClick(item, target); }}>
      {menuItems || [
        <Menu.Item key="rename">
          重命名
        </Menu.Item>,
        <Menu.Item key="delete">
          删除
        </Menu.Item>]}
    </Menu>
  );
  return (
    <div key={item.id} role="none" onClick={(e) => { e.stopPropagation(); }} className={`${prefix}-tree-item-action`}>
      {enableAddFolder(item) && <Icon type="create_new_folder" style={{ marginRight: 6 }} onClick={() => { onMenuClick(item, { key: 'add' }); }} />}
      <Dropdown overlay={menu} trigger={['click']} getPopupContainer={trigger => trigger.parentNode}>
        <Button funcType="flat" icon="more_vert" size="small" />
      </Dropdown>
    </div>
  );
};


function TreeNode(props) {
  const {
    provided, onSelect, path, item, onExpand, onCollapse, onMenuClick, onCreate, search, onEdit, enableAction, menuItems, enableAddFolder,
  } = props;
  const onSave = (e) => {
    if (item.id === 'new') {
      onCreate(e.target.value, path, item);
    } else {
      onEdit(e.target.value, item);
    }
  };
  const renderEditing = () => (
    <div
      role="none"
      className={`${prefix}-tree-item`}
    >
      <TextField placeholder="请输入文件夹名称" style={{ width: '100%' }} maxLength={20} defaultValue={item.data.name} onBlur={onSave} autoFocus />
    </div>
  );
  const renderTitle = () => {
    const { name } = item.data;
    const index = name.indexOf(search);
    const beforeStr = name.substr(0, index);
    const afterStr = name.substr(index + search.length);
    const result = index > -1 ? (
      <span>
        {beforeStr}
        <span style={{ color: '#f50' }}>{search}</span>
        {afterStr}
      </span>
    ) : name;
    return <SmartTooltip title={name}>{result}</SmartTooltip>;
  };
  const renderContent = () => (
    <div
      className={`${prefix}-tree-item-wrapper`}
    >
      <div
        role="none"
        className={classNames(`${prefix}-tree-item`, { [`${prefix}-tree-item-selected`]: item.selected })}
        onClick={() => { onSelect(item); }}
      >
        <span className={`${prefix}-tree-item-prefix`}>{getIcon(item, onExpand, onCollapse)}</span>
        <span className={`${prefix}-tree-item-title`}>{renderTitle()}</span>
        {enableAction && getAction({ ...item, path }, menuItems, enableAddFolder, onMenuClick)}
      </div>
    </div>
  );
  // console.log(path);
  return (
    <div
      ref={provided.innerRef}
      {...provided.draggableProps}
      {...provided.dragHandleProps}
    >
      {item.isEditing ? renderEditing() : renderContent()}
    </div>
  );
}
TreeNode.defaultProps = defaultProps;
export default observer(TreeNode);
