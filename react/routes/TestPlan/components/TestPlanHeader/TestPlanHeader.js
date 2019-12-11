import React, { useContext, useCallback } from 'react';
import { Choerodon } from '@choerodon/boot';
import { toJS } from 'mobx';
import { observer } from 'mobx-react-lite';
import { FormattedMessage } from 'react-intl';
import { Button, Modal } from 'choerodon-ui/pro';
import { openEditPlan } from '../TestPlanModal';
import { updatePlanStatus } from '@/api/TestPlanApi';
import Store from '../../stores';
import './TestPlanHeader.less';

const confirmCompletePlanModalKey = Modal.key();

function TestPlanHeader() {
  const { testPlanStore, createAutoTestStore } = useContext(Store);
  const { testPlanStatus, getCurrentPlanId, statusRes } = testPlanStore;

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
      title: `确定完成计划 ${planItem.item.data.name}？`,
      children: (
        <div>
          <p>当前计划的完成情况如下：</p>
          <p>{`执行总数：${statusRes.total}`}</p>
          {
            statusRes.statusVOList.map(item => (
              <p>{`${item.statusName}：${item.count}`}</p>
            ))
          }
          <p>{`确定要完成计划 ${planItem.item.data.name} 吗？`}</p>
        </div>
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
    const { getItem, updateTree } = testPlanStore.treeRef.current;
    const oldPlan = getItem(newPlan.planId);
    updateTree(newPlan.planId, {
      data: {
        ...oldPlan.data,
        name: newPlan.name,
        objectVersionNumber: newPlan.objectVersionNumber,
      },
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
