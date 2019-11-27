/* eslint-disable no-param-reassign */
import {
  observable, action, computed, toJS,
} from 'mobx';
import { find } from 'lodash';
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

  @action handleCheckChange(checked, item) {
    const { data: { parentId } } = item;
    if (checked) {
      item.checked = true;
      // 如果是子项目      
      if (parentId) {
        const parent = this.treeMap.get(parentId);
        this.handleParentWhenCheckChild(parent);
      } else {
        this.autoCheckChildren(item);
      }
    } else {
      item.checked = false;
      if (parentId) {
        const parent = this.treeMap.get(parentId);
        this.handleParentWhenUnCheckChild(parent);
      } else {
        this.autoUnCheckChildren(item);
      }
    }
  }

  @action autoCheckChildren(item) {
    item.isIndeterminate = false;
    item.children.forEach((folderId) => {
      this.treeMap.get(folderId).checked = true;
    });
  }

  @action autoUnCheckChildren(item) {
    item.isIndeterminate = false;
    item.children.forEach((folderId) => {
      this.treeMap.get(folderId).checked = false;
    });
  }

  @action handleParentWhenCheckChild(parent) {
    const { children } = parent;
    // 如果没有自动选中，就选中
    if (!parent.checked) {
      parent.checked = true;
    }
    // 如果有一个子没选中，就是中间态
    const isIndeterminate = children.some(childId => !this.treeMap.get(childId).checked);
    parent.isIndeterminate = isIndeterminate;
  }

  @action handleParentWhenUnCheckChild(parent) {
    const { children } = parent;
    // 如果有一个子选中，就选中
    const checked = children.some(childId => this.treeMap.get(childId).checked);
    parent.checked = checked;
    // 如果有一个子没选中，就是中间态
    const isIndeterminate = children.some(childId => !this.treeMap.get(childId).checked);
    parent.isIndeterminate = isIndeterminate;
  }
}

export default new IssueTreeStore();
