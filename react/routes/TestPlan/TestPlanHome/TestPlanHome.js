import React, {
  useCallback, useContext, useEffect,
} from 'react';
import { observer } from 'mobx-react-lite';
import _ from 'lodash';
import { FormattedMessage } from 'react-intl';
import {
  Page, Header, Content, Breadcrumb, Choerodon,
} from '@choerodon/boot';
import {
  Icon, Tabs, Spin,
} from 'choerodon-ui';
import { Modal, Button } from 'choerodon-ui/pro';
import {
  getCycleTree, getExecutesByCycleId, editExecuteDetail, deleteExecute,
} from '../../../api/cycleApi';
import CreateAutoTest from '../components/CreateAutoTest';
import TestPlanDetailCard from '../components/TestPlanDetailCard';
import TestPlanStatusCard from '../components/TestPlanStatusCard';
import UpdateRemindModalChildren from '../components/UpdateRemindModalChildren';
import TestPlanTree from '../components/TestPlanTree';
import TestPlanTable from '../components/TestPlanTable';
import TestPlanHeader from '../components/TestPlanHeader';
import { openCreatePlan } from '../components/TestPlanModal';
import Empty from '../../../components/Empty';
import testCaseEmpty from './testCaseEmpty.svg';

import Store from '../stores';
import './TestPlanHome.less';
import { getDragRank } from '../../../common/utils';


const { TabPane } = Tabs;
const { confirm } = Modal;
const updateRemindModal = Modal.key();

