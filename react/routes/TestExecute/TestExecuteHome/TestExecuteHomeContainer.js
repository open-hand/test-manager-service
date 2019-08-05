
import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { stores } from '@choerodon/boot';

import _ from 'lodash';
import moment from 'moment';
import TestExecuteHome from './TestExecuteHome';
import {
  getCycleTree, getExecutesByCycleId,
} from '../../../api/cycleApi';
import { getPrioritys } from '../../../api/agileApi';
import { getStatusList } from '../../../api/TestStatusApi';
import { editCycle } from '../../../api/ExecuteDetailApi';
import { getParams, executeDetailLink } from '../../../common/utils';
import RunWhenProjectChange from '../../../common/RunWhenProjectChange';
import TestExecuteStore from '../TestExecuteStore/TestExecuteStore';

const { AppState } = stores;


const dataList = [];

function getParentKey(key) { return key.split('-').slice(0, -1).join('-'); }
@observer
class TestExecuteHomeContainer extends Component {
  state = {
    loading: false,
    tableLoading: false,
    testList: [],
    autoExpandParent: true,
    treeSearchValue: '',
    statusList: [],
    filters: {},
    prioritys: [],
  };

  componentDidMount() {
    RunWhenProjectChange(TestExecuteStore.clearStore);
    this.loadTreeAndExecute();
  }


  loadTreeAndExecute = (assignedTo = TestExecuteStore.treeAssignedTo) => {
    this.setState({
      loading: true,
    });
    Promise.all([getStatusList('CYCLE_CASE'), getPrioritys()]).then(([statusList, prioritys]) => {
      this.setState({ statusList, prioritys });
    });
    getCycleTree(assignedTo).then((data) => {
      // traverseTree({ title: '所有版本', key: '0', children: data.versions });
      TestExecuteStore.setTreeData(data.versions);
      this.setState({
        loading: false,
      });
      this.generateList(data.versions);

      // window.console.log(dataList);
    });
    // 如果选中了项，就刷新table数据
    const currentCycle = TestExecuteStore.getCurrentCycle;
    if (currentCycle.cycleId) {
      this.loadCycle(null, { node: { props: { data: currentCycle } } }, true);
    }
  }


  loadCycle = (selectedKeys, {
    selected, selectedNodes, node, event,
  } = {}, flag) => {
    if (selectedKeys) {
      TestExecuteStore.setSelectedKeys(selectedKeys);
    }
    const data = node ? node.props.data : TestExecuteStore.getCurrentCycle;
    if (data.cycleId) {
      // 切换时，将分页回到第一页
      if (data.cycleId !== TestExecuteStore.getCurrentCycle.cycleId) {
        TestExecuteStore.setExecutePagination({ current: 1 });
      }
      TestExecuteStore.setCurrentCycle(data);
      if (data.type === 'folder' || data.type === 'cycle') {
        if (!flag) {
          this.setState({
            tableLoading: true,
          });
        }
        this.loadExecutes(data);
      }
    }
  }


  /**
   *右侧reload
   *
   * @memberof TestExecuteHomeContainer
   */
  loadExecutes = () => {
    const currentCycle = TestExecuteStore.getCurrentCycle;
    const { cycleId, type } = currentCycle;
    const { treeAssignedTo } = TestExecuteStore;
    const executePagination = TestExecuteStore.getExecutePagination;
    const { filters } = this.state;
    const targetPage = executePagination.current;
    getExecutesByCycleId({
      page: targetPage,
      size: executePagination.pageSize,
    }, cycleId,
    {
      ...filters,
      lastUpdatedBy: [Number(this.lastUpdatedBy)],
      assignedTo: [treeAssignedTo || Number(this.assignedTo)],
    }, type).then((res) => {
      TestExecuteStore.setExecutePagination({
        current: res.pageNum,
        pageSize: res.pageSize,
        total: res.total,
      });
      this.setState({
        tableLoading: false,
        testList: res.list,
      });
    });
  }

  reloadExecutes = () => {
    this.setState({
      tableLoading: true,
    });
    this.loadExecutes();
  }

  generateList = (data) => {
    for (let i = 0; i < data.length; i += 1) {
      const node = data[i];
      const { key, title } = node;
      // 找出url上的cycleId
      const { cycleId } = getParams(window.location.href);
      const currentCycle = TestExecuteStore.getCurrentCycle;
      if (!currentCycle.cycleId && Number(cycleId) === node.cycleId) {
        this.setExpandDefault(node);
      } else if (currentCycle.cycleId === node.cycleId) {
        TestExecuteStore.setCurrentCycle(node);
      }
      dataList.push({ key, title });
      if (node.children) {
        this.generateList(node.children, node.key);
      }
    }
  }


  filterCycle = (value) => {
    // window.console.log(value);
    if (value !== '') {
      const expandedKeys = dataList.map((item) => {
        if (item.title.indexOf(value) > -1) {
          return getParentKey(item.key);
        }
        return null;
      }).filter((item, i, self) => item && self.indexOf(item) === i);
      TestExecuteStore.setExpandedKeys(expandedKeys);
    }
    this.setState({
      treeSearchValue: value,
      autoExpandParent: true,
    });
  }

