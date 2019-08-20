import React, { useState } from 'react';
import {
  Button, Dropdown, Menu,
} from 'choerodon-ui';
import { Permission } from '@choerodon/master';
import './TableDropMenu.less';

/**
 * 表格中的下拉菜单
 * 4个参数
 * menu 菜单 Menu 组件构成的菜单项 
 *      无传入则代表无下来菜单，仅渲染text内容
 * text 该列需渲染的文字部分
 * onClickEdit 点击文件编辑事件
 * isHasMenu 布尔类型， true代表有下拉菜单
 * className  不传入则是默认样式
 */

const TableDropMenu = (props) => {
  const {
    menu, isHasMenu = 'true', text, onClickEdit, className,
  } = props;
  const { permission } = props;
  const {
    type, projectId, organizationId, service,
  } = permission || props;

  return (
    <div className={isHasMenu ? (className || 'table-drop-menu-base') : 'table-drop-menu-base-notEdit'}>
      <span style={{ display: 'inline-block' }}>
        {permission
          ? (
            <Permission
              type={type}
              projectId={projectId}
              organizationId={organizationId}
              service={service}
            >
              <a role="button" onClick={onClickEdit} onKeyDown={null}>
                {text}
              </a>
            </Permission>
          )
          : (
            <a role="button" onClick={isHasMenu ? onClickEdit : null} onKeyDown={null}>
              {text}
            </a>
          )}
      </span>
      {isHasMenu && menu
        ? (
          <div style={{ float: 'right' }}>
            <Dropdown overlay={menu} trigger="click">
              <Button shape="circle" icon="more_vert" />
            </Dropdown>
          </div>
        ) : null}
    </div>
  );
};

export default TableDropMenu;
