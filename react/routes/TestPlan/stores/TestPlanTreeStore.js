/* eslint-disable no-param-reassign */
import {
  observable, action, computed, toJS,
} from 'mobx';
import { find } from 'lodash';
import { getPlanTree } from '@/api/TestPlanApi';
// import { getIssueTree } from '@/api/IssueManageApi';
class TestPlanTreeStore {
  @observable testPlanStatus = 'todo';

  @action setTestPlanStatus = (testPlanStatus) => {
    this.testPlanStatus = testPlanStatus;
  }

  @observable currentPlanId = undefined;

  @action setCurrentPlanId = (currentPlanId) => {
    this.currentPlanId = currentPlanId;
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
    return toJS(this.currentCycle);
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
    for (let i = 0; i < treeFolders.length; i++) {
      if (!treeFolders[i].topLevel && treeFolders[i].children && treeFolders[i].children.length && treeFolders[i].children.includes(folderId) && !rootIds.includes(folderId)) {
        parent = treeFolders[i];
        if (parent.data.parentId) {
          return this.getParent(rootIds, treeFolders, parent.id);
        } else {
          console.log(i, treeFolders[i]);
          return parent;
        }
      }
    }
    return parent;
  };

  @action setTreeData(treeData, defaultSelectId) {
    const { rootIds, treeFolder } = treeData;
    // 选中之前选中的
    let selectedId = this.currentCycle ? this.currentCycle.id : undefined;
    if (!this.currentCycle.id && rootIds && rootIds.length > 0) {
      selectedId = defaultSelectId ? Number(defaultSelectId) : rootIds[0];      
    }
    this.treeData = {
      rootIds: rootIds.slice(0, 4) || [],
      treeFolder: (treeFolder && treeFolder.map((folder) => {
        const {
          issueFolderVO, expanded, children, ...other 
        } = folder;
        return {
          children: children || [],
          data: issueFolderVO,
          isExpanded: expanded,
          selected: folder.id === selectedId,
          ...other,
        };
      })) || [],
    };
    if (selectedId) {
      const planId = (this.getParent(this.treeData.rootIds, this.treeData.treeFolder, selectedId) && this.getParent(this.treeData.rootIds, this.treeData.treeFolder, selectedId).id) || selectedId;
      this.setCurrentPlanId(planId);
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
