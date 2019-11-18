import React, { Component, createRef } from 'react';
import { observer } from 'mobx-react';
import { Choerodon } from '@choerodon/boot';
import _ from 'lodash';
import { getProjectId, getOrganizationId, handleRequestFailed } from '@/common/utils';
import './IssueTree.scss';
import IssueTreeStore from '../../stores/IssueTreeStore';
import {
  getIssueTree, addFolder, moveFolders, copyFolders,
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
      type: 'folder',
    };
    return handleRequestFailed(addFolder(data));
  }

  handleEdit = () => {

  }

  handleDrag = (sourceItem, destination) => {
    // console.log(sourceItem, destination);
  }

  render() {
    const { loading } = IssueTreeStore;
    const treeData = IssueTreeStore.getTreeData;
    const noVersion = treeData.rootIds.length === 0;

    return (
      <div className="c7ntest-IssueTree">
        <Tree
          ref={this.treeRef}
          data={treeData}
          onCreate={this.handleCreate}
          onEdit={this.handleEdit}
          afterDrag={this.handleDrag}
        />
      </div>
    );
  }
}

IssueTree.propTypes = {

};

export default IssueTree;
