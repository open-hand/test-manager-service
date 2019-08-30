/* eslint-disable no-lonely-if */
import {
  observable, action, computed, toJS,
} from 'mobx';
import _ from 'lodash';
import {
  getIssuesByFolder, getIssuesByIds, getSingleIssues,
  getIssuesByVersion, getAllIssues,
} from '../../../api/IssueManageApi';
import {
  getProjectVersion, getPrioritys, getIssueTypes, getIssueStatus, getLabels,
} from '../../../api/agileApi';
import IssueTreeStore from './IssueTreeStore';

class IssueStore {
  @observable issues = [];

  @observable issueIds = [];

  @observable versions = [];

  @observable prioritys = [];

  @observable labels = [];

  @observable issueTypes = [];

  @observable issueStatusList = [];

  @observable selectedVersion = null;

  @observable pagination = {
    current: 1,
    pageSize: 10,
    total: 0,
  };

  @observable filter = {
    advancedSearchArgs: {},
    searchArgs: {},
  };

  @observable filteredInfo = {};

  @observable order = {
    orderField: '',
    orderType: '',
  };

  @observable loading = true;

  @observable paramName = undefined;

  @observable paramIssueId = undefined;

  @observable barFilters = undefined;

  @observable draggingTableItems = [];

  @observable copy = false;

  @observable tableDraging = false;

  @observable treeShow = true;

  @observable issueForderNames = [];

  @computed get getIssueIds() {
    return toJS(this.issueIds);
  }

  @computed get getIssueFolderNames() {
    return toJS(this.issueForderNames);
  }

  @action setIssueForderNames(data) {
    this.issueForderNames = data;
  }

  @action clearStore = () => {
    this.issues = [];
    this.issueIds = [];
    this.versions = [];
    this.prioritys = [];
    this.issueTypes = [];
    this.issueStatusList = [];
    this.selectedVersion = null;
    this.pagination = {
      current: 1,
      pageSize: 10,
      total: 0,
    };
    this.filter = {
      advancedSearchArgs: {},
      searchArgs: {},
    };
    this.filteredInfo = {};
    this.order = {
      orderField: '',
      orderType: '',
    };
    this.loading = true;
    this.paramName = undefined;
    this.paramIssueId = undefined;
    this.barFilters = undefined;
    this.draggingTableItems = [];
    this.copy = false;
    this.tableDraging = false;
    this.treeShow = true;
  }

  init() {
    this.setOrder({
      orderField: '',
      orderType: '',
    });
    this.setFilter({
      advancedSearchArgs: {
        // issueTypeId: [18],
        // typeCode: ['issue_test']
      },
      searchArgs: {},
    });
    this.setFilteredInfo({});
    // this.loadIssues();
  }

  loadIssues = (page, size = this.pagination.pageSize) => {
    const Page = page === undefined ? this.pagination.current : Math.max(page, 1);
    this.setLoading(true);
    const { orderField, orderType } = this.order;
    return new Promise((resolve) => {
      getIssueTypes().then((issueTypes) => {
        this.setIssueTypes(issueTypes);
        // 设置测试类型
        const filter = this.getFilter;
        filter.advancedSearchArgs.issueTypeId = issueTypes.map((type) => type.id);
        this.setFilter(filter);
        const funcArr = [];
        funcArr.push(getProjectVersion());
        funcArr.push(getPrioritys());
        funcArr.push(getIssueStatus());
        funcArr.push(getLabels());
        const { currentCycle } = IssueTreeStore;
        // 树的每一层的类型
        const types = ['all', 'topversion', 'version', 'folder'];
        const type = currentCycle.key ? types[currentCycle.key.split('-').length - 1] : 'allissue';
        const { versionId, cycleId, children } = currentCycle;
        // 不是第一页情况
        if (Page > 1) {
          // 调用
          funcArr.push(getIssuesByIds(versionId, cycleId, this.issueIds.slice(size * (Page - 1), size * Page)));
        } else {
          // 第一页 五种情况 地址栏有参数时的优先级为最低
          /**
           * 1.加载所有issue
           * 2.记载某一类型的版本下的issue,例如规划中的版本
           * 3.加载某个版本的issue
           * 4.加载某个文件夹下的issue
           * 5.地址栏有paramIssueId时只取单个issue并打开侧边
           *
           */
          // 1.加载全部数据
          if ((type === 'all' || type === 'allissue') && !this.paramIssueId) {
            funcArr.push(getAllIssues(Page, size, this.getFilter, orderField, orderType));
          } else if (type === 'topversion') {
            // 2.加载某一类versions
            const versions = children.map((child) => child.versionId);
            funcArr.push(getIssuesByVersion(versions,
              Page, size, this.getFilter, orderField, orderType));
          } else if (type === 'version') {
            // 3.加载单个version
            funcArr.push(getIssuesByVersion([versionId],
              Page, size, this.getFilter, orderField, orderType));
          } else if (type === 'folder') {
            // 4.加载单个folder
            funcArr.push(getIssuesByFolder(cycleId,
              Page, size, this.getFilter, orderField, orderType));
          } else if (this.paramIssueId) {
            // 5.地址栏有url 调用只取这一个issue的方法 这个要放最后
            funcArr.push(getSingleIssues(Page, size, this.getFilter, orderField, orderType));
          }
        }

        Promise.all(funcArr).then(([versions, prioritys, issueStatusList, labels, res]) => {
          this.setVersions(_.reverse(versions));
          this.setPrioritys(prioritys);
          this.setIssueStatusList(issueStatusList);
          this.setLabels(labels);
          if (versions && versions.length > 0) {
            this.selectVersion(versions[0].versionId);
          }
          this.setIssues(res.list);
          this.setIssueForderNames(_.map(res.list, 'folderName'));
          if (Page === 1) {
            this.setIssueIds(res.allIdValues || []);
            if (window.sessionStorage) {
              sessionStorage.allIdValues = res.allIdValues || [];
            }
          }
          // 调用ids接口不返回总数

          if (Page > 1) {
            this.setPagination({
              current: Page,
              pageSize: size,
              total: this.pagination.total,
            });
          } else {
            this.setPagination({
              current: res.pageNum,
              pageSize: size,
              total: res.total,
            });
          }
          resolve(res);
          this.setLoading(false);
        });
      });
    });
  }


