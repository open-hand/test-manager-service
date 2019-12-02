import React, { Component, createRef } from 'react';
import { observer } from 'mobx-react';
import { Menu } from 'choerodon-ui';
import { handleRequestFailed } from '@/common/utils';
import './TestPlanTree.scss';
import {
  addFolder, editFolder, deleteFolder, moveFolders,
} from '@/api/IssueManageApi';
import { Loading } from '@/components';
import Tree from '@/components/Tree';
import TreeNode from './TreeNode';
import Store from '../../stores';

@observer
class TestPlanTree extends Component {
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
    const { currentPlanId, treeData, getParent } = testPlanStore;
    testPlanStore.setCurrentCycle(item);
    const planId = (getParent(treeData.rootIds, treeData.treeFolder, item.id) && getParent(treeData.rootIds, treeData.treeFolder, item.id).id) || item.id;
    testPlanStore.loadRightData(planId !== currentPlanId);
  }

  renderTreeNode = (node, { item }) => {
    if (item.data.parentId) {
      return (
        node
      );
    } else {
      return (
        <TreeNode
          item={item}
        >
          {node}
        </TreeNode>
      );
    }
  }

  render() {
    const { context: { testPlanStore } } = this.props;
    const { treeLoading } = testPlanStore;
    const { treeData } = testPlanStore;
    return (
      <div className="c7ntest-TestPlanTree">
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
          isDragEnabled={false}
          treeNodeProps={
            {
              enableAction: item => !item.data.parentId,
              menuItems: [
                <Menu.Item key="copy">
                  复制此计划
                </Menu.Item>,
                <Menu.Item key="rename">
                  重命名
                </Menu.Item>,
                <Menu.Item key="delete">
                 删除
                </Menu.Item>,
              ],
            }
          }
          onMenuClick={(nodeItem, key) => {
            switch (key) {
              case 'copy': {
                console.log('copy');
                break;
              }
              default: {
                break;
              }
            }
          }}
        />
      </div>
    );
  }
}

TestPlanTree.propTypes = {

};

export default props => (
  <Store.Consumer>
    {context => (
      <TestPlanTree {...props} context={context} />
    )}
  </Store.Consumer>
);
