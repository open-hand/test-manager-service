import React, { useContext, useCallback } from 'react';
import { Choerodon } from '@choerodon/boot';
import { toJS } from 'mobx';
import { observer } from 'mobx-react-lite';
import { FormattedMessage } from 'react-intl';
import { Button } from 'choerodon-ui/pro';
import { openEditPlan } from '../TestPlanModal';
import { updatePlanStatus } from '@/api/TestPlanApi';
import Store from '../../stores';

function TestPlanHeader() {
  const { testPlanStore, createAutoTestStore } = useContext(Store);
  const { testPlanStatus, getCurrentPlanId } = testPlanStore;

  const handleUpdatePlanStatus = (newStatus) => {
    const { getItem } = testPlanStore.treeRef.current || {};
    const planItem = getItem(Number(testPlanStore.getCurrentPlanId)) || {};
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

  const handleCreateAutoTest = () => {
    createAutoTestStore.setVisible(true);
  };
  const handleOpenEditPlan = useCallback(async () => {
    openEditPlan({
      planId: getCurrentPlanId,
    });
  }, [getCurrentPlanId]);
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
              testPlanStatus === 'todo' ? (
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
