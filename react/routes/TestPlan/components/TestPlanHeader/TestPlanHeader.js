import React, { useContext, useCallback } from 'react';
import { toJS } from 'mobx';
import { observer } from 'mobx-react-lite';
import { FormattedMessage } from 'react-intl';
import { Button } from 'choerodon-ui/pro';
import { openEditPlan } from '../TestPlanModal';
import Store from '../../stores';

function TestPlanHeader() {
  const { testPlanStore, createAutoTestStore } = useContext(Store);
  const { testPlanStatus, getCurrentPlanId, treeData } = testPlanStore;
  console.log(toJS(treeData));
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
                <Button icon="play_circle_filled">
                  <FormattedMessage id="testPlan_manualTest" />
                </Button>
              ) : (
                <Button icon="check_circle" disabled={testPlanStatus === 'doing' && treeData.rootIds && treeData.rootIds.length} onClick={handleCreateAutoTest}>
                  <FormattedMessage id="testPlan_completePlan" />
                </Button>
              )
            }
            <Button icon="auto_test">
              <FormattedMessage id="testPlan_autoTest" />
            </Button>
          </React.Fragment>
        ) : ''
      }
    </React.Fragment>
  );
}
export default observer(TestPlanHeader);
