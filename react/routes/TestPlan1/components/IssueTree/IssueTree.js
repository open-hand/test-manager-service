import React, { Component, createRef } from 'react';
import { observer } from 'mobx-react';
import { handleRequestFailed } from '@/common/utils';
import './IssueTree.scss';
import {
  addFolder, editFolder, deleteFolder, moveFolders,
} from '@/api/IssueManageApi';
import { Loading } from '@/components';
import Tree from '@/components/Tree';
import TreeNode from './TreeNode';
import Store from '../../stores';

@observer
class IssueTree extends Component {
  constructor(props) {
    super(props);
    this.treeRef = createRef();
    const { context: { testPlanStore } } = this.props;
    testPlanStore.setTreeRef(this.treeRef);
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
    const { context: { testPlanStore } } = this.props;
    testPlanStore.setCurrentCycle(item);
    testPlanStore.loadExecutes();
  }

  renderTreeNode = (node, { item }) => {
    if (item.data.parentId) {
      return node;
    } else {
      return (
        <TreeNode item={item}>{node}</TreeNode>
      );
    }
  }

  render() {
    const { context: { testPlanStore } } = this.props;
    const { treeLoading } = testPlanStore;
    const { treeData } = testPlanStore;
    return (
      <div className="c7ntest-IssueTree">
        <Loading loading={treeLoading} />
        <Tree
          ref={this.treeRef}
          data={treeData}
          onCreate={this.handleCreate}
          onEdit={this.handleEdit}
          onDelete={this.handleDelete}
          afterDrag={this.handleDrag}
          selected={testPlanStore.getCurrentCycle}
          setSelected={this.setSelected}
          renderTreeNode={this.renderTreeNode}
        />
      </div>
    );
  }
}

IssueTree.propTypes = {

};

export default props => (
  <Store.Consumer>
    {context => (
      <IssueTree {...props} context={context} />
    )}
  </Store.Consumer>
);
