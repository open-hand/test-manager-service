/* eslint-disable no-lonely-if */
import {
  observable, action, computed, toJS,
} from 'mobx';
import { findIndex } from 'lodash';
import { getIssuesByFolder } from '../../../api/IssueManageApi';
import IssueTreeStore from './IssueTreeStore';

class IssueStore {
  @observable issues = [];
  
  @observable pagination = {
    current: 1,
    pageSize: 10,
    total: 0,
  };

  @observable filter = {
    searchArgs: {},
  };

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

  @action clearStore = () => {
    this.issues = [];
    this.pagination = {
      current: 1,
      pageSize: 10,
      total: 0,
    };
    this.filter = {
      searchArgs: {},
    };
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
  }

  init() {
    this.setOrder({
      orderField: '',
      orderType: '',
    });
    this.setFilter({
      advancedSearchArgs: {
      
      },
    });
    this.setBarFilters([]);
  }

  loadIssues = async (page, size = this.pagination.pageSize) => {
    const Page = page === undefined ? this.pagination.current : Math.max(page, 1);
    this.setLoading(true);
    const { orderField, orderType } = this.order;
    const { currentCycle } = IssueTreeStore;
    const { id } = currentCycle;
    try {
      const res = await getIssuesByFolder(id, Page, size, this.getFilter, orderField, orderType);
      this.setIssues(res.list);
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
      this.setLoading(false);
    } catch (e) {
      this.setLoading(false);
    }
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
    const index = findIndex(originIssues, { issueId: data.issueId });
    originIssues[index] = { ...originIssues[index], ...data };
  }

  @action setPagination(data) {
    this.pagination = data;
  }

  @action setFilter(data) {
    this.filter = data;
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
    this.draggingTableItems = draggingTableItems.filter(issue => issue.typeCode !== 'issue_auto_test');
  }

  @action setTableDraging(flag) {
    this.tableDraging = flag;
  }

  @computed get getIssues() {
    return toJS(this.issues);
  }

  @computed get getFilter() {
    const { filter } = this;
    return {
      ...filter,
      contents: this.barFilters,
    };
  }

  @computed get getDraggingTableItems() {
    return toJS(this.draggingTableItems);
  }
}
export default new IssueStore();