  // 默认展开并加载右侧数据
  setExpandDefault = (defaultExpandKeyItem) => {
    if (defaultExpandKeyItem) {
      TestExecuteStore.setExpandedKeys([getParentKey(defaultExpandKeyItem.key)]);
      TestExecuteStore.setSelectedKeys([defaultExpandKeyItem.key]);
      TestExecuteStore.setCurrentCycle(defaultExpandKeyItem);
      this.setState({
        autoExpandParent: true,
        tableLoading: true,
      });
      this.loadExecutes();
    }
  }


  quickPass = (execute, e) => {
    e.stopPropagation();
    this.quickPassOrFail(execute, '通过');
  }

  quickFail = (execute, e) => {
    e.stopPropagation();
    this.quickPassOrFail(execute, '失败');
  }

  quickPassOrFail = (execute, text) => {
    const cycleData = { ...execute };
    if (_.find(this.state.statusList, { projectId: 0, statusName: text })) {
      cycleData.executionStatus = _.find(this.state.statusList, { projectId: 0, statusName: text }).statusId;
      delete cycleData.defects;
      delete cycleData.caseAttachment;
      delete cycleData.testCycleCaseStepES;
      delete cycleData.lastRank;
      delete cycleData.nextRank;
      cycleData.assignedTo = cycleData.assignedTo || 0;
      // 加载所有数据，因为进度条需要更新
      this.setState({
        loading: true,
      });
      editCycle(cycleData).then((Data) => {
        this.loadTreeAndExecute();
      }).catch((error) => {
        this.setState({
          loading: false,
        });
        Choerodon.prompt('网络错误');
      });
    } else {
      Choerodon.prompt('未找到对应状态');
    }
  }

  handleTreeNodeExpand = (expandedKeys) => {
    TestExecuteStore.setExpandedKeys(expandedKeys);
    this.setState({
      autoExpandParent: false,
    });
  }

  handleRefreshClick = () => {
    this.loadTreeAndExecute();
  }

  handleExecuteByChange = (lastUpdatedBy) => {
    this.lastUpdatedBy = lastUpdatedBy;
    this.loadCycle();
  }

  handleAssignedToChange = (assignedTo) => {
    this.assignedTo = assignedTo;
    this.loadCycle();
  }

  handleTreeAssignedToChange = (e) => {
    let assignedTo = 0;
    if (e.target.value === 'my') {
      assignedTo = AppState.userInfo.id;
      TestExecuteStore.setTreeAssignedTo(assignedTo);
      this.loadTreeAndExecute(assignedTo);
    } else {
      TestExecuteStore.setTreeAssignedTo(0);
      this.loadTreeAndExecute(0);
    }
  }

  /**
   * 点击table的一项
   *
   * @memberof TestPlanHome
   */
  handleTableRowClick = (record) => {
    const currentCycle = TestExecuteStore.getCurrentCycle;
    const { cycleId, type } = currentCycle;
    const isCycle = type === 'cycle';
    const { history } = this.props;
    history.push(executeDetailLink(record.executeId, isCycle ? cycleId : null));
  }

  handleExecuteTableChange = (pagination, filters, sorter, barFilters) => {
    const Filters = { ...filters };
    if (barFilters && barFilters.length > 0) {
      Filters.summary = barFilters;
    }
    TestExecuteStore.setExecutePagination(pagination);
    this.setState({
      tableLoading: true,
      filters: Filters,
    }, () => {
      this.loadExecutes();
    });
  }

  render() {
    const treeData = TestExecuteStore.getTreeData;
    const expandedKeys = TestExecuteStore.getExpandedKeys;
    const selectedKeys = TestExecuteStore.getSelectedKeys;
    const currentCycle = TestExecuteStore.getCurrentCycle;
    const { leftVisible, treeAssignedTo } = TestExecuteStore;
    const executePagination = TestExecuteStore.getExecutePagination;

    return (
      <TestExecuteHome
        {...this.state}
        treeData={treeData}
        expandedKeys={expandedKeys}
        selectedKeys={selectedKeys}
        currentCycle={currentCycle}
        leftVisible={leftVisible}
        treeAssignedTo={treeAssignedTo}
        executePagination={executePagination}
        onRefreshClick={this.handleRefreshClick}
        quickPass={this.quickPass}
        quickFail={this.quickFail}
        onTreeAssignedToChange={this.handleTreeAssignedToChange}
        onTreeNodeExpand={this.handleTreeNodeExpand}
        onTreeNodeSelect={this.loadCycle}
        filterCycle={this.filterCycle}
        onExecuteByChange={this.handleExecuteByChange}
        onAssignedToChange={this.handleAssignedToChange}
        onExecuteTableChange={this.handleExecuteTableChange}
        onTableRowClick={this.handleTableRowClick}
      />
    );
  }
}
export default TestExecuteHomeContainer;
