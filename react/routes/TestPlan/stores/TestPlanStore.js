import {
  observable, action, computed, toJS,
} from 'mobx';

import TestPlanTreeStore from './TestPlanTreeStore'; 
import { getStatusList } from '@/api/TestStatusApi';
import { getExecutesByFolder, getStatusByFolder, getPlanDetail } from '@/api/TestPlanApi';

class TestPlanStore extends TestPlanTreeStore {
    @observable loading = false;

    @action setLoading = (loading) => {
      this.loading = loading;
    }

    @computed get getLoading() {
      return this.loading;
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
      pageSize: 20,
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

    @observable executeOldData = {};

    @action setExecuteOldData = (executeOldData) => {
      this.executeOldData = executeOldData;
    }

    @observable executeNewData = {};

    @action setExecuteNewData = (executeNewData) => {
      this.executeNewData = executeNewData;
    }

    checkIdMap = observable.map();

    @observable planInfo = {};

    @action setPlanInfo = (planInfo) => {
      this.planInfo = planInfo;
    }

    @observable statusRes = {};

    @action setStatusRes = (statusRes) => {
      this.statusRes = statusRes;
    }

    @action clearStore = () => {
      this.tableLoading = false;
      this.treeData = {};
      this.testList = [];  
      this.expandedKeys = ['0-0'];  
      this.selectedKeys = [];  
      this.currentCycle = {};  
      this.preCycle = {};
      this.filter = {};
      this.executePagination = {
        current: 1,
        total: 0,
        pageSize: 20,
      };
      this.checkIdMap = observable.map();
      this.order = {
        orderField: '',
        orderType: '',
      };
      this.testPlanStatus = 'todo';
      this.executeOldData = {};
      this.executeNewData = {};
      this.planInfo = {};
      this.statusRes = {};
    }

    /**
     * 加载左边的树和右边的表格
     *
     * @memberof TestPlanStore
     */
    loadAllData = () => {
      this.setLoading(true);
      return Promise.all([getStatusList('CYCLE_CASE'), this.loadIssueTree()]).then(([statusList, treeData]) => {
        this.setLoading(false);
        if (this.getCurrentPlanId) {
          this.setStatusList(statusList);
          this.loadPlanDetail();
          this.loadExecutes();
          this.loadStatusRes();
        }
      }).catch((e) => {
        this.setLoading(false);
      });
    }

    loadRightData = async (isLoadPlamDetail = false) => {
      const promiseArr = [this.loadExecutes(), this.loadStatusRes()];
      if (isLoadPlamDetail) {
        promiseArr.push(this.loadPlanDetail());
      }
      Promise.all(promiseArr).then(() => {
      }).catch((e) => {
        console.log(e);
      });
    }

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
      this.setTableLoading(true);
      const executes = await getExecutesByFolder({ 
        planId: this.getCurrentPlanId, folderId: id, current, pageSize, filter, orderField, orderType, 
      });
      this.setTableLoading(false);
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
    loadStatusRes() {
      const currentCycle = this.getCurrentCycle;
      const { id: folderId } = currentCycle;
      getStatusByFolder({ planId: this.getCurrentPlanId, folderId }).then((res) => {
        this.setStatusRes(res);
      });
    }

    /**
     * 获取计划详情
     *
     * @memberof TestPlanStore
     */
    loadPlanDetail() {
      console.log(this.getCurrentPlanId);
      getPlanDetail(this.getCurrentPlanId).then((res) => {
        console.log(res);
        this.setPlanInfo(res);
      });
    }
}

export default TestPlanStore;
