import {
  observable, action, computed, toJS,
} from 'mobx';

class AutoListStore {
  @observable loading = true;

  @observable autoRefresh = localStorage.getItem('testManager.AutoList.autoRefresh') || false;

  @observable appList = [];

  @observable historyList = [];

  @observable envList = [];

  @observable currentApp = null;

  @observable selectLoading = false;

  @observable pagination = {
    current: 1,
    total: 0,
    pageSize: 10,
  };

  @observable filter = {};

  @action
  clear = () => {
    this.appList = [];
    this.historyList = [];
    this.envList = [];
    this.pagination = {
      current: 1,
      total: 0,
      pageSize: 10,
    };
    this.currentApp = null;
  }

  @action
  Loading = () => {
    this.loading = true;
  }

  @action
  unLoading = () => {
    this.loading = false;
  }

  @action setAutoRefresh(autoRefresh) {
    this.autoRefresh = autoRefresh;
  }

  @action setAppList = (appList) => {
    this.appList = appList;
  }

  @action setHistoryList = (historyList) => {
    this.historyList = historyList;
  }

  @action setEnvList = (envList) => {
    this.envList = envList;
  }

  @action setCurrentApp = (currentApp) => {
    this.currentApp = currentApp;
  }

  @action setSelectLoading = (selectLoading) => {
    this.selectLoading = selectLoading;
  }

  @action setPagination = (pagination) => {
    this.pagination = pagination;
  }

  @action setFilter = (filter) => {
    this.filter = filter;
  }
}

export default new AutoListStore();
