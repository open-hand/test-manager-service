/* eslint-disable no-param-reassign */
import {
  observable, action, computed, toJS, set,
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

  // 用于进行根目录判断
  treeRootMap = observable.map();

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

  async loadIssueTree(initCaseSelected) {
    this.setLoading(true);
    const treeData = await getIssueTree();
    this.setTreeData(treeData, initCaseSelected);
    this.setLoading(false);
  }

  @action setTreeData(treeData, initCaseSelected) {
    const { rootIds, treeFolder } = treeData;
    // 选中之前选中的
    const selectedId = this.currentCycle.id || rootIds[0];
    // 保存根目录 用于后续递归选择时判断根节点
    rootIds.forEach((r) => {
      this.treeRootMap.set(r, true);
    });
    const newTreeFolder = [];
    for (let index = 0; index < treeFolder.length; index += 1) {
      const folder = treeFolder[index];
      if (folder.issueFolderVO.initStatus === 'creating' || folder.issueFolderVO.initStatus === 'fail') {
        this.treeRootMap.delete(folder.id);
      } else {
        const {
          issueFolderVO, expanded, children, caseCount, ...other
        } = folder;
        const result = {
          children: children || [],
          data: issueFolderVO,
          isExpanded: expanded,
          selected: folder.id === selectedId,
          ...other,
        };
        this.treeMap.set(folder.id, {
          id: folder.id,
          children: children || [],
          data: issueFolderVO,
          caseCount,
          checked: false,
          isIndeterminate: false,
          selected: [],
          unSelected: [],
          mainSelect: false,
          mainUnSelect: false,
        });
        newTreeFolder.push(result);
      }
    }
    this.treeData = {
      rootIds: [...this.treeRootMap.keys()],
      treeFolder: newTreeFolder,
    };
    if (selectedId) {
      this.setCurrentCycle(find(this.treeData.treeFolder, { id: selectedId }) || {});
    }
    // 数据初始化之后，设置选中的值
    if (initCaseSelected) {
      Object.keys(initCaseSelected).forEach((key) => {
        const folderId = key;
        this.handleCheckChange(true, folderId);
        const mapData = this.treeMap.get(folderId);
        if (mapData) {
          mapData.selected = initCaseSelected[key].selected;
          mapData.unSelected = initCaseSelected[key].unSelected;
        }
      });
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
    const { data: { parentId, children } } = item;
    // 如果
    if (item.checked === checked) {
      return;
    }
    this.setItemCheck(item, checked);
    // 处理子集
    this.autoHandleChildren(item, checked);
    // 处理父级
    if (!this.treeRootMap.get(folderId)) {
      this.autoHandleParent(parentId, checked);
    }
  }

  // 递归自动选中或取消所有子目录
  @action autoHandleChildren(item, checked) {
    item.isIndeterminate = false;
    item.children.forEach((folderId) => {
      const child = this.treeMap.get(folderId);
      const { children } = child;
      this.setItemCheck(child, checked);
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
      this.setItemCheck(item, true);
    } else {
      // 如果有一个子选中，就选中
      this.setItemCheck(item, children.some((childId) => this.treeMap.get(childId).checked));
    }
    // 如果有一个子没选中，就是中间态
    const isIndeterminate = item.checked ? children.some((childId) => !this.treeMap.get(childId).checked) : false;
    item.isIndeterminate = isIndeterminate;
    if (!this.treeRootMap.get(id)) {
      this.autoHandleParent(parentId, checked);
    }
  }

  @action setItemCheck(item, checked) {
    item.checked = checked;
    item.selected = [];
    item.unSelected = [];
    item.mainSelect = false;
    item.mainUnSelect = false;
  }

  // 选中单个case的处理
  @action
  addFolderSelectedCase(folderId, caseId) {
    const item = this.treeMap.get(folderId);
    // 已未选中为主
    if (item.mainUnSelect) {
      // 从取消选中去掉，代表选中
      pull(item.unSelected, caseId);
    } else {
      // 以选中为主
      if (!item.mainSelect) {
        set(item, { selected: [] });
        item.mainSelect = true;
      }
      if (!item.selected.includes(caseId)) {
        item.selected.push(caseId);
      }
    }
  }

  // 取消单个选中
  @action
  removeFolderSelectedCase(folderId, caseId) {
    const item = this.treeMap.get(folderId);
    // 已选中为主
    if (item.mainSelect) {
      // 从选中去掉，代表未选中
      pull(item.selected, caseId);
      // 如果全移除了，就取消树的选中
      if (item.selected.length === 0) {
        this.handleCheckChange(false, folderId);
      }
    } else {
      // 以未选中为主
      if (!item.unSelected) {
        set(item, { unSelected: [] });
        item.mainUnSelect = true;
      }
      item.unSelected.push(caseId);
      if (item.caseCount === item.unSelected.length) {
        this.handleCheckChange(false, folderId);
      }
    }
  }

  getSelectedFolders() {
    const result = {};
    for (const [id, item] of this.treeMap) {
      // 只取树最后一层的目录
      if (item.checked && item.children.length === 0) {
        const {
          unSelected, selected, mainSelect, mainUnSelect,
        } = item;
        // 有一个就是custom
        const custom = mainSelect || mainUnSelect;
        if (!custom) {
          result[id] = {
            custom: false,
          };
        } else if (mainUnSelect) {
          result[id] = {
            custom: true,
            unSelected,
          };
        } else {
          result[id] = {
            custom: true,
            selected,
          };
        }
      }
    }
    return result;
  }

  // 获取当前选中的issue数量
  @computed get getSelectedIssueNum() {
    const selectedFolders = this.getSelectedFolders();
    return Object.keys(selectedFolders).reduce((total, key) => {
      const folderId = key;
      const item = this.treeMap.get(folderId);
      if (item.mainSelect) {
        total += item.selected.length;
      } else {
        total += item.caseCount || 0;
        if (item.unSelected) {
          total -= item.unSelected.length;
        }
      }
      return total;
    }, 0);
  }
}

export default IssueTreeStore;
