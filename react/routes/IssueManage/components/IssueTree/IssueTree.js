import React, { Component, createRef } from 'react';
import { observer } from 'mobx-react';
import { Choerodon } from '@choerodon/boot';
import _ from 'lodash';
import { getProjectId, getOrganizationId, handleRequestFailed } from '@/common/utils';
import './IssueTree.scss';
import IssueTreeStore from '../../stores/IssueTreeStore';
import {
  getIssueTree, addFolder, moveFolders, copyFolders, editFolder, deleteFolder,
} from '../../../../api/IssueManageApi';
import IssueStore from '../../stores/IssueStore';
import { NoVersion } from '../../../../components';
import Tree from '../Tree';

@observer
class IssueTree extends Component {
  constructor() {
    super();
    this.treeRef = createRef();
    IssueTreeStore.setTreeRef(this.treeRef);
    this.state = {

    };
  }


  componentDidMount() {
    this.getTree();
  }

  getTree = () => {
    IssueTreeStore.setLoading(true);
    getIssueTree().then((data) => {
      IssueTreeStore.setTreeData(data);
      IssueTreeStore.setLoading(false);
    }).catch(() => {
      IssueTreeStore.setLoading(false);
      Choerodon.prompt('网络错误');
    });
  }

  handleCreate = (value, parentId) => {
    // console.log(value, parentId);
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
    // console.log(sourceItem, destination);
  }

  setSelected = (item) => {
    IssueTreeStore.setCurrentCycle(item);
  }

  render() {
    const { loading } = IssueTreeStore;
    const treeData = IssueTreeStore.getTreeData;
    const noVersion = treeData.rootIds.length === 0;

    return (
      <div className="c7ntest-IssueTree">
        {/* {IssueTreeStore.getCurrentCycle.id && IssueTreeStore.getCurrentCycle.data.name} */}
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
      </div>
    );
  }
}

IssueTree.propTypes = {

};

export default IssueTree;
