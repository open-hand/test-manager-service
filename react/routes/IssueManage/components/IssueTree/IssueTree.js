import React, { Component, createRef } from 'react';
import { observer } from 'mobx-react';
import { handleRequestFailed } from '@/common/utils';
import './IssueTree.scss';
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
    const data = {
      parentId,
      name: value,
      type: 'cycle',
    };
    const result = await handleRequestFailed(addFolder(data));    
    if (parentId === 0) {
      IssueTreeStore.addRootItem(result.folderId);
    }
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
    const parent = treeData.items[destination.parentId];
    const { index = parent.children.length } = destination;
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
    IssueStore.setClickIssue({});
    IssueStore.setPagination({
      current: 1,
      pageSize: 10,
      total: IssueStore.pagination.total,
    });
    IssueStore.loadIssues();
  }

  handleUpdateItem=(item) => {
    IssueTreeStore.setCurrentFolder(item);
  }

  renderTreeNode = (node, { item }) => <TreeNode item={item}>{node}</TreeNode>

  render() {
    const { loading, treeData } = IssueTreeStore;

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
          updateItem={this.handleUpdateItem}
          renderTreeNode={this.renderTreeNode}
          treeNodeProps={{
            // 最多8层
            enableAddFolder: item => item.path.length < 9 && !item.hasCase,
          }}
          getDeleteTitle={item => `确认删除“${item.data.name}”目录？|删除后目录下的所有用例也将被删除`}        
        />
      </div>
    );
  }
}

IssueTree.propTypes = {

};

export default IssueTree;
