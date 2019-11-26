import React, {
  Component, useState, useContext, useEffect, 
} from 'react';
import { observer } from 'mobx-react-lite';
import _ from 'lodash';
import { FormattedMessage } from 'react-intl';
import {
  Page, Header, Content, Breadcrumb, Choerodon,
} from '@choerodon/boot';
import { Button, Icon, Tabs } from 'choerodon-ui';
import { Modal } from 'choerodon-ui/pro';
import {
  getCycleTree, getExecutesByCycleId,
} from '../../../api/cycleApi';
import Injecter from '../../../components/Injecter';
import CreateAutoTest from '../components/CreateAutoTest'; 
import openCreatePlan from '../components/CreatePlan';
import TestPlanDetailCard from '../components/TestPlanDetailCard';
import TestPlanStatusCard from '../components/TestPlanStatusCard';
import TestPlanTreeWrap from '../components/TestPlanTreeWrap';
import TestPlanTable from '../components/TestPlanTable';
import Empty from '../../../components/Empty';
import testCaseEmpty from '../../../assets/testCaseEmpty.svg';

import Store from '../stores';
import './TestPlanHome.less';
import { getParams } from '../../../common/utils';


const { TabPane } = Tabs;
export default observer(() => {
  const { prefixCls, createAutoTestStore, testPlanStore } = useContext(Store);
  const [activeKey, setActiveKey] = useState('notStart');

  const handleTabsChange = (value) => {
    setActiveKey(value);
  };

  const handleCreateAutoTest = () => {
    createAutoTestStore.setVisible(true);
  };
  const handleOpenCreatePlan = () => {
    openCreatePlan();
  };

  const /**
  *右侧reload
  *
  * @memberof TestExecuteHomeContainer
  */
    loadExecutes = () => {
      const currentCycle = testPlanStore.getCurrentCycle;
      const { cycleId, type } = currentCycle;
      const executePagination = testPlanStore.getExecutePagination;
      const { filters } = testPlanStore;
      const targetPage = executePagination.current;
      getExecutesByCycleId({
        page: targetPage,
        size: executePagination.pageSize,
      }, cycleId,
      {
        ...filters,
        lastUpdatedBy: [Number(this.lastUpdatedBy)],
      }, type).then((res) => {
        testPlanStore.setExecutePagination({
          current: res.pageNum,
          pageSize: res.pageSize,
          total: res.total,
        });
        testPlanStore.setTableLoading(false);
      });
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
        if (!flag) {
          testPlanStore.setTableLoading(true);
        }
        loadExecutes(data);
      }
    }
  };

  const generateList = (data) => {
    const { dataList } = testPlanStore;
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
    getCycleTree().then((data) => {
      // traverseTree({ title: '所有版本', key: '0', children: data.versions });
      testPlanStore.setTreeData(data.versions);
      testPlanStore.setLoading(false);
      generateList(data.versions);

      // window.console.log(dataList);
    });
    // 如果选中了项，就刷新table数据
    const currentCycle = testPlanStore.getCurrentCycle;
    if (currentCycle.cycleId) {
      loadCycle(null, { node: { props: { data: currentCycle } } }, true);
    }
  };

  useEffect(() => {
    loadTreeAndExecute();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const { treeData, loading } = testPlanStore;
  const noPlan = treeData.length === 0 || treeData[0].children.length === 0;
  return (
    <Page className={prefixCls}>
      <Header
        title={<FormattedMessage id="testPlan_name" />}
      >
        <Button icon="playlist_add icon" onClick={handleOpenCreatePlan}>
          <FormattedMessage id="testPlan_createPlan" />
        </Button>
        <Button icon="mode_edit">
          <FormattedMessage id="testPlan_editPlan" />
        </Button>
        <Button icon="play_circle_filled">
          <FormattedMessage id="testPlan_manualTest" />
        </Button>
        <Button icon="auto_test" onClick={handleCreateAutoTest}>
          <FormattedMessage id="testPlan_autoTest" />
        </Button>
      </Header>
      <Breadcrumb />
      <Content style={{ display: 'flex', padding: '0', borderTop: '0.01rem solid rgba(0,0,0,0.12)' }}>
        {
          noPlan ? <Empty loading={loading} pic={testCaseEmpty} title="" description="" /> : (
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
                <div className={`${prefixCls}-contentWrap-baseInfo`}>
                  <div className={`${prefixCls}-contentWrap-baseInfo-currentPlanName`}>
                    <Icon type="insert_invitation" />
                    <span>0.20.0版本测试计划</span>
                  </div>
                  <div className={`${prefixCls}-contentWrap-baseInfo-warning`}>
                    <Icon type="error" />
                    <span>该计划正在进行自动化测试，手工测试结果可能会将自动化测试结果覆盖！</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', flexWrap: 'nowrap' }}>
                    <div style={{ flex: 1.42, marginRight: '0.16rem' }}>
                      <TestPlanDetailCard />
                    </div>
                    <div style={{ flex: 1 }}>
                      <TestPlanStatusCard />
                    </div>
                  </div>
                  <div className={`${prefixCls}-contentWrap-table`}>
                    {/* <Injecter store={testPlanStore} item={['statusList', 'getTestList', 'executePagination', 'rightLoading']}>
                      {([statusList, testList, executePagination, rightLoading]) => (
                        <TestPlanTable
                          statusList={statusList}
                          loading={rightLoading}
                          pagination={executePagination}
                          dataSource={testList}
                          onLastUpdatedByChange={this.handleLastUpdatedByChange}
                          onAssignedToChange={this.handleAssignedToChange}
                          onDragEnd={this.onDragEnd}                        
                          onTableChange={this.handleExecuteTableChange}
                          onTableRowClick={this.handleTableRowClick}
                          onDeleteExecute={this.handleDeleteExecute}
                        />
                      )}
                    </Injecter> */}
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
});
