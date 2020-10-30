import React, { Component, createRef } from 'react';
import { WSHandler, Choerodon } from '@choerodon/boot';
import { observer } from 'mobx-react';
import { Menu } from 'choerodon-ui';
import { handleRequestFailed } from '@/common/utils';
import { preCopyFolder, copyFolder } from '@/api/IssueManageApi';
import './IssueTree.less';
import {
  addFolder, editFolder, deleteFolder, moveFolder,
} from '@/api/IssueManageApi';
import { Loading } from '@/components';
import Tree from '@/components/Tree';
import IssueStore from '../../stores/IssueStore';
import IssueTreeStore from '../../stores/IssueTreeStore';
import TreeNode from './TreeNode';

@observer
class IssueTree extends Component {
  constructor() {
    super();
    this.treeRef = createRef();
    IssueTreeStore.setTreeRef(this.treeRef);
  }

  handleCreate = async (value, parentId) => {
    if (value && value.indexOf('/') > -1) {
      Choerodon.prompt('目录名不能包含/');
      return false;
    }

    const data = {
      [parentId ? 'parentId' : 'rootNode']: parentId || true, // 解决主键为0 无法解密
      name: value,
      type: 'cycle',
    };
    const result = await handleRequestFailed(addFolder(data));

    if (parentId === 0) {
      IssueTreeStore.addRootItem(result.folderId);
    }

    const newData = {
      id: result.folderId,
      children: [],
      data: result,
    };
    // 增添至数组内，防止导入后对树内数据搜索无数据
    IssueTreeStore.addTreeFolder(newData);
    return {
      id: result.folderId,
      data: {
        parentId: result.parentId,
        name: value,
        objectVersionNumber: result.objectVersionNumber,
      },
    };
  }

  handleEdit = async (newName, item) => {
    if (newName && newName.indexOf('/') > -1) {
      Choerodon.prompt('目录名不能包含/');
      return false;
    }
    const { objectVersionNumber } = item.data;
    const data = {
      folderId: item.id,
      objectVersionNumber,
      name: newName,
      type: 'cycle',
    };
    const result = await handleRequestFailed(editFolder(data));
    return {
      data: {
        ...item.data,
        name: result.name,
        objectVersionNumber: result.objectVersionNumber,
      },
    };
  }

  handleDelete = async (item) => {
    await handleRequestFailed(deleteFolder(item.id));
    // 只移除跟节点，作用是删除目录后可以正确判断是不是没目录了，来显示空插画
    IssueTreeStore.removeRootItem(item.id);
  }

  handleDrag = async (sourceItem, destination) => {
    const { treeData } = this.treeRef.current;
    const parent = treeData.items[destination.parentId];
    const { index = parent.children.length } = destination;
    let lastId = parent.children[index - 1];
    let nextId = parent.children[index];
    // 解决树的拖拽排序 无法从上往下拖拽排序
    if (sourceItem.parentId === destination.parentId && sourceItem.index < index) {
      lastId = parent.children[index];
      nextId = parent.children.length !== index ? parent.children[index + 1] : null;
    }
    const data = {
      folderId: sourceItem.id,
      lastRank: lastId ? treeData.items[lastId].data.rank : null,
      nextRank: nextId ? treeData.items[nextId].data.rank : null,
    };
    const rank = await handleRequestFailed(moveFolder(data, destination.parentId));
    return {
      data: {
        ...sourceItem.data,
        rank,
        parentId: destination.parentId,
        objectVersionNumber: sourceItem.data.objectVersionNumber + 1,
      },
    };
  }

  setSelected = (item) => {
    IssueTreeStore.setCurrentFolder(item);
    IssueStore.setClickIssue({});
    IssueStore.setPagination({
      current: 1,
      pageSize: 10,
      total: IssueStore.pagination.total,
    });
    IssueStore.setOrder({
      orderField: '',
      orderType: '',
    });
    IssueStore.loadIssues();
  }

  handleUpdateItem = (item) => {
    IssueTreeStore.setCurrentFolder(item);
  }

  handleCopyFolder = (folderId) => {
    preCopyFolder(folderId).then((isCanCopy) => {
      if (isCanCopy) {
        copyFolder(folderId).then((res) => {
          IssueTreeStore.loadIssueTree(folderId);
        });
        return true;
      }
      Choerodon.prompt('复制后的文件名超出最大限制');
      return false;
    });
  }

  handleMenuClick = (key, nodeItem) => {
    switch (key) {
      case 'copy':
        this.handleCopyFolder(nodeItem.id);
        break;
      case 'delete':
        IssueTreeStore.treeRef.current.trigger.delete(nodeItem);
        break;
      default:
        break;
    }
  }

  handleMessage = (data) => { // 暂时废弃
    if (data && data === 'success') {
      IssueTreeStore.loadIssueTree(IssueStore.getCurrentFolder);
    }
  }

  renderTreeNode = (node, { item }) => (
    <TreeNode
      nodeProps={node.props}
      item={item}
      onMenuClick={this.handleMenuClick}
    >
      {node}
    </TreeNode>
  )

  getMenuItems = () => ([

    <Menu.Item key="rename">
      重命名
    </Menu.Item>,
    <Menu.Item key="copy">
      复制
    </Menu.Item>,
    <Menu.Item key="delete">
      删除
    </Menu.Item>,

  ]);

  render() {
    const { loading, treeData } = IssueTreeStore;

    return (
      <div className="c7ntest-IssueTree">
        <Loading loading={loading} />
        <WSHandler
          messageKey="COPY_TEST_FOLDER"
          onMessage={this.handleMessage}
        >
          <Tree
            ref={this.treeRef}
            data={treeData}
            onCreate={this.handleCreate}
            onEdit={this.handleEdit}
            onDelete={this.handleDelete}
            afterDrag={this.handleDrag}
            selected={IssueTreeStore.getCurrentFolder}
            setSelected={this.setSelected}
            updateItem={this.handleUpdateItem}
            renderTreeNode={this.renderTreeNode}
            onMenuClick={this.handleMenuClick}
            treeNodeProps={{
              menuItems: this.getMenuItems,
              // 最多8层
              enableAddFolder: (item) => item.path.length < 9 && !item.hasCase,
            }}
            getDeleteTitle={(item) => `确认删除“${item.data.name}”目录？|删除后目录下的所有用例也将被删除`}
          />
        </WSHandler>
      </div>
    );
  }
}

IssueTree.propTypes = {

};

export default IssueTree;
