import {
  observable, computed, action, runInAction, autorun, 
} from 'mobx';
import { store } from '@choerodon/boot';
import priorityApi from '@/api/priority';

@store('PriorityStore')
class PriorityStore {
  @observable allPriority = [];

  @observable onLoadingList = false;

  @observable priorityList = [];

  @observable onCreatingPriority = false;

  @observable onEditingPriority = false;

  @computed get editingPriority() {
    return this.priorityList.find(item => item.id === this.editingPriorityId);
  }

  @action
  setPriorityList(newPriorityList) {
    this.priorityList = [...newPriorityList];
  }

  @computed
  get getPriorityList() {
    return this.priorityList.slice();
  }

  @action
  setOnLoadingList(state) {
    this.onLoadingList = state;
  }

  @action
  setOnCreatingPriority(state) {
    this.onCreatingPriority = state;
  }

  @action
  setOnEditingPriority(state) {
    this.onEditingPriority = state;
  }

  @action
  setEditingPriorityId(id) {
    this.editingPriorityId = id;
  }


  @action
  loadPriorityList = async (orgId) => {
    try {
      this.onLoadingList = true;
      const data = await priorityApi.load();
      runInAction(
        () => {
          this.priorityList = data;
        },
      );
    } catch (err) {
      throw err;
    } finally {
      runInAction(
        () => {
          this.onLoadingList = false;
        },
      );
    }
  };

  loadAllPriority = async (orgId) => {
    try {
      const data = await priorityApi.load();
      runInAction(
        () => {
          const { content } = data;
          this.allPriority = data;
        },
      );
    } catch (err) {
      throw err;
    }
  };
}

const priorityStore = new PriorityStore();

export default priorityStore;
