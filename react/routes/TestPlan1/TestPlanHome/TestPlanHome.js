import React, {
  Component, useState, useContext, useEffect, 
} from 'react';
import { observer } from 'mobx-react-lite';
import _ from 'lodash';
import { FormattedMessage } from 'react-intl';
import {
  Page, Header, Content, Breadcrumb, Choerodon,
} from '@choerodon/boot';
import {
  Button, Icon, Tabs, Spin, 
} from 'choerodon-ui';
import { Modal } from 'choerodon-ui/pro';
import {
  getCycleTree, getExecutesByCycleId, editExecuteDetail, deleteExecute,
} from '../../../api/cycleApi'; 
import { editCycle } from '../../../api/ExecuteDetailApi';
import { getStatusList } from '../../../api/TestStatusApi';
import Injecter from '../../../components/Injecter';
import CreateAutoTest from '../components/CreateAutoTest'; 
import { openCreatePlan } from '../components/TestPlanModal';
import TestPlanDetailCard from '../components/TestPlanDetailCard';
import TestPlanStatusCard from '../components/TestPlanStatusCard';
import TestPlanTreeWrap from '../components/TestPlanTreeWrap';
import TestPlanTable from '../components/TestPlanTable';
import Empty from '../../../components/Empty';
import testCaseEmpty from '../../../assets/testCaseEmpty.svg';

import Store from '../stores';
import './TestPlanHome.less';
import { getParams, getDragRank, executeDetailShowLink } from '../../../common/utils';


const { TabPane } = Tabs;
const { confirm } = Modal;

