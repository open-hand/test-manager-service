/* eslint-disable no-param-reassign */
import {
  observable, action, computed, toJS,
} from 'mobx';
import { find } from 'lodash';
import { getPlanTree } from '@/api/TestPlanApi';
// import { getIssueTree } from '@/api/IssueManageApi';
function getId(id) {
  if (!id) {
    return id;
  }
  if (typeof id === 'number') {
    return id;
  } else if (id.split('-').length === 2) {
    return Number(id.split('-')[0]);
  } else {
    return id;
  }
}
class TestPlanTreeStore {
  @observable testPlanStatus = 'todo';

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

  treeRef = null;

  @action clearStore = () => {
    this.currentCycle = {};
    this.treeData = {
      rootIds: [],
      treeFolder: [],
    };
    this.treeRef = null;
  }

  @computed get getTreeData() {
    return toJS(this.treeData);
  }

  @computed get getSelectedKeys() {
    return toJS(this.selectedKeys);
  }

  @computed get getCurrentCycle() {
    return { ...this.currentCycle, id: getId(this.currentCycle.id) };
  }

  @computed get getCurrentPlanId() {
    const { id } = this.currentCycle;
    const currentPlan = this.getParent(this.treeData.rootIds, this.treeData.treeFolder, id);  
    return currentPlan ? getId(currentPlan.id) : undefined;
  }

  @computed get getPreCycle() {
    return toJS(this.preCycle);
  }

  @action setSelectedKeys(selectedKeys) {
    this.selectedKeys = selectedKeys;
  }

  async loadIssueTree(defaultSelectId) {
    this.setTreeLoading(true);
    const treeData = await getPlanTree(this.testPlanStatus);
    this.setTreeData(treeData, defaultSelectId);
    this.setTreeLoading(false);
  }

  @action getParent = (rootIds, treeFolders, folderId) => {
    let parent;
    for (let i = 0; i < treeFolders.length; i += 1) {
      if (!treeFolders[i].topLevel && treeFolders[i].children && treeFolders[i].children.length && treeFolders[i].children.includes(folderId) && !rootIds.includes(folderId)) {
        parent = treeFolders[i];
        if (parent.data.parentId) {
          return this.getParent(rootIds, treeFolders, parent.id);
        } else {          
          return parent;
        }
      } else {
        return treeFolders[i];
      }
    }
    return parent;
  };

  @action setTreeData(treeData, defaultSelectId) {
    const { rootIds, treeFolder } = treeData;
    const planIds = rootIds.slice(0, 5).map(id => `${id}-plan`);
    // 选中之前选中的
    let selectedId = this.currentCycle ? this.currentCycle.id : undefined;
    if (!this.currentCycle.id && planIds.length > 0) {
      selectedId = defaultSelectId ? Number(defaultSelectId) : planIds[0];
    }
    this.treeData = {
      rootIds: planIds,
      treeFolder: treeFolder.map((folder) => {
        const {
          id, issueFolderVO, expanded, children, ...other
        } = folder;
        return {
          id: rootIds.includes(id) ? `${id}-plan` : id,
          children: children || [],
          data: issueFolderVO,
          isExpanded: expanded,
          selected: folder.id === selectedId,
          ...other,
        };
      }) || [],
    };
    if (selectedId) {
      this.setCurrentCycle(find(this.treeData.treeFolder, { id: selectedId }) || {});
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
