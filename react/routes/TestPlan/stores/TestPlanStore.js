import {
  observable, action, computed, toJS,
} from 'mobx';
import { Choerodon } from '@choerodon/boot';
import moment from 'moment';
import { getCycleTree, getExecutesByCycleId } from '../../../api/cycleApi';
import { getStatusList } from '../../../api/TestStatusApi';
import { getPrioritys } from '../../../api/agileApi';
import BaseTreeProto from '../../../store/BaseTreeProto';
/**
 * 非递归遍历树 将测试阶段按照时间排序
 *
 * @param {*} node
 * @returns
 */
function traverseTree(node) {
  if (!node) {
    return;
  }
  const stack = [];
  stack.push(node);
  let tmpNode;
  while (stack.length > 0) {
    tmpNode = stack.pop();
    const { type, key } = tmpNode;
    if (key.split('-').length === 3 || type === 'cycle') {
      tmpNode.children = tmpNode.children.sort((a, b) => {
        if (moment(a.fromDate).isAfter(moment(b.fromDate))) {
          return 1;
        } else {
          return -1;
        }
      }).map((child, i) => ({ ...child, key: `${key}-${i}` }));
    }
    if (tmpNode.children && tmpNode.children.length > 0) {
      let i = tmpNode.children.length - 1;
      for (i = tmpNode.children.length - 1; i >= 0; i -= 1) {
        stack.push(tmpNode.children[i]);
      }
    }
  }
}
const reorder = (list, startIndex, endIndex) => {
  const result = Array.from(list);
  const [removed] = result.splice(startIndex, 1);
  result.splice(endIndex, 0, removed);

  return result;
};
class TestPlanStore extends BaseTreeProto {
  @observable isTreeVisible = true;

  @observable prioritys = [];

  @observable statusList = [];

  @observable times = [];

  @observable dataList = [];

  @observable testList = [];

  @observable executePagination = {
    current: 1,
    total: 0,
    pageSize: 50,
  }

  @observable loading = false;

  @observable filters = {};

  @observable rightLoading = false;

  @observable calendarShowMode = 'multi';

  @observable CreateCycleVisible = false;

  @observable EditCycleVisible = false;

  @observable CurrentEditCycle = {};

  @observable EditStageVisible = false;

  @observable CurrentEditStage = {};

  @observable assignedTo = null;

  @observable lastUpdatedBy = null;

  @observable exportVersionId = null;

  @observable exportCycleId = null;

  @observable exportStageId = null;

  @action clearStore = () => {
    this.isTreeVisible = true;
    this.prioritys = [];
    this.statusList = [];
    this.times = [];
    this.dataList = [];
    this.testList = [];
    this.executePagination = {
      current: 1,
      total: 0,
      pageSize: 50,
    };
    this.loading = false;
    this.filters = {};
    this.rightLoading = false;
    this.calendarShowMode = 'multi';
    this.CreateCycleVisible = false;
    this.EditCycleVisible = false;
    this.CurrentEditCycle = {};
    this.EditStageVisible = false;
    this.CurrentEditStage = {};
    this.assignedTo = null;
    this.lastUpdatedBy = null;
    this.treeData = [];
    this.expandedKeys = ['0-0'];
    this.selectedKeys = [];
    this.addingParent = null;
    this.currentCycle = {};
    this.preCycle = {};
    this.exportVersionId = null;
    this.exportCycleId = null;
    this.exportStageId = null;
  }

  getTree = () => new Promise((resolve) => {
    this.enterLoading();
    Promise.all([
      getPrioritys(),
      getStatusList('CYCLE_CASE'),
      getCycleTree(),
    ]).then(([prioritys, statusList, data]) => {
      this.setPrioritys(prioritys);
      this.setStatusList(statusList);
      // traverseTree({ title: '所有版本', key: '0', children: data.versions });
      this.setTreeData(data.versions);
      this.generateList(data.versions);
      // 默认选中一个项
      if (data.versions && data.versions.length > 0) {
        this.selectDefaultNode(data.versions[0]);
      }
      resolve();
    }).catch((err) => {     
      Choerodon.prompt('网络错误');
    }).finally(() => {
      this.leaveLoading();
    });
    // 如果选中了项，就刷新table数据
    const currentCycle = this.getCurrentCycle;
    if (currentCycle.versionId) {
      this.reloadCycle();
    }
  })

  selectDefaultNode = (node) => {
    if (!this.currentCycle.key) {
      // this.setCurrentCycle(node);
      this.loadCycle(node.key, { node: { props: { data: node } } });
    }
  }