export default observer(() => {
  const { prefixCls, createAutoTestStore, testPlanStore } = useContext(Store);
  const {
    treeData, loading, testPlanStatus, rightLoading, dataList, checkIdMap, testList,
  } = testPlanStore;

  const handleTabsChange = (value) => {
    testPlanStore.setTestPlanStatus(value);
  };

  const handleCreateAutoTest = () => {
    createAutoTestStore.setVisible(true);
  };
  const handleOpenCreatePlan = () => {
    openCreatePlan();
  };

  const loadCycle = (selectedKeys, {
    selected, selectedNodes, node, event,
  } = {}, flag) => {
    if (selectedKeys) {
      testPlanStore.setSelectedKeys(selectedKeys);
    }
    const data = node ? node.props.data : testPlanStore.getCurrentCycle;
    if (data.cycleId) {
      // 切换时，将分页回到第一页
      if (data.cycleId !== testPlanStore.getCurrentCycle.cycleId) {
        testPlanStore.setExecutePagination({ current: 1 });
      }
      testPlanStore.setCurrentCycle(data);
      if (data.type === 'folder' || data.type === 'cycle') {
        // if (!flag) {
        //   testPlanStore.setRightLoading(true);
        // }
        testPlanStore.loadExecutes();
      }
    }
  };

  const generateList = (data) => {
    for (let i = 0; i < data.length; i += 1) {
      const node = data[i];
      const { key, title } = node;
      // 找出url上的cycleId
      const { cycleId } = getParams(window.location.href);
      const currentCycle = testPlanStore.getCurrentCycle;
      if (!currentCycle.cycleId && Number(cycleId) === node.cycleId) {
        this.setExpandDefault(node);
      } else if (currentCycle.cycleId === node.cycleId) {
        testPlanStore.setCurrentCycle(node);
      }
      dataList.push({ key, title });
      if (node.children) {
        generateList(node.children, node.key);
      }
    }
  };

  const loadTreeAndExecute = () => {
    testPlanStore.setLoading(true);
    Promise.all([getStatusList('CYCLE_CASE'), getCycleTree()]).then(([statusList, planTreeData]) => {
      testPlanStore.setStatusList(statusList);
      testPlanStore.setTreeData(planTreeData.versions);
      testPlanStore.setLoading(false);
      generateList(planTreeData.versions);
      // 默认选中一个项
      // if (planTreeData.versions && planTreeData.versions.length > 0) {
      //   testPlanStore.setCurrentCycle(planTreeData.versions[0]);
      //   // 如果选中了项，就刷新table数据
      //   loadCycle(null, { node: { props: { planTreeData: planTreeData.versions[0] } } }, true);
      // }
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
    testPlanStore.rightEnterLoading();
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
      testPlanStore.rightLeaveLoading();
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

  /**
   * 点击table的一项
   *
   * @memberof TestPlanHome
   */
  const handleTableRowClick = (record) => {
    const { history } = this.props;
    history.push(executeDetailShowLink(record.executeId));
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
    // const { statusList } = testPlanStore;
    // const cycleData = { ...execute };
    // if (_.find(statusList, { projectId: 0, statusName: text })) {
    //   cycleData.executionStatus = _.find(statusList, { projectId: 0, statusName: text }).statusId;
    //   delete cycleData.defects;
    //   delete cycleData.caseAttachment;
    //   delete cycleData.testCycleCaseStepES;
    //   delete cycleData.lastRank;
    //   delete cycleData.nextRank;
    //   // 加载所有数据，因为进度条需要更新
    //   this.setState({
    //     loading: true,
    //   });
    //   editCycle(cycleData).then((Data) => {
    //     this.loadTreeAndExecute();
    //   }).catch((error) => {
    //     this.setState({
    //       loading: false,
    //     });
    //     Choerodon.prompt('网络错误');
    //   });
    // } else {
    //   Choerodon.prompt('未找到对应状态');
    // }
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
    console.log('失焦了');
    console.log(value, checkIdMap.size, checkIdMap.keys());
    if (value && checkIdMap.size) {
      checkIdMap.clear();
    }
  };

  useEffect(() => {
    loadTreeAndExecute();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const noPlan = treeData.length === 0 || treeData[0].children.length === 0;

  return (
    <Page className={prefixCls}>
      <Header
        title={<FormattedMessage id="testPlan_name" />}
      >
        <Button icon="playlist_add icon" onClick={handleOpenCreatePlan}>
          <FormattedMessage id="testPlan_createPlan" />
        </Button>
        {
          testPlanStatus !== 'finished' ? (
            <React.Fragment>
              <Button icon="mode_edit">
                <FormattedMessage id="testPlan_editPlan" />
              </Button>
              {
                testPlanStatus === 'notStart' ? (
                  <Button icon="play_circle_filled">
                    <FormattedMessage id="testPlan_manualTest" />
                  </Button>
                ) : (
                  <Button icon="check_circle">
                    <FormattedMessage id="testPlan_completePlan" />
                  </Button>
                )
              }
              <Button icon="auto_test" disabled={testPlanStatus === 'doing'} onClick={handleCreateAutoTest}>
                <FormattedMessage id="testPlan_autoTest" />
              </Button>
            </React.Fragment>
          ) : ''
        }
        
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
              extra={<Button type="primary" funcType="raised" onClick={handleOpenCreatePlan}>创建计划</Button>}
            />
          ) : (
            <div className={`${prefixCls}-contentWrap`}>
              <div className={`${prefixCls}-contentWrap-left`}>
                <div className={`${prefixCls}-contentWrap-testPlanTree`}>
                  <Tabs defaultActiveKey="notStart" onChange={handleTabsChange}>
                    <TabPane tab="未开始" key="notStart">
                      <TestPlanTreeWrap onTreeNodeSelect={loadCycle} />
                    </TabPane>
                    <TabPane tab="进行中" key="doing">
                      <TestPlanTreeWrap onTreeNodeSelect={loadCycle} />
                    </TabPane>
                    <TabPane tab="已完成" key="finished">
                      <TestPlanTreeWrap onTreeNodeSelect={loadCycle} />
                    </TabPane>
                  </Tabs>
                </div>
              </div>
              <div className={`${prefixCls}-contentWrap-right`}>
                <Spin spinning={rightLoading}>
                  <div className={`${prefixCls}-contentWrap-right-currentPlanName`}>
                    <Icon type="insert_invitation" />
                    <span>0.20.0版本测试计划</span>
                  </div>
                  <div className={`${prefixCls}-contentWrap-right-warning`}>
                    <Icon type="error" />
                    <span>该计划正在进行自动化测试，手工测试结果可能会将自动化测试结果覆盖！</span>
                  </div>
                  <div className={`${prefixCls}-contentWrap-right-card`}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', flexWrap: 'nowrap' }}>
                      <div style={{ flex: 1.42, marginRight: '0.16rem' }}>
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
                        onTableRowClick={handleTableRowClick}
                        onDeleteExecute={handleDeleteExecute}
                        onQuickPass={handleQuickPass}
                        onQuickFail={handleQuickFail}
                        onAssignToChange={handleAssignToChange}
                      />
                    </div>
                  </div>
                </Spin>
                
              </div>
            </div>
          )
        }
      </Content>
      <CreateAutoTest createAutoTestStore={createAutoTestStore} />
    </Page>
  );
});
