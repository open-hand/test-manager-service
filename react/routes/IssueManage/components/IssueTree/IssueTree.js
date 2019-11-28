import React, { Component, createRef } from 'react';
import { observer } from 'mobx-react';
import { handleRequestFailed } from '@/common/utils';
import './IssueTree.scss';
import IssueTreeStore from '../../stores/IssueTreeStore';
import IssueStore from '../../stores/IssueStore';
import {
  addFolder, editFolder, deleteFolder, moveFolders,
} from '@/api/IssueManageApi';
import { NoVersion, Loading } from '@/components';
import Tree from '@/components/Tree';
import TreeNode from './TreeNode';

@observer
class IssueTree extends Component {
  constructor() {
    super();
    this.treeRef = createRef();
    IssueTreeStore.setTreeRef(this.treeRef);
  }

  handleCreate = (value, parentId) => {
    const data = {
      parentId,
      name: value,
      type: 'cycle',
    };
    return handleRequestFailed(addFolder(data));
  }

  handleEdit = (newName, item) => {
    const { objectVersionNumber } = item.data;
    const data = {
      folderId: item.id,
      objectVersionNumber,
      name: newName,
      type: 'cycle',
    };
    return handleRequestFailed(editFolder(data));
  }

  handleDelete = item => handleRequestFailed(deleteFolder(item.id))

  handleDrag = (sourceItem, destination) => {
    handleRequestFailed(moveFolders([sourceItem.id], destination.parentId));
  }
  

  setSelected = (item) => {
    IssueTreeStore.setCurrentCycle(item);
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
          empty={loading ? null : <NoVersion onCreateClick={() => { this.treeRef.current.addFirstLevelItem(); }} />}
          data={treeData}
          onCreate={this.handleCreate}
          onEdit={this.handleEdit}
          onDelete={this.handleDelete}
          afterDrag={this.handleDrag}
          selected={IssueTreeStore.getCurrentCycle}
          setSelected={this.setSelected}
          renderTreeNode={this.renderTreeNode}
          enableAddFolder={item => !item.hasCase}
        />
      </div>
    );
  }
}

IssueTree.propTypes = {

};

export default IssueTree;