  selectDefaultVersion = (versionId) => {
    const targetVersion = this.dataList.find((item => item.versionId === versionId && item.key.split('-').length === 3));
    if (targetVersion) {
      this.setCurrentCycle(targetVersion);
      this.setExpandedKeys([...this.expandedKeys, targetVersion.key]);
      this.setSelectedKeys([targetVersion.key]);
    }
  }

  reloadCycle = () => {
    const data = this.getCurrentCycle;
    const { executePagination, filters } = this;
    if (data.type === 'folder') {
      getExecutesByCycleId({
        page: executePagination.current,
        size: executePagination.pageSize,
      }, data.cycleId,
      {
        ...filters,
        lastUpdatedBy: [Number(this.lastUpdatedBy) || null],
        assignedTo: [Number(this.assignedTo) || null],
      }).then((cycle) => {
        this.rightLeaveLoading();
        this.setTestList(cycle.list);
        this.setExecutePagination({
          current: executePagination.current,
          pageSize: executePagination.pageSize,
          total: cycle.total,
        });
      });
    }
  }

  // 点击树的一项，加载数据
  loadCycle = (selectedKeys, {
    selected, selectedNodes, node, event,
  } = {}) => {
    const { executePagination, filters } = this;
    const data = node ? node.props.data : this.getCurrentCycle;
    // 点所有的都生成事件日历
    this.updateTimes([data]);
    if (data.type === 'folder') {
      this.setCalendarShowMode('single');
    } else {
      this.setFilters({});
      this.setCalendarShowMode('multi');
    }
    // if (data.versionId) {
    if (selectedKeys) {
      this.setSelectedKeys(selectedKeys);
    }

    if (data.key) {
      if (data.key.split('-').length === 3) {
        this.setExportVersionId(data.versionId);
      }
      if (data.key.split('-').length === 4) {
        this.setExportVersionId(data.versionId);
        this.setExportCycleId(data.cycleId);
      }
      if (data.key.split('-').length === 5) {
        this.setExportVersionId(data.versionId);
        this.setExportCycleId(data.parentCycleId);
        this.setExportStageId(data.cycleId);
      }
    }

    this.rightEnterLoading();
    this.setCurrentCycle(data);

    // window.console.log(data);
    if (data.type === 'folder') {
      getExecutesByCycleId({
        page: 1,
        size: executePagination.pageSize,
      }, data.cycleId,
      {
        ...filters,
        lastUpdatedBy: [Number(this.lastUpdatedBy) || null],
        assignedTo: [Number(this.assignedTo) || null],
      }).then((cycle) => {
        this.rightLeaveLoading();
        this.setTestList(cycle.list);
        this.setExecutePagination({
          current: 1,
          pageSize: executePagination.pageSize,
          total: cycle.total,
        });
      });
    }
    // }
  }

  getParentKey = (key, tree) => key.split('-').slice(0, -1).join('-')

  getParent = (key) => {
    const parentKey = this.getParentKey(key);
    const arr = parentKey.split('-').slice(1);
    let temp = this.treeData;
    arr.forEach((index, i) => {
      // window.console.log(temp);
      if (i === 0) {
        temp = temp[index];
      } else {
        temp = temp.children[index];
      }
    });
    return temp;
  }

  generateList = (data) => {
    for (let i = 0; i < data.length; i += 1) {
      const node = data[i];
      const { key, title, versionId } = node;
      // 找出url上的cycleId
      // const { cycleId } = getParams(window.location.href);
      const currentCycle = this.getCurrentCycle;
      // if (!currentCycle.cycleId && Number(cycleId) === node.cycleId) {
      //   this.setExpandDefault(node);
      // } else
      // 两种情况，version或者cycle和文件夹，version没有type
      if (currentCycle.key === node.key) {
        //
        this.updateTimes([node]);
        this.setCurrentCycle(node);
      }
      this.dataList.push({ key, title, versionId });
      if (node.children) {
        this.generateList(node.children, node.key);
      }
    }
  }


  @action setIsTreeVisible = (isTreeVisible) => {
    this.isTreeVisible = isTreeVisible;
  }

  @action setAssignedTo(assignedTo) {
    this.assignedTo = assignedTo;
  }

  @action setLastUpdatedBy(lastUpdatedBy) {
    this.lastUpdatedBy = lastUpdatedBy;
  }

  @action setCalendarShowMode = (calendarShowMode) => {
    this.calendarShowMode = calendarShowMode;
  }

  @action setFilters = (filters) => {
    this.filters = filters;
  }

