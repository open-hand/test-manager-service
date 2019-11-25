import React, { Component, createRef } from 'react';
import { observer } from 'mobx-react';
import { handleRequestFailed } from '@/common/utils';
import './IssueTree.scss';
import IssueTreeStore from '../../stores/IssueTreeStore';
import {
  addFolder, editFolder, deleteFolder, moveFolders,
} from '../../../../api/IssueManageApi';
import IssueStore from '../../stores/IssueStore';
import { NoVersion, Loading } from '../../../../components';
import Tree from '../Tree';

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

  handleDrag = (sourceItem, destination) => handleRequestFailed(moveFolders([sourceItem.id], destination.parentId))
  

  setSelected = (item) => {
    IssueTreeStore.setCurrentCycle(item);
    IssueStore.loadIssues();
  }

  render() {
    const { loading } = IssueTreeStore;
    const treeData = IssueTreeStore.getTreeData;
    const noVersion = treeData.rootIds.length === 0;

    return (
      <div className="c7ntest-IssueTree">
        <Loading loading={loading} />
        {noVersion ? !loading && <NoVersion /> : (
          <Tree
            ref={this.treeRef}
            data={treeData}
            onCreate={this.handleCreate}
            onEdit={this.handleEdit}
            onDelete={this.handleDelete}
            afterDrag={this.handleDrag}
            selected={IssueTreeStore.getCurrentCycle}
            setSelected={this.setSelected}
          />
        )}
      </div>
    );
  }
}

IssueTree.propTypes = {

};

export default IssueTree;
