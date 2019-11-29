import {
  observable, action, computed, toJS,
} from 'mobx';

import IssueTreeStore from './IssueTreeStore'; 
import { getStatusList } from '@/api/TestStatusApi';
import { getIssuesByFolder } from '@/api/IssueManageApi';

class TestPlanStore extends IssueTreeStore {
    @observable loading = true;

    @action setLoading = (loading) => {
      this.loading = loading;
    }

    @computed get getLoading() {
      return this.loading;
    }

    @observable dataList = [];

    @action setDataList = (dataList) => {
      this.dataList = dataList;
    }

    @computed get getDataList() {
      return this.dataList;
    }

    @observable rightLoading = false;

    @action setRightLoading = (rightLoading) => {
      this.rightLoading = rightLoading;
    }

    @computed get getRightLoading() {
      return this.rightLoading;
    }

    @observable tableLoading = false;

    @action setTableLoading = (tableLoading) => {
      this.tableLoading = tableLoading;
    }

    @computed get getTableLoading() {
      return this.tableLoading;
    }

    @observable executePagination = {
      current: 1,
      total: 0,
      pageSize: 50,
    };
     
    @action setExecutePagination(executePagination) {
      this.executePagination = { ...this.executePagination, ...executePagination };
    }
  
    @computed get getExecutePagination() {
      return toJS(this.executePagination);
    }

    @observable statusList = [];

    @action setStatusList(statusList) {
      this.statusList = statusList;
    }

    @observable testList = [];

    @action setTestList = (testList) => {
      this.testList = testList;
    }

    @computed get getTestList() {
      return toJS(this.testList);
    }

    @observable filter = {};

    @action setFilter = (filter) => {
      this.filter = filter;
    }

    @observable order = {
      orderField: '',
      orderType: '',
    };
  

    @computed get getFilters() {
      return this.filter;
    }

    checkIdMap = observable.map();

    @action clearStore = () => {
      this.treeData = [];  
      this.expandedKeys = ['0-0'];  
      this.selectedKeys = [];  
      this.addingParent = null;  
      this.currentCycle = {};  
      this.preCycle = {};
      this.dataList = [];
      this.rightLoading = false;
      this.filter = {};
      this.executePagination = {
        current: 1,
        total: 0,
        pageSize: 50,
      };
      this.checkIdMap = observable.map();
      this.order = {
        orderField: '',
        orderType: '',
      };
      this.testPlanStatus = 'todo';
    }

    /**
     * 加载左边的树和右边的表格
     *
     * @memberof TestPlanStore
     */
    loadAllData = () => Promise.all([getStatusList('CYCLE_CASE'), this.loadIssueTree()]).then(([statusList, treeData]) => {
      this.setStatusList(statusList);
      this.loadExecutes();
      this.loadStatusRes();
    })

    /**
     * 加载测试计划的表格
     *
     * @memberof TestPlanStore
     */
    async loadExecutes() {
      const currentCycle = this.getCurrentCycle;
      const { executePagination, filter, order } = this;
      const { id } = currentCycle;
      const { orderField, orderType } = order;
      const { current, pageSize } = executePagination;
      this.setRightLoading(true);
      const executes = await getIssuesByFolder(id, current, pageSize, filter, orderField, orderType);
      this.setRightLoading(false);
      this.setTestList(executes.list);
      this.setExecutePagination({
        current: executePagination.current,
        pageSize: executePagination.pageSize,
        total: executes.total,
      });
    }

    /**
     * 加载当前层级执行状态统计
     *
     * @memberof TestPlanStore
     */
    async loadStatusRes() {

    }
}

export default TestPlanStore;
