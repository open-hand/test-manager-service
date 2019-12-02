import {
  observable, action, computed, toJS,
} from 'mobx';

import TestPlanTreeStore from './TestPlanTreeStore'; 
import { getStatusList } from '@/api/TestStatusApi';
import { getExecutesByFolder, getStatusByFolder, getPlanDetail } from '@/api/TestPlanApi';

const getParent = (treeFolders, folderId) => {
  let parent;
  for (let i = 0; i < treeFolders.length; i++) {
    if (treeFolders[i].children && treeFolders[i].children.length && treeFolders[i].children.includes(folderId)) {
      parent = treeFolders[i];
      if (parent.data.parentId) {
        return getParent(treeFolders, parent.id);
      } else {
        return parent;
      }
    }
  }
};
class TestPlanStore extends TestPlanTreeStore {
    @observable loading = true;

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

    @action clearStore = () => {
      this.treeData = [];  
      this.expandedKeys = ['0-0'];  
      this.selectedKeys = [];  
      this.addingParent = null;  
      this.currentCycle = {};  
      this.preCycle = {};
      this.dataList = [];
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
      this.currentPlanId = undefined;
    }

    /**
     * 加载左边的树和右边的表格
     *
     * @memberof TestPlanStore
     */
    loadAllData = () => Promise.all([getStatusList('CYCLE_CASE'), this.loadIssueTree()]).then(([statusList, treeData]) => {
      this.setStatusList(statusList);
      this.loadPlanDetail();
      this.loadExecutes();
      this.loadStatusRes();
    })

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
      const { id: folderId } = currentCycle;
      const planId = (getParent(this.treeData.treeFolder, folderId) && getParent(this.treeData.treeFolder, folderId).id) || folderId;
      this.setCurrentPlanId(planId);
      const { orderField, orderType } = order;
      const { current, pageSize } = executePagination;
      this.setTableLoading(true);
      const executes = await getExecutesByFolder({ 
        planId, folderId, current, pageSize, filter, orderField, orderType, 
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
      const planId = getParent(this.treeData.treeFolder, folderId) && getParent(this.treeData.treeFolder, folderId).id;
      getStatusByFolder({ planId, folderId }).then((res) => {
        console.log(res);
      });
    }

    /**
     * 获取计划详情
     *
     * @memberof TestPlanStore
     */
    loadPlanDetail() {
      console.log(this.currentPlanId);
      getPlanDetail(this.currentPlanId).then((res) => {
        console.log(res);
      });
    }
}

export default TestPlanStore;
