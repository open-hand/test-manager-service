import {
  observable, action, computed, toJS,
} from 'mobx';

class CreateAutoTestStore {
  @observable visible = false;

  @observable loading = false;

  @observable currentStep = 1;

  @observable appList = [];

  @observable app = {};

  @observable appVersion = {};

  @observable appVersionList = [];

  @observable version = {};

  @observable appVersionPagination = {
    current: 1,
    total: 0,
    pageSize: 10,
  }

  @observable env = {};

  @observable envList = [];

  @observable configValue = null;

  @observable newConfigValue = null;

  @action setVisible = (visible) => {
    this.visible = visible;
  }

  @action setLoading = (loading) => {
    this.loading = loading;
  }

  @action setApp = (app) => {
    this.app = app;
  }

  @action setAppList = (appList) => {
    this.appList = appList;
  }

  @action setAppVersionList = (appVersionList) => {
    this.appVersionList = appVersionList;
  }

  @action setEnvList = (envList) => {
    this.envList = envList;
  }

  @action setAppVersion = (appVersion) => {
    this.appVersion = appVersion;
  }

  @action setVersion = (version) => {
    this.version = version;
  }

  @action setAppVersionPagination = (appVersionPagination) => {
    this.appVersionPagination = { ...this.appVersionPagination, ...appVersionPagination };
  }

  @action setEnv = (env) => {
    this.env = env;
  }

  @action toStep = (step) => {
    this.currentStep = step;
  }

  @action nextStep = () => {
    this.currentStep += 1;
  }

  @action preStep = () => {
    this.currentStep -= 1;
  }

  /**
   * 取消部署，数据初始化
   *
   * @memberof CreateAutoTestStore
   */
  @action clearTestInfo = () => {
    this.currentStep = 1;
    this.appList = [];
    this.app = {};
    this.appVersion = {};
    this.appVersionList = [];
    this.version = {};
    this.appVersionPagination = {
      current: 1,
      total: 0,
      pageSize: 10,
    };
    this.env = {};
    this.envList = [];
    this.configValue = null;
    this.newConfigValue = null;
    this.visible = false;
  }

  @action setConfigValue(configValue) {
    this.configValue = configValue;
    this.newConfigValue = configValue;
  }

  @action setNewConfigValue(newConfigValue) {
    this.newConfigValue = { ...this.newConfigValue, yaml: newConfigValue };
  }

  @computed get getConfigValue() {
    return toJS(this.configValue);
  }

  @computed get getNewConfigValue() {
    return toJS(this.newConfigValue);
  }

  @computed get getAppList() {
    return toJS(this.appList);
  }
}

export default new CreateAutoTestStore();
