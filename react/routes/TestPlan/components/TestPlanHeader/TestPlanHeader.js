import React, { useContext, useCallback, useEffect } from 'react';
import { Choerodon } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import { FormattedMessage } from 'react-intl';
import { Button, Modal } from 'choerodon-ui/pro';
import { openEditPlan } from '../TestPlanModal';
import { updatePlanStatus } from '@/api/TestPlanApi';
import ConfirmCompleteModalChildren from './components/ConfirmCompleteModalChildren';
import Store from '../../stores';
import './TestPlanHeader.less';

const confirmCompletePlanModalKey = Modal.key();

function TestPlanHeader() {
  const { testPlanStore, createAutoTestStore } = useContext(Store);
  const { testPlanStatus, getCurrentPlanId } = testPlanStore;

  const onUpdatePlanStatus = (planItem, newStatus) => {
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
  };

  const confirmCompletePlan = (planItem, newStatus) => {
    Modal.open({
      key: confirmCompletePlanModalKey,
      title: '完成计划确认',
      children: (
        <ConfirmCompleteModalChildren planName={planItem.item.data.name} testPlanStore={testPlanStore} />
      ),
      okText: '确定',
      onOk: onUpdatePlanStatus.bind(this, planItem, newStatus),
      cancelText: '取消',
      style: { width: '5.6rem' },
      className: 'c7ntest-testPlan-completePlan-confirm-modal',
    });
  };

  const handleUpdatePlanStatus = (newStatus) => {
    const { getItem } = testPlanStore.treeRef.current || {};
    const planItem = getItem(Number(testPlanStore.getCurrentPlanId)) || {};
    if (planItem.item && planItem.item.id) {
      if (newStatus === 'doing') {
        onUpdatePlanStatus(planItem, newStatus);
      } else {
        confirmCompletePlan(planItem, newStatus);
      }
    }
  };

  const handleCreateAutoTest = () => {
    createAutoTestStore.setVisible(true);
  };
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

  return (
    <React.Fragment>
      {
        testPlanStatus !== 'done' ? (
          <React.Fragment>
            {getCurrentPlanId && (
              <Button icon="mode_edit" onClick={handleOpenEditPlan}>
                <FormattedMessage id="testPlan_editPlan" />
              </Button>
            )}
            {
              getCurrentPlanId && testPlanStatus === 'todo' ? (
                <Button icon="play_circle_filled" onClick={handleUpdatePlanStatus.bind(this, 'doing')}>
                  <FormattedMessage id="testPlan_manualTest" />
                </Button>
              ) : (
                getCurrentPlanId && (
                <Button icon="check_circle" disabled={testPlanStatus !== 'doing'} onClick={handleUpdatePlanStatus.bind(this, 'done')}>
                  <FormattedMessage id="testPlan_completePlan" />
                </Button>
                )
              )
            }
            {/* <Button icon="auto_test">
              <FormattedMessage id="testPlan_autoTest" />
            </Button> */}
          </React.Fragment>
        ) : ''
      }
    </React.Fragment>
  );
}
export default observer(TestPlanHeader);
