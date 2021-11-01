import React, { Component, createRef } from 'react';
import { observer } from 'mobx-react';
import { Menu, Icon } from 'choerodon-ui';
import { Choerodon } from '@choerodon/master';
import Loading from '@choerodon/agile/lib/components/Loading';
import { handleRequestFailed, getProjectId } from '@/common/utils';
import './TestPlanTree.less';
import {
  editPlan, deletePlan, addFolder, editFolder, deleteFolder, checkPlanName,
} from '@/api/TestPlanApi';
import Tree from '@/components/Tree';
import { openClonePlan } from '../TestPlanModal';
import openDragPlanFolder from '../DragPlanFolder';
import openImportIssue from '../ImportIssue';
import TreeNode from './TreeNode';
import Store from '../../stores';
import openBatchAssignModal from '../BatchAssignModal';
import { closeBatchModal } from '../BatchAction';

@observer
class TestPlanTree extends Component {
  constructor(props) {
    super(props);
    this.treeRef = createRef();
    const { context: { testPlanStore } } = this.props;
    testPlanStore.setTreeRef(this.treeRef);
  }

  editPlanName = async (newName, item) => {
    const { context: { testPlanStore } } = this.props;
    const { objectVersionNumber } = item.data;
    const data = {
      planId: item.id,
      objectVersionNumber,
      name: newName,
      caseChanged: false,
    };
    const result = await handleRequestFailed(editPlan(data));
    testPlanStore.loadIssueTree();
    return {
      data: {
        ...item.data,
        name: newName,
        objectVersionNumber: result.objectVersionNumber,
      },
    };
  };

  editFolderName = async (newName, item) => {
    const { context: { testPlanStore } } = this.props;
    const [, folderId] = testPlanStore.getId(item.id);
    const { objectVersionNumber } = item.data;
    const data = {
      cycleId: folderId,
      cycleName: newName,
      objectVersionNumber,
      projectId: getProjectId(),
    };
    const result = await handleRequestFailed(editFolder(data));
    testPlanStore.loadIssueTree();
    return {
      data: {
        ...item.data,
        name: newName,
        objectVersionNumber: result.objectVersionNumber,
      },
    };
  };

  handleReName = async (newName, item) => {
    const { context: { testPlanStore } } = this.props;
    const isPlan = testPlanStore.isPlan(item.id);
    if (isPlan && newName !== item.data.name) {
      if (await checkPlanName(newName)) {
        Choerodon.prompt('计划名称重复');
        return item;
      }
    }
    return isPlan ? this.editPlanName(newName, item) : this.editFolderName(newName, item);
  }

  handleDelete = async (item) => {
    const { context: { testPlanStore } } = this.props;
    const isPlan = testPlanStore.isPlan(item.id);
    if (isPlan) {
      await handleRequestFailed(deletePlan(item.id));
      testPlanStore.loadIssueTree();
    } else {
      const [, folderId] = testPlanStore.getId(item.id);
      await handleRequestFailed(deleteFolder(folderId));
      testPlanStore.loadIssueTree();
    }
    console.log('handleDelete');
    // testPlanStore.loadAllData();
  }

  handleCreateFolder = async (value, parentId, item) => {
    const { context: { testPlanStore } } = this.props;
    const isPlan = testPlanStore.isPlan(parentId);
    const [planId, folderId] = testPlanStore.getId(parentId);
    const data = {
      planId,
      parentCycleId: isPlan ? null : folderId,
      cycleName: value,
    };
    const result = await handleRequestFailed(addFolder(data));
    testPlanStore.loadIssueTree();
    return {
      id: `${planId}-${result.cycleId}`,
      data: {
        parentId: result.parentId,
        name: value,
        objectVersionNumber: result.objectVersionNumber,
      },
    };
  }

  setSelected = (item) => {
    const { context: { testPlanStore } } = this.props;
    const [planId, folderId] = testPlanStore.getId(item.id);
    const { executePagination } = testPlanStore;
    if (item.id) {
      if (testPlanStore.isPlan(item.id)) { // 如果是计划
        testPlanStore.updateTimes([item]);
      }
      testPlanStore.setMainActiveTab('testPlanTable');
      testPlanStore.setFilter({});
      testPlanStore.setBarFilter([]);
      closeBatchModal({ testPlanStore });
      testPlanStore.setExecutePagination({
        ...executePagination,
        current: 1,
        pageSize: 20,
      });
      testPlanStore.loadRightData(planId, folderId);
    }
    testPlanStore.setCurrentCycle(item);
  }

  handleAfterDelete = (deleteItem, target) => {
    const { context: { testPlanStore } } = this.props;
    console.log('handleAfterDelete', target);
    // 删除的不是选中时
    if (!target) {
      const [planId, folderId] = testPlanStore.getId();
      const { executePagination } = testPlanStore;
      if (folderId === '') { // 如果是计划
        testPlanStore.updateTimes([testPlanStore.treeFolderMaps.get(planId)]);
      }
      testPlanStore.setMainActiveTab('testPlanTable');
      testPlanStore.setFilter({});
      testPlanStore.setBarFilter([]);
      closeBatchModal({ testPlanStore });
      testPlanStore.setExecutePagination({
        ...executePagination,
        current: 1,
        pageSize: 20,
      });
      testPlanStore.loadRightData(planId, folderId);
    }
  }

