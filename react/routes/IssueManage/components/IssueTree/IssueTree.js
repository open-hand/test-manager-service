import React, { Component, createRef } from 'react';
import { observer } from 'mobx-react';
import { handleRequestFailed } from '@/common/utils';
import './IssueTree.scss';
import IssueTreeStore from '../../stores/IssueTreeStore';
import IssueStore from '../../stores/IssueStore';
import {
  addFolder, editFolder, deleteFolder, moveFolder,
} from '@/api/IssueManageApi';
import { Loading } from '@/components';
import Tree from '@/components/Tree';
import TreeNode from './TreeNode';

@observer
class IssueTree extends Component {
  constructor() {
    super();
    this.treeRef = createRef();
    IssueTreeStore.setTreeRef(this.treeRef);
  }

  handleCreate = async (value, parentId) => {
    const data = {
      parentId,
      name: value,
      type: 'cycle',
    };
    const result = await handleRequestFailed(addFolder(data));
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
    const { index } = destination;
    const parent = treeData.items[destination.parentId];
    const lastId = parent.children[index - 1];
    const nextId = parent.children[index];
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
    IssueStore.loadIssues();
  }

  renderTreeNode = (node, { item }) => <TreeNode item={item}>{node}</TreeNode>

  render() {
    const { loading } = IssueTreeStore;
    const treeData = IssueTreeStore.getTreeData;

    return (
      <div className="c7ntest-IssueTree">
        <Loading loading={loading} />
        <Tree
          ref={this.treeRef}          
          data={treeData}
          onCreate={this.handleCreate}
          onEdit={this.handleEdit}
          onDelete={this.handleDelete}
          afterDrag={this.handleDrag}
          selected={IssueTreeStore.getCurrentFolder}
          setSelected={this.setSelected}
          renderTreeNode={this.renderTreeNode}
          treeNodeProps={{
            enableAddFolder: item => !item.hasCase,
          }}
          getDeleteTitle={item => `确认删除“${item.data.name}”文件夹？删除后文件夹下的所有用例也将被删除`}        
        />
      </div>
    );
  }
}

IssueTree.propTypes = {

};

export default IssueTree;
