/* eslint-disable no-param-reassign */
import {
  observable, action, computed, toJS,
} from 'mobx';
import moment from 'moment';
import { find, pull } from 'lodash';
import { getPlanTree } from '@/api/TestPlanApi';
// import { getIssueTree } from '@/api/IssueManageApi';
// 数据处理成tree形式，便于查看数据
function makeTree(rootIds, treeFolder) {
  const map = new Map(treeFolder.map(item => ([item.id, item])));
  const transverse = (item) => {
    item.children = item.children.map(id => transverse(map.get(id)));
    return item;
  };
  return transverse({
    id: 0,
    children: rootIds,
  });
}
class TestPlanTreeStore {
  @observable testPlanStatus = 'doing';

  @action setTestPlanStatus = (testPlanStatus) => {
    this.testPlanStatus = testPlanStatus;
  }

  @observable treeData = {
    rootIds: [],
    treeFolder: [],
  }

  @observable selectedKeys = [];

  @observable currentCycle = {};

  @observable treeLoading = false;

  treeRef = {};

  @action clearStore = () => {
    this.currentCycle = {};
    this.treeData = {
      rootIds: [],
      treeFolder: [],
    };
    this.treeRef = {};
  }

  @computed get getTreeData() {
    return toJS(this.treeData);
  }

  @computed get getSelectedKeys() {
    return toJS(this.selectedKeys);
  }

  @computed get getCurrentCycle() {
    return this.currentCycle;
  }

  getId(id = this.currentCycle.id) {
    if (!id) {
      return [id, ''];
    }
    if (typeof id === 'number') {
      return [id, ''];
    } else if (id.split('-').length === 2) {
      const [planId, folderId] = id.split('-');
      return [Number(planId), Number(folderId)];
    } else {
      return [id, ''];
    }
  }

  isPlan(id) {
    if (typeof id === 'number') {
      return true;
    } else if (id.split('-').length === 2) {
      return false;
    }
    return false;
  }

  @action removeRootItem(folderId) {
    pull(this.treeData.rootIds, folderId);
  }

  @computed get getCurrentPlanId() {
    const [planId, folderId] = this.getId();
    return planId;
  }

  @computed get getPreCycle() {
    return toJS(this.preCycle);
  }

  @action setSelectedKeys(selectedKeys) {
    this.selectedKeys = selectedKeys;
  }

  @observable times = [];

  @action setTimes = (times) => {
    this.times = times;
  }

  updateTimes = (data) => {
    const times = [];
    this.generateTimes(data, times);
    this.setTimes(times);
  }

  getAllChildren = parent => this.treeData.treeFolder.filter(item => parent.children.includes(item.id));

  generateTimes = (data, times, level = 0) => {
    for (let i = 0; i < data.length; i += 1) {
      const node = data[i];
      const {
        fromDate, toDate, children,
      } = node.data;

      times.push({
        ...node,
        children,
        type: this.isPlan(node.id) ? 'plan' : 'folder',
        start: `${moment(moment.min(fromDate)).format('YYYY-MM-DD')} 00:00:00`,
        end: `${moment(moment.max(toDate)).format('YYYY-MM-DD')} 23:59:59`,
        level,
      });

      if (node.children && node.children.length > 0) {
        this.generateTimes(this.getAllChildren(node), times, level + 1);
      }
    }
  }

  async loadIssueTree(defaultSelectId) {
    this.setTreeLoading(true);
    const treeData = await getPlanTree(this.testPlanStatus);
    this.setTreeData(treeData, defaultSelectId);
    this.setTreeLoading(false);
  }

  @action setTreeData(treeData, defaultSelectId) {
    const { flattenedTree } = this.treeRef.current || {};
    let flattenedTreeIds;
    if (flattenedTree) {
      flattenedTreeIds = flattenedTree.map(node => node.item).filter(item => item.isExpanded).map(item => item.id);
    }

    const { rootIds, treeFolder } = treeData;
    const firstRoot = (treeFolder && treeFolder.find(item => item.issueFolderVO.initStatus === 'success' && item.topLevel)) || {};
    // 选中之前选中的
    let selectedId = this.currentCycle ? this.currentCycle.id : undefined;
    if (!this.currentCycle.id && rootIds && rootIds.length > 0) {
      selectedId = defaultSelectId ? Number(defaultSelectId) : firstRoot.id;
    }
    this.treeData = {
      rootIds: rootIds || [],
      treeFolder: (treeFolder && treeFolder.map((folder) => {
        const {
          id, planId, issueFolderVO, expanded, children, ...other
        } = folder;
        return {
          id: planId ? `${planId}-${id}` : id,
          children: children ? children.map(child => `${planId || id}-${child}`) : [],
          data: issueFolderVO,
          isExpanded: (flattenedTreeIds && (flattenedTreeIds.includes(id) || flattenedTreeIds.includes(`${planId}-${id}`))) || expanded,
          selected: folder.id === selectedId,
          ...other,
        };
      })) || [],
    };
    // window.console.log(makeTree(toJS(this.treeData.rootIds), toJS(this.treeData.treeFolder)));
    if (selectedId) {
      const currentCycle = find(this.treeData.treeFolder, { id: selectedId }) || {};
      this.setCurrentCycle(currentCycle);
      this.updateTimes([currentCycle]);
    }
  }

  @action setCurrentCycle(currentCycle) {
    this.currentCycle = currentCycle;
  }

  @action setCurrentCycleById(id) {
    const data = find(this.treeData.treeFolder, { id });
    if (data) {
      this.setCurrentCycle(data);
    }
  }

  @action setTreeLoading = (treeLoading) => {
    this.treeLoading = treeLoading;
  }

  @action setTreeRef = (treeRef) => {
    this.treeRef = treeRef;
  }
}

export default TestPlanTreeStore;
