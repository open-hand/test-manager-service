/* eslint-disable no-param-reassign */
import {
  observable, action, computed, toJS,
} from 'mobx';
import { find, pull } from 'lodash';
import { getIssueTree } from '@/api/IssueManageApi';

class IssueTreeStore {
  @observable treeData = {
    rootIds: [],
    treeFolder: [],
  }

  @observable selectedKeys = [];

  @observable currentCycle = {};

  @observable loading = false;

  treeRef = null;

  // 使用map，方便查找访问
  treeMap = observable.map();

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
    this.setLoading(true);
    const treeData = await getIssueTree();
    this.setTreeData(treeData, defaultSelectId);
    this.setLoading(false);
  }

  @action setTreeData(treeData, defaultSelectId) {
    const { rootIds, treeFolder } = treeData;
    // 选中之前选中的
    let selectedId = this.currentCycle ? this.currentCycle.id : undefined;
    if (!this.currentCycle.id && rootIds.length > 0) {
      selectedId = defaultSelectId ? Number(defaultSelectId) : rootIds[0];
    }
    this.treeData = {
      rootIds,
      treeFolder: treeFolder.map((folder) => {
        const {
          issueFolderVO, expanded, children, ...other
        } = folder;
        const result = {
          children: children || [],
          data: issueFolderVO,
          isExpanded: expanded,
          selected: folder.id === selectedId,
          checked: false,
          isIndeterminate: false,
          ...other,
        };
        this.treeMap.set(folder.id, result);
        return result;
      }),
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

  @action setLoading = (loading) => {
    this.loading = loading;
  }

  @action setTreeRef = (treeRef) => {
    this.treeRef = treeRef;
  }

  @action handleCheckChange(checked, folderId) {
    const item = this.treeMap.get(folderId);
    const { data: { parentId } } = item;
    // 如果
    if (item.checked === checked && !item.isIndeterminate) {
      return;
    }
    item.checked = checked;
    // 处理子集
    this.autoHandleChildren(item, checked);
    // 处理父级      
    if (parentId) {
      this.autoHandleParent(parentId, checked);
    }
  }

  // 递归自动选中或取消所有子文件夹
  @action autoHandleChildren(item, checked) {
    item.isIndeterminate = false;
    item.children.forEach((folderId) => {
      const child = this.treeMap.get(folderId);
      const { children } = child;
      child.checked = checked;
      if (children.length > 0) {
        this.autoHandleChildren(child, checked);
      }
    });
  }

  @action autoHandleParent(id, checked) {
    const item = this.treeMap.get(id);
    const { children, data: { parentId } } = item;
    // 子选中，父一定选中
    if (checked) {
      item.checked = true;
    } else {
      // 如果有一个子选中，就选中
      item.checked = children.some(childId => this.treeMap.get(childId).checked);
    }
    // 如果有一个子没选中，就是中间态
    const isIndeterminate = item.checked ? children.some(childId => !this.treeMap.get(childId).checked) : false;
    item.isIndeterminate = isIndeterminate;
    if (parentId) {
      this.autoHandleParent(parentId, checked);
    }
  }

  // 选中单个case的处理
  addFolderSelectedCase(folderId, caseId) {
    const item = this.treeMap.get(folderId);
    // 已未选中为主
    if (item.unSelectedCases) {
      // 从取消选中去掉，代表选中
      pull(item.unSelectedCases, caseId);    
    } else {
      // 以选中为主
      if (!item.selectedCases) {
        item.selectedCases = [];
      }
      item.selectedCases.push(caseId);
    }
  }

  // 取消单个选中
  removeFolderSelectedCase(folderId, caseId) {
    const item = this.treeMap.get(folderId);
    // 已选中为主
    if (item.selectedCases) {
      // 从选中去掉，代表未选中
      pull(item.selectedCases, caseId);
      // 如果全移除了，就取消树的选中
      if (item.selectedCases.length === 0) {
        this.handleCheckChange(false, folderId);
      }
    } else {
      // 以未选中为主
      if (!item.unSelectedCases) {
        item.unSelectedCases = [];
      }
      item.unSelectedCases.push(caseId);
    }
  }

  getSelectedFolders() {
    const result = {};
    for (const [id, item] of this.treeMap) {
      // 只取树最后一层的文件夹
      if (item.checked && item.children.length === 0) {
        const { unSelectedCases, selectedCases } = item;
        result[id] = {
          unSelectedCases, selectedCases,
        };
      }
    }
    return result;
  }
}

export default IssueTreeStore;