  @action setTimes = (times) => { 
    this.times = times;
  }

  updateTimes = (data) => {
    const times = [];
    this.generateTimes(data, times);
    // console.log(times);
    this.setTimes(times);
  }

  generateTimes = (data, times) => {
    for (let i = 0; i < data.length; i += 1) {
      const node = data[i];
      const {
        fromDate, toDate, title, type, children, versionId, cycleId,
      } = node;

      if (!versionId && !cycleId && children.length > 0) {
        // 版本 规划中
        let stepChildren = [];
        children.forEach((child) => {
          stepChildren = [...stepChildren, ...child.children];
        });
        // 进行过滤，防止时间为空
        const starts = stepChildren.filter(child => child.fromDate && child.toDate).map(child => moment(child.fromDate));
        const ends = stepChildren.filter(child => child.fromDate && child.toDate).map(child => moment(child.toDate));
        if (starts.length > 0 && ends.length > 0) {
          times.push({
            ...node,
            // children, 不需要编辑，不用限制时间的选择，所有不用传children
            type: 'topversion',
            start: `${moment(moment.min(starts)).format('YYYY-MM-DD')} 00:00:00`,
            end: `${moment(moment.max(ends)).format('YYYY-MM-DD')} 23:59:59`,
          });
        }
      } else if (versionId && !cycleId && children.length > 0) {
        // 版本 0.1.1
        // console.log(children);
        const starts = children.filter(child => child.fromDate && child.toDate).map(child => moment(child.fromDate));
        const ends = children.filter(child => child.fromDate && child.toDate).map(child => moment(child.toDate));
        if (starts.length > 0 && ends.length > 0) {
          times.push({
            ...node,
            // children,
            type: 'version',
            start: `${moment(moment.min(starts)).format('YYYY-MM-DD')} 00:00:00`,
            end: `${moment(moment.max(ends)).format('YYYY-MM-DD')} 23:59:59`,
          });
        }
      } else if (fromDate && toDate) {
        times.push({
          ...node,
          children,
          start: `${moment(moment.min(fromDate)).format('YYYY-MM-DD')} 00:00:00`,
          end: `${moment(moment.max(toDate)).format('YYYY-MM-DD')} 23:59:59`,          
        });
      }

      if (node.children) {
        this.generateTimes(node.children, times);
      }
    }
  }

  @action setPrioritys(prioritys) {
    this.prioritys = prioritys;
  }

  @action setStatusList(statusList) {
    this.statusList = statusList;
  }

  @action enterLoading() {
    this.loading = true;
  }

  @action leaveLoading() {
    this.loading = false;
  }

  @action rightEnterLoading() {
    this.rightLoading = true;
  }

  @action rightLeaveLoading() {
    this.rightLoading = false;
  }

  @action setTestList = (testList) => {
    this.testList = testList;
  }

  @action setExecutePagination = (executePagination) => {
    this.executePagination = executePagination;
  }

  @action setCreateCycleVisible = (CreateCycleVisible) => {
    this.CreateCycleVisible = CreateCycleVisible;
  }

  @action EditStage(CurrentEditStage) {
    const parent = this.getParent(CurrentEditStage.key);
    const { fromDate, toDate } = parent;
    // 为阶段设置父元素时间段，来限制时间的选择
    this.CurrentEditStage = {
      ...CurrentEditStage,
      parentTime: { start: moment(fromDate), end: moment(toDate) },
    };
    this.EditStageVisible = true;
  }

  @action ExitEditStage() {
    this.CurrentEditStage = {};
    this.EditStageVisible = false;
  }

  @action EditCycle(CurrentEditCycle) {
    this.CurrentEditCycle = CurrentEditCycle;
    this.EditCycleVisible = true;
  }

  @action ExitEditCycle() {
    this.CurrentEditCycle = {};
    this.EditCycleVisible = false;
  }

  @computed get getTimes() {
    return toJS(this.times);
  }

  @computed get getTimesLength() {
    return this.times.length > 0;
  }

  @computed get getTestList() {
    return toJS(this.testList);
  }

  @action setExportVersionId(data) {
    this.exportVersionId = data;
  }

  @action setExportCycleId(data) {
    this.exportCycleId = data;
  }

  @action setExportStageId(data) {
    this.exportStageId = data;
  }

  @action resortTree(parent, soureIndex, targetIndex) {
    // eslint-disable-next-line no-param-reassign
    parent.children = reorder(parent.children, soureIndex, targetIndex);
  }
}

export default new TestPlanStore();
