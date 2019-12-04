import React, { Component, createRef } from 'react';
import { observer } from 'mobx-react';
import { Menu, Icon } from 'choerodon-ui';
import { Choerodon } from '@choerodon/boot';
import { handleRequestFailed } from '@/common/utils';
import './TestPlanTree.scss';
import {
  editPlan, deletePlan, addFolder, editFolder, moveFolder,
} from '@/api/TestPlanApi';
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

  editPlanName = async (newName, item) => {
    const { objectVersionNumber } = item.data;
    const data = {
      planId: item.id,
      objectVersionNumber,
      name: newName,
      caseChanged: false,
    };
    const result = await handleRequestFailed(editPlan(data));
    return {
      data: {
        ...item.data,
        objectVersionNumber: result.objectVersionNumber,
      },
    };    
  };

  editFolderName = async (newName, item) => {
    const { objectVersionNumber } = item.data;
    const data = { 
      cycleName: newName,
    };
    const result = await handleRequestFailed(editFolder(data));
    return {
      data: {
        ...item.data,
        name: result.name,
        objectVersionNumber: result.objectVersionNumber,
      },
    };    
  };

  handleReName = async (newName, item) => {
    const { context: { testPlanStore } } = this.props;
    const isPlan = testPlanStore.isPlan(item.id);
    return isPlan ? this.editPlanName(newName, item) : this.editFolderName(newName, item);
  }

  handleDelete = item => handleRequestFailed(deletePlan(item.id))

  handleDrag = async (sourceItem, destination) => {
    const { context: { testPlanStore } } = this.props;
    const { parentId } = destination;
    const isPlan = testPlanStore.isPlan(parentId);
    const [, folderId] = testPlanStore.getId(parentId);
    const data = {
      parentCycleId: isPlan ? 0 : folderId,
    };
    const result = await handleRequestFailed(editFolder(data));
    return {
      data: {
        ...sourceItem.data,
        objectVersionNumber: result.objectVersionNumber,
      },
    };   
  }

  handleCreateFolder = async (value, parentId, item) => {
    const { context: { testPlanStore } } = this.props;
    const isPlan = testPlanStore.isPlan(parentId);
    const [, folderId] = testPlanStore.getId(parentId);
    const data = {
      parentCycleId: isPlan ? 0 : folderId,
      cycleName: value,
    };
    const result = await handleRequestFailed(addFolder(data));
    return {
      data: {
        ...item.data,
        objectVersionNumber: result.objectVersionNumber,
      },
    };   
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
      const canImport = item.children.length === 0;
      return canImport ? [
        <Menu.Item key="rename">
          重命名
        </Menu.Item>,
        <Menu.Item key="delete">
          删除
        </Menu.Item>,
        <Menu.Item key="import">
          导入用例
        </Menu.Item>,
      ] : [
        <Menu.Item key="rename">
            重命名
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
          onCreate={this.handleCreateFolder}
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
              // 计划和没有执行的，可以添加子文件夹
              enableAddFolder: item => item.topLevel || !item.hasCase,
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
