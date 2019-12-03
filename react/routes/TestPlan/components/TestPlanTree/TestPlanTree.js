import React, { Component, createRef } from 'react';
import { observer } from 'mobx-react';
import { Menu, Icon } from 'choerodon-ui';
import { Choerodon } from '@choerodon/boot';
import { handleRequestFailed } from '@/common/utils';
import './TestPlanTree.scss';
import {
  moveFolders,
} from '@/api/IssueManageApi';
import { editPlan, deletePlan } from '@/api/TestPlanApi';
import { Loading } from '@/components';
import Tree from '@/components/Tree';
import { openClonePlan } from '../TestPlanModal';
import openDragPlanFolder from '../DragPlanFolder';
import openImportIssue from '../ImportIssue';
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

  editPlanName = (data) => {
    const { getItem, updateTree } = this.treeRef.current || {};
    return editPlan(data).then(() => {
      const planItem = getItem(data.planId);
      updateTree(data.planId, { data: { ...planItem.data, objectVersionNumber: data.objectVersionNumber + 1 } });
    }).catch(() => {
      Choerodon.prompt('重命名失败');
    });
  };

  handleReName = (newName, item) => {
    const { objectVersionNumber } = item.data;
    const data = {
      planId: item.id,
      objectVersionNumber,
      name: newName,
      caseChanged: false,
    };
    return handleRequestFailed(this.editPlanName(data));
  }

  handleDelete = item => handleRequestFailed(deletePlan(item.id))

  handleDrag = (sourceItem, destination) => {
    handleRequestFailed(moveFolders([sourceItem.id], destination.parentId));
  }

  setSelected = (item) => {
    const { context: { testPlanStore } } = this.props;
    const [planId] = testPlanStore.getId(item.id);
    testPlanStore.setCurrentCycle(item);
    testPlanStore.loadRightData(planId !== testPlanStore.getCurrentPlanId);
  }

  renderTreeNode = (node, { item }) => {
    if (!item.topLevel) {
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

  getMenuItems = (item) => {
    const isPlan = item.topLevel;
    if (isPlan) {
      return [
        <Menu.Item key="copy">
          复制此计划
        </Menu.Item>,
        <Menu.Item key="rename">
          重命名
        </Menu.Item>,
        <Menu.Item key="drag">
          调整结构
        </Menu.Item>,
        <Menu.Item key="delete">
          删除
        </Menu.Item>,
      ];
    } else {
      return [
        <Menu.Item key="rename">
          重命名
        </Menu.Item>,
        <Menu.Item key="import">
          导入用例
        </Menu.Item>,
        <Menu.Item key="delete">
          删除
        </Menu.Item>,
      ];
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
          onEdit={this.handleReName}
          onDelete={this.handleDelete}
          afterDrag={this.handleDrag}
          selected={testPlanStore.currentCycle}
          setSelected={this.setSelected}
          renderTreeNode={this.renderTreeNode}
          isDragEnabled={false}
          treeNodeProps={
            {              
              menuItems: this.getMenuItems,
              getFolderIcon: (item, defaultIcon) => (item.topLevel ? <Icon type="insert_invitation" style={{ marginRight: 5 }} /> : defaultIcon),
            }
          }
          onMenuClick={(key, nodeItem) => {
            switch (key) {
              case 'copy': {
                openClonePlan({
                  planId: nodeItem.id,
                });
                break;
              }
              case 'drag': {
                openDragPlanFolder({
                  planId: nodeItem.id,
                });
                break;
              }
              case 'import': {
                openImportIssue({
                  planId: nodeItem.id,
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
