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

  @observable rootIds=[]

  @observable selectedKeys = [];

  @observable currentFolder = {};

  @observable loading = false;

  treeRef = null;

  @action clearStore = () => {
    this.currentFolder = {};
    this.treeData = {
      rootIds: [],
      treeFolder: [],
    };
    this.rootIds = [];
    this.treeRef = null;
  }

  @computed get getTreeData() {
    return toJS(this.treeData);
  }

  @computed get getSelectedKeys() {
    return toJS(this.selectedKeys);
  }

  @computed get getCurrentFolder() {
    return toJS(this.currentFolder);
  }

  @computed get getPreFolder() {
    return toJS(this.preFolder);
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
    let selectedId = this.currentFolder ? this.currentFolder.id : undefined;
    if (!this.currentFolder.id && rootIds.length > 0) {
      selectedId = defaultSelectId || rootIds[0];
    }
    this.treeData = {
      rootIds,
      treeFolder: treeFolder.map((folder) => {
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
      }),
    };
    this.rootIds = [...rootIds];
    if (selectedId) {
      this.setCurrentFolder(find(this.treeData.treeFolder, { id: selectedId }) || {});
    }
  }

  @action addRootItem(folderId) {
    this.rootIds.push(folderId);
  }

  @action removeRootItem(folderId) {
    pull(this.rootIds, folderId);
  }

  @action setCurrentFolder(currentFolder) {
    this.currentFolder = currentFolder;
  }

  @action setCurrentFolderById(id) {
    const data = find(this.treeData.treeFolder, { id });
    if (data) {
      this.setCurrentFolder(data);
    }
  }

  @action setLoading = (loading) => {
    this.loading = loading;
  }

  @action setTreeRef = (treeRef) => {
    this.treeRef = treeRef;
  }

  updateHasCase(itemId, flag) {
    if (this.treeRef && this.treeRef.current) {
      const item = this.treeRef.current.getItem(itemId);
      if (item && !item.hasCase === flag) {
        this.treeRef.current.updateTree(itemId, { hasCase: flag });
      }
    }
  }

  @action
  updateChildren(itemId, folderId) {
    const target = find(this.treeData.treeFolder, { id: itemId });
    if (target && target.children) {
      target.children.push(folderId);
    }
  }  
}

export default new IssueTreeStore();