  @action setIssues(data) {
    this.issues = data;
  }

  /**
   * 当issue更新时,本地更新单个issue
   * @param {*} data
   */
  @action updateSingleIssue(data) {
    const originIssues = this.issues;
    const index = _.findIndex(originIssues, { issueId: data.issueId });
    originIssues[index] = { ...originIssues[index], ...data };
  }

  @action setIssueIds(issueIds) {
    this.issueIds = issueIds;
  }

  @action setVersions(versions) {
    this.versions = versions;
  }

  @action setPrioritys(prioritys) {
    this.prioritys = prioritys;
  }

  @action setLabels(labels) {
    this.labels = labels;
  }

  @action setIssueTypes(issueTypes) {
    this.issueTypes = issueTypes;
  }

  @action setIssueStatusList(issueStatusList) {
    this.issueStatusList = issueStatusList;
  }

  @action selectVersion(selectedVersion) {
    this.selectedVersion = selectedVersion;
  }

  @action setPagination(data) {
    this.pagination = data;
  }

  @action setFilter(data) {
    this.filter = data;
  }

  @action setFilteredInfo(data) {
    this.filteredInfo = data;
  }

  @action setOrder(data) {
    this.order = data;
  }

  @action setLoading(data) {
    this.loading = data;
  }

  @action setParamType(data) {
    this.paramType = data;
  }

  @action setParamName(data) {
    this.paramName = data;
  }

  @action setParamIssueId(data) {
    this.paramIssueId = data;
  }

  @action setBarFilters(data) {
    this.barFilters = data;
  }

  @action setCopy(flag) {
    this.copy = flag;
  }


  @action setDraggingTableItems(draggingTableItems) {
    // console.log('set', draggingTableItems);
    this.draggingTableItems = draggingTableItems.filter((issue) => issue.typeCode !== 'issue_auto_test');
  }

  @action setTableDraging(flag) {
    this.tableDraging = flag;
  }

  @action setTreeShow(flag) {
    this.treeShow = flag;
  }

  @computed get getIssues() {
    return toJS(this.issues);
  }

  @computed get getVersions() {
    return toJS(this.versions);
  }

  @computed get getPrioritys() {
    return toJS(this.prioritys);
  }

  @computed get getLabels() {
    return toJS(this.labels);
  }

  @computed get getDefaultPriority() {
    const priority = _.find(this.prioritys, { default: true });
    if (priority) {
      return priority.id;
    } else if (this.prioritys.length > 0) {
      return this.prioritys[0].id;
    }
    return null;
  }

  @computed get getIssueTypes() {
    return toJS(this.issueTypes);
  }

  @computed get getTestType() {
    const type = _.find(this.issueTypes, { typeCode: 'issue_test' });
    if (type) {
      return type.id;
    }
    return null;
  }

  @computed get getIssueStatus() {
    return toJS(this.issueStatusList);
  }

  @computed get getSeletedVersion() {
    return toJS(this.selectedVersion);
  }

  @computed get getFilter() {
    const { filter } = this;
    return {
      ...filter,
      contents: this.barFilters,
      otherArgs: {
        ...filter.otherArgs,
        issueIds: this.paramIssueId ? [Number(this.paramIssueId)] : undefined,
      },
    };
  }

  @computed get getDraggingTableItems() {
    return toJS(this.draggingTableItems);
  }
}
export default new IssueStore();
