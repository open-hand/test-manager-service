import React, { Component, createRef } from 'react';
import { observer } from 'mobx-react';
import { Menu, Icon } from 'choerodon-ui';
import { handleRequestFailed } from '@/common/utils';
import './TestPlanTree.scss';
import {
  addFolder, editFolder, deleteFolder, moveFolders,
} from '@/api/IssueManageApi';
import { Loading } from '@/components';
import Tree from '@/components/Tree';
import { openClonePlan } from '../TestPlanModal';
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
    console.log(item);
    const { context: { testPlanStore } } = this.props;
    const { getCurrentPlanId, treeData, getParent } = testPlanStore;
    testPlanStore.setCurrentCycle(item);
    // const planId = (getParent(treeData.rootIds, treeData.treeFolder, item.id) && getParent(treeData.rootIds, treeData.treeFolder, item.id).id) || item.id;
    testPlanStore.loadRightData(true);
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
          nodeProps={node.props}
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
          selected={testPlanStore.currentCycle}
          setSelected={this.setSelected}
          renderTreeNode={this.renderTreeNode}
          isDragEnabled={false}
          treeNodeProps={
            {
              enableAction: item => item.topLevel,
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
              getFolderIcon: (item, defaultIcon) => (item.topLevel ? <Icon type="insert_invitation" style={{ marginRight: 5 }} /> : defaultIcon),
            }
          }
          onMenuClick={(key, nodeItem) => {
            switch (key) {
              case 'copy': {
                openClonePlan({
                  planId: Number(nodeItem.id.split('-')[0]),
                });
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
