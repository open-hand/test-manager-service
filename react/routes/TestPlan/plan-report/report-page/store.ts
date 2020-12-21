import {
  observable, action, runInAction,
} from 'mobx';
import TestReportApi from '@/api/TestReport';
import { getStatusByFolder } from '@/api/TestPlanApi';
import { getStatusList } from '@/api/TestStatusApi';
import { IUser } from '@/common/types';

class TestReportStore {
  constructor({ planId }: { planId: string }) {
    this.planId = planId;
  }

  planId: string

  @observable loading = false;

  @observable statusList = []

  @observable pieData: {
    total: number
    statusVOList: {
      count: number
      statusColor: string
      statusName: string
    }[]
  } = { total: 0, statusVOList: [] };

  @observable baseInfo: {
    manager: IUser | null
    startDate: string
    endDate: string
    totalCaseCount: number
    relatedIssueCount: number
    totalBugCount: number
    solvedBugCount: number
    passedCaseCount: number
  } = {
    manager: null,
    startDate: '',
    endDate: '',
    totalCaseCount: 0,
    relatedIssueCount: 0,
    totalBugCount: 0,
    solvedBugCount: 0,
    passedCaseCount: 0,
  };

  @action
  async loadData() {
    this.loading = true;
    const [statusList, baseInfo, pieData] = await Promise.all([
      getStatusList('CYCLE_CASE'),
      TestReportApi.load(this.planId),
      getStatusByFolder({ planId: this.planId, folderId: this.planId }),
    ]);
    runInAction(() => {
      this.statusList = statusList;
      this.pieData = pieData;
      this.baseInfo = baseInfo;
      this.loading = false;
    });
  }
}

export default TestReportStore;