  handleMenuClick = (key, nodeItem) => {
    const { context: { testPlanStore } } = this.props;
    switch (key) {
      case 'copy': {
        openClonePlan({
          data: nodeItem,
          onCLone: () => {
            testPlanStore.loadAllData();
          },
        });
        break;
      }
      case 'drag': {
        openDragPlanFolder({
          beforeOpen: (plantIds) => testPlanStore.getFolderDataById(nodeItem.id).isSort && testPlanStore.returnDefaultRank(plantIds),
          planId: nodeItem.id,
          handleOk: () => {
            testPlanStore.loadAllData();
          },
        });
        break;
      }
      case 'import': {
        const [planId, folderId] = testPlanStore.getId(nodeItem.id);
        openImportIssue({
          planId,
          folderId,
          onSubmit: () => {
            testPlanStore.loadAllData();
          },
        });
        break;
      }
      case 'delete': {
        testPlanStore.treeRef.current.trigger.delete(nodeItem);
        break;
      }
      case 'rank': {
        testPlanStore.RankByDate(nodeItem.id);
        break;
      }
      case 'assign': {
        if (nodeItem.id) {
          const [planId, cycleId] = String(nodeItem.id).split('%');
          let handleOk = () => { };
          const [currentPlanId, currentCycleId] = testPlanStore.getId();
          if (currentPlanId === planId && (currentCycleId === cycleId || currentCycleId === '') && testPlanStore.mainActiveTab !== 'testPlanSchedule') {
            handleOk = () => testPlanStore.loadRightData();
          }
          openBatchAssignModal({ planId, cycleId }, handleOk);
        }
        break;
      }
      default: {
        break;
      }
    }
  }

  handleUpdateItem = (item) => {
    const { context: { testPlanStore } } = this.props;
    if (testPlanStore.getCurrentPlanId === item.id) {
      testPlanStore.setPlanInfo({ ...testPlanStore.planInfo, name: item.data.name });
    }
  }

  renderTreeNode = (node, { item }) => {
    if (!item.topLevel) {
      return (
        node
      );
    }
    return (
      <TreeNode
        item={item}
        nodeProps={node.props}
        onMenuClick={this.handleMenuClick}
      >
        {node}
      </TreeNode>
    );
  }

  getMenuItems = (item) => {
    const isPlan = item.topLevel;
    const { status, context: { testPlanStore } } = this.props;
    if (status === 'done') {
      return isPlan ? [
        <Menu.Item key="copy">
          复制此计划
        </Menu.Item>,
      ] : null;
    }
    if (isPlan) {
      return [
        <Menu.Item key="copy">
          复制此计划
        </Menu.Item>,
        <Menu.Item key="rename">
          重命名
        </Menu.Item>,
        <Menu.Item key="rank">
          {testPlanStore.getFolderDataById(item.id).isSort ? '默认排序' : '时间排序'}
        </Menu.Item>,
        <Menu.Item key="drag">
          调整计划结构
        </Menu.Item>,
        <Menu.Item key="delete">
          删除
        </Menu.Item>,
      ];
    }
    const canImport = item.children.length === 0;
    return canImport ? [
      <Menu.Item key="rename">
        重命名
      </Menu.Item>,
      <Menu.Item key="assign">
        批量指派
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
      <Menu.Item key="assign">
        批量指派
      </Menu.Item>,
      <Menu.Item key="delete">
        删除
      </Menu.Item>,
    ];
  }

  render() {
    const { context: { testPlanStore }, status } = this.props;
    const { treeLoading } = testPlanStore;
    const { treeData } = testPlanStore;
    return (
      <div className="c7ntest-TestPlanTree">
        <Loading loadId="tree" />
        <Tree
          ref={this.treeRef}
          data={treeData}
          onCreate={this.handleCreateFolder}
          onEdit={this.handleReName}
          onDelete={this.handleDelete}
          getDeleteTitle={(item) => {
            const isPlan = item.topLevel;
            return isPlan ? '确认删除计划? |删除后计划下的所有执行也将被删除' : '确认删除目录? |删除后目录下的所有执行也将被删除';
          }}
          selected={testPlanStore.currentCycle}
          onAfterDelete={this.handleAfterDelete}
          setSelected={this.setSelected}
          updateItem={this.handleUpdateItem}
          renderTreeNode={this.renderTreeNode}
          isDragEnabled={false}
          treeNodeProps={
            {
              menuItems: this.getMenuItems,
              getFolderIcon: (item, defaultIcon) => (item.topLevel ? <Icon type="insert_invitation" style={{ marginRight: 5 }} /> : defaultIcon),
              // 计划和没有执行的，可以添加子目录
              // 最多8层
              enableAddFolder: status === 'done' ? false : (item) => item.path.length < 9 && (item.topLevel || !item.hasCase),
            }
          }
          onMenuClick={this.handleMenuClick}
        />
      </div>
    );
  }
}

TestPlanTree.propTypes = {

};

export default (props) => (
  <Store.Consumer>
    {(context) => (
      // eslint-disable-next-line react/jsx-props-no-spreading
      <TestPlanTree {...props} context={context} />
    )}
  </Store.Consumer>
);
