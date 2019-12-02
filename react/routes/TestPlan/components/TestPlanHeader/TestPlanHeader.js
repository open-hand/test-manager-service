import React, { useContext, useCallback } from 'react';
import { observer } from 'mobx-react-lite';
import { FormattedMessage } from 'react-intl';
import { Button } from 'choerodon-ui/pro';
import { openEditPlan } from '../TestPlanModal';
import Store from '../../stores';

function TestPlanHeader() {
  const { testPlanStore, createAutoTestStore } = useContext(Store);
  const { testPlanStatus, currentPlanId } = testPlanStore;
  const handleCreateAutoTest = () => {
    createAutoTestStore.setVisible(true);
  };
  const handleOpenEditPlan = useCallback(async () => {    
    openEditPlan({
      planId: currentPlanId,
    });
  }, [currentPlanId]);
  return (
    <React.Fragment>
      {
        testPlanStatus !== 'done' ? (
          <React.Fragment>
            {currentPlanId && (
              <Button icon="mode_edit" onClick={handleOpenEditPlan}>
                <FormattedMessage id="testPlan_editPlan" />
              </Button>
            )}
            {
              testPlanStatus === 'todo' ? (
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
    </React.Fragment>
  );
}
export default observer(TestPlanHeader);