function TestPlanHome() {
  const {
    prefixCls, createAutoTestStore, testPlanStore,
  } = useContext(Store);
  const {
    treeData, loading, checkIdMap, testList, testPlanStatus, planInfo,
  } = testPlanStore;

  const handleTabsChange = (value) => {
    // testPlanStore.clearStore();
    testPlanStore.setTestPlanStatus(value);
    testPlanStore.setCurrentCycle({});
    testPlanStore.loadAllData();
  };

  const handleUpdateOk = () => {

  };

  const handleIgnoreUpdate = () => {

  };
  const handleOpenCreatePlan = () => {
    openCreatePlan({
      onCreate: (newPlan) => {
        if (testPlanStatus !== 'todo') {
          testPlanStore.setTestPlanStatus('todo');
        }      
        testPlanStore.loadAllData();
      },
    });
  };
  const handleOpenUpdateRemind = () => {
    Modal.open({
      key: updateRemindModal,
      drawer: true,
      title: '用例变更提醒',
      children: <UpdateRemindModalChildren testPlanStore={testPlanStore} />,
      style: { width: '10.9rem' },
      className: 'c7ntest-testPlan-updateRemind-modal',
      okText: '更新',
      cancelText: '取消',
      onOk: handleUpdateOk,
      footer: (okBtn, cancelBtn) => (
        <div>
          {okBtn}
          <Button funcType="funcType" onClick={handleIgnoreUpdate}>忽略更新</Button>
          {cancelBtn}
        </div>
      ),
    });
  };

  const onDragEnd = (sourceIndex, targetIndex) => {
    const { lastRank, nextRank } = getDragRank(sourceIndex, targetIndex, testList);
    const source = testList[sourceIndex];
    const temp = { ...source };
    delete temp.defects;
    delete temp.caseAttachment;
    delete temp.testCycleCaseStepES;
    delete temp.issueInfosVO;
    temp.assignedTo = temp.assignedTo || 0;
    testPlanStore.setTableLoading(true);
    editExecuteDetail({
      ...temp,
      ...{
        lastRank,
        nextRank,
      },
    }).then((res) => {
      testPlanStore.loadExecutes();
    }).catch((err) => {
      Choerodon.prompt('网络错误');
      testPlanStore.setTableLoading(false);
    });
  };

  const handleExecuteTableChange = (pagination, filters, sorter, barFilters) => {
    const Filters = { ...filters };
    if (barFilters && barFilters.length > 0) {
      Filters.summary = barFilters;
    }
    if (pagination.current) {
      testPlanStore.setFilters(Filters);
      testPlanStore.rightEnterLoading();
      testPlanStore.setExecutePagination(pagination);
      testPlanStore.loadExecutes();
    }
  };

  const handleDeleteExecute = (record) => {
    const { executeId } = record;
    confirm({
      width: 560,
      title: Choerodon.getMessage('确认删除吗?', 'Confirm delete'),
      content: Choerodon.getMessage('当您点击删除后，该条执行将从此计划阶段中移除!', 'When you click delete, after which the data will be deleted !'),
      onOk: () => {
        testPlanStore.rightEnterLoading();
        deleteExecute(executeId)
          .then((res) => {
            testPlanStore.loadExecutes();
          }).catch((err) => {
            /* console.log(err); */
            Choerodon.prompt('网络异常');
            testPlanStore.rightLeaveLoading();
          });
      },
      okText: '删除',
      okType: 'danger',
    });
  };

  const quickPassOrFail = (execute, text) => {
  };

  const handleQuickPass = (execute, e) => {
    e.stopPropagation();
    quickPassOrFail(execute, '通过');
  };

  const handleQuickFail = (execute, e) => {
    e.stopPropagation();
    quickPassOrFail(execute, '失败');
  };

  const handleAssignToChange = (value) => {
    // console.log('失焦了');
    // console.log(value, checkIdMap.size, checkIdMap.keys());
    if (value && checkIdMap.size) {
      checkIdMap.clear();
    }
  };

  useEffect(() => {
    testPlanStore.loadAllData();
  }, [testPlanStore]);

  const noPlan = treeData.rootIds && treeData.rootIds.length === 0;

  return (
    <Page className={prefixCls}>
      <Header
        title={<FormattedMessage id="testPlan_name" />}
      >
        <Button icon="playlist_add icon" onClick={handleOpenUpdateRemind}>
          <FormattedMessage id="testPlan_createPlan" />
        </Button>
        <Button icon="playlist_add icon" onClick={handleOpenCreatePlan}>
          <FormattedMessage id="testPlan_createPlan" />
        </Button>
        <TestPlanHeader />
      </Header>
      <Breadcrumb />
      <Content style={{ display: 'flex', padding: '0', borderTop: '0.01rem solid rgba(0,0,0,0.12)' }}>
        {
          noPlan ? (
            <Empty
              loading={loading}
              pic={testCaseEmpty}
              title="暂无计划"
              description="当前项目下无计划，请创建"
              extra={<Button color="primary" funcType="raised" onClick={handleOpenCreatePlan}>创建计划</Button>}
            />
          ) : (
            <div className={`${prefixCls}-contentWrap`}>
              <div className={`${prefixCls}-contentWrap-left`}>
                <div className={`${prefixCls}-contentWrap-testPlanTree`}>
                  <Tabs defaultActiveKey="todo" onChange={handleTabsChange} value={testPlanStatus}>
                    <TabPane tab="未开始" key="todo">
                      <TestPlanTree />
                    </TabPane>
                    <TabPane tab="进行中" key="doing">
                      <TestPlanTree />
                    </TabPane>
                    <TabPane tab="已完成" key="done">
                      <TestPlanTree />
                    </TabPane>
                  </Tabs>
                </div>
              </div>
              <div className={`${prefixCls}-contentWrap-right`}>
                <div className={`${prefixCls}-contentWrap-right-currentPlanName`}>
                  <Icon type="insert_invitation" />
                  <span>{planInfo.name}</span>
                </div>
                <div className={`${prefixCls}-contentWrap-right-warning`}>
                  <Icon type="error" />
                  <span>该计划正在进行自动化测试，手工测试结果可能会将自动化测试结果覆盖！</span>
                </div>
                <div className={`${prefixCls}-contentWrap-right-card`}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', flexWrap: 'nowrap' }}>
                    <div style={{ flex: 1, marginRight: '0.16rem' }}>
                      <TestPlanDetailCard />
                    </div>
                    <div style={{ flex: 1 }}>
                      <TestPlanStatusCard />
                    </div>
                  </div>
                  <div className={`${prefixCls}-contentWrap-table`}>
                    <TestPlanTable
                      onDragEnd={onDragEnd}
                      onTableChange={handleExecuteTableChange}
                      onDeleteExecute={handleDeleteExecute}
                      onQuickPass={handleQuickPass}
                      onQuickFail={handleQuickFail}
                      onAssignToChange={handleAssignToChange}
                      onOpenUpdateRemind={handleOpenUpdateRemind}
                    />
                  </div>
                </div>
              </div>
            </div>
          )
        }
      </Content>
      <CreateAutoTest createAutoTestStore={createAutoTestStore} />
    </Page>
  );
}

export default observer(TestPlanHome);
