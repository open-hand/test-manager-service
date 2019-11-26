import React, { Component, useState, useContext } from 'react';
import { observer } from 'mobx-react-lite';
import _ from 'lodash';
import { FormattedMessage } from 'react-intl';
import {
  Page, Header, Content, Breadcrumb, Choerodon,
} from '@choerodon/boot';
import { Button, Icon, Tabs } from 'choerodon-ui';
import { Modal } from 'choerodon-ui/pro';
import CreateAutoTest from '../components/CreateAutoTest'; 
import openCreatePlan from '../components/CreatePlan';
import TestPlanDetailCard from '../components/TestPlanDetailCard';
import TestPlanStatusCard from '../components/TestPlanStatusCard';
import TestPlanTreeWrap from '../components/TestPlanTreeWrap';
import Store from '../stores';
import './TestPlanHome.less';

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
          this.setState({
            tableLoading: true,
          });
        }
        this.loadExecutes(data);
      }
    }
  };

  const { dataList } = testPlanStore;

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
        <div className={`${prefixCls}-contentWrap`}>
          <div className={`${prefixCls}-contentWrap-left`}>
            <div className={`${prefixCls}-contentWrap-testPlanTree`}>
              <Tabs defaultActiveKey="1" onChange={handleTabsChange}>
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
            </div>
         
          </div>
         
        </div>
      </Content>
      <CreateAutoTest createAutoTestStore={createAutoTestStore} />
    </Page>
  );
});
