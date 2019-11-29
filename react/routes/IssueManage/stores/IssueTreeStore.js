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

  @observable currentFolder = {};

  @observable loading = false;

  treeRef = null;

  @action clearStore = () => {
    this.currentFolder = {};
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
      selectedId = defaultSelectId ? Number(defaultSelectId) : rootIds[0];      
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
    if (selectedId) {
      this.setCurrentFolder(find(this.treeData.treeFolder, { id: selectedId }) || {});
    }
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
}

export default new IssueTreeStore();
