/* eslint-disable react/jsx-no-bind */
import React, { useContext, useCallback } from 'react';
import {
  Choerodon, stores,
} from '@choerodon/boot';
import { HeaderButtons } from '@choerodon/master';
import { observer } from 'mobx-react-lite';
import { useIntl } from 'react-intl';
import { Modal } from 'choerodon-ui/pro';
import { useHistory } from 'react-router-dom';
import queryString from 'query-string';
import { updatePlanStatus } from '@/api/TestPlanApi';
import { openCreatePlan, openEditPlan } from '../TestPlanModal';
import ConfirmCompleteModalChildren from './components/ConfirmCompleteModalChildren';
import Store from '../../stores';
import './TestPlanHeader.less';

const { AppState } = stores;
const confirmCompletePlanModalKey = Modal.key();

function TestPlanHeader() {
  const intl = useIntl();
  const { testPlanStore } = useContext(Store);
  const history = useHistory();
  const { testPlanStatus, getCurrentPlanId, getCurrentCycle } = testPlanStore;
  const {
    id, name, category, organizationId,
  } = AppState.currentMenuType;
  const queryStr = queryString.stringify({
    id,
    name,
    category,
    planName: getCurrentCycle && getCurrentCycle.data ? getCurrentCycle.data.name : '',
    type: 'project',
    organizationId,
  });
  const onUpdatePlanStatus = useCallback(async (planItem, newStatus) => {
    updatePlanStatus({
      planId: planItem.item.id,
      objectVersionNumber: planItem.item.data.objectVersionNumber,
      statusCode: newStatus,
    }).then(() => {
      if (newStatus === 'doing') {
        Choerodon.prompt('开始测试成功');
        testPlanStore.setTestPlanStatus('doing');
        testPlanStore.loadAllData();
      } else {
        Choerodon.prompt('完成测试成功');
        testPlanStore.setTestPlanStatus('done');
        testPlanStore.loadAllData();
      }
    }).catch(() => {
      if (newStatus === 'doing') {
        Choerodon.prompt('开始测试失败');
      } else {
        Choerodon.prompt('完成测试失败');
      }
    });
  }, [testPlanStore]);

  const confirmCompletePlan = useCallback((planItem, newStatus) => {
    Modal.open({
      key: confirmCompletePlanModalKey,
      title: '完成计划确认',
      children: (
        <ConfirmCompleteModalChildren planName={planItem.item.data.name} testPlanStore={testPlanStore} />
      ),
      okText: '确定',
      onOk: async () => {
        await onUpdatePlanStatus(planItem, newStatus);
        Modal.open({
          title: '跳转到计划报告',
          onOk: () => {
            history.push(`/testManager/TestPlan/report/${getCurrentPlanId}?${queryStr}`);
          },
        });
      },
      cancelText: '取消',
      style: { width: '5.6rem' },
      className: 'c7ntest-testPlan-completePlan-confirm-modal',
    });
  }, []);

  const handleUpdatePlanStatus = useCallback((newStatus) => {
    const { getItem } = testPlanStore.treeRef.current || {};
    const planItem = getItem(testPlanStore.getCurrentPlanId) || {};
    if (planItem.item && planItem.item.id) {
      if (newStatus === 'doing') {
        onUpdatePlanStatus(planItem, newStatus);
      } else {
        confirmCompletePlan(planItem, newStatus);
      }
    }
  }, [confirmCompletePlan, onUpdatePlanStatus, testPlanStore.getCurrentPlanId, testPlanStore.treeRef]);

  const handlePlanEdit = useCallback((newPlan) => {
    testPlanStore.setCalendarLoading(true);
    testPlanStore.loadIssueTree().then(() => {
      testPlanStore.setCalendarLoading(false);
    }).catch(() => {
      Choerodon.prompt('更新计划日历失败');
      testPlanStore.setCalendarLoading(false);
    });
    // 更新右侧数据
    if (testPlanStore.getCurrentPlanId === newPlan.planId) {
      testPlanStore.loadPlanDetail();
    }
  }, [testPlanStore]);
  const handleOpenEditPlan = useCallback(async () => {
    openEditPlan({
      planId: getCurrentPlanId,
      onEdit: handlePlanEdit,
    });
  }, [getCurrentPlanId, handlePlanEdit]);
  const handleReportClick = useCallback(() => {
    history.push(`/testManager/TestPlan/report/${getCurrentPlanId}?${queryStr}`);
  }, [getCurrentPlanId, history, queryStr]);

  const handleOpenCreatePlan = useCallback(() => {
    openCreatePlan({
      onCreate: () => {
        if (testPlanStatus !== 'todo') {
          testPlanStore.setTestPlanStatus('todo');
        }
        testPlanStore.loadIssueTree();
      },
    });
  }, [testPlanStatus, testPlanStore]);

  const handleRefresh = useCallback(() => {
    testPlanStore.loadAllData();
  }, [testPlanStore]);

  return (
    <HeaderButtons items={[{
      name: intl.formatMessage({ id: 'testPlan_createPlan' }),
      display: true,
      icon: 'playlist_add',
      handler: handleOpenCreatePlan,
    }, {
      name: intl.formatMessage({ id: 'testPlan_editPlan' }),
      display: testPlanStatus !== 'done' && getCurrentPlanId,
      icon: 'edit-o',
      handler: handleOpenEditPlan,
    }, {
      name: intl.formatMessage({ id: 'testPlan_manualTest' }),
      display: getCurrentPlanId && testPlanStatus === 'todo',
      icon: 'play_circle_filled',
      handler: handleUpdatePlanStatus.bind(this, 'doing'),
    }, {
      name: intl.formatMessage({ id: 'testPlan_completePlan' }),
      display: getCurrentPlanId && testPlanStatus === 'doing',
      icon: 'finished',
      handler: handleUpdatePlanStatus.bind(this, 'done'),
    }, {
      name: '计划报告',
      display: getCurrentPlanId && testPlanStatus !== 'todo',
      icon: 'find_in_page-o',
      handler: handleReportClick,
    }, {
      name: intl.formatMessage({ id: 'refresh' }),
      display: true,
      icon: 'refresh',
      handler: handleRefresh,
      iconOnly: true,
    }]}
    />
  );
}
export default observer(TestPlanHeader);
