import React, {
  useState,
} from 'react';
import {
  Button,
} from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import SelectFocusLoad from '@/components/SelectFocusLoad';

function BatchModal({
  onCancel, onAssign, testPlanStore,
}) {
  const { checkIdMap, assignToUserId } = testPlanStore;
  const [loading, setLoading] = useState(false);

  const submit = async () => {
    setLoading(true);
    await testPlanStore.executesAssignTo();
    onCancel();
    setLoading(false);
  };

  const handleAssignToChange = (value) => {
    testPlanStore.setAssignToUserId(value);
  };

  return (
    <>
      {
        testPlanStore.batchAction === 'assign' && (
        <div style={{ padding: 15 }}>
          <SelectFocusLoad
            key={testPlanStore.assignToUserId}
            allowClear
            style={{ display: 'flex', width: 300, margin: '15px 0 0' }}
            placeholder="批量指派"
            label="批量指派"
            getPopupContainer={(trigger) => trigger.parentNode}
            type="user"
            value={testPlanStore.assignToUserId}
            onChange={handleAssignToChange}
          />
          <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
            <Button
              onClick={onCancel}
              disabled={loading}
              style={{
                fontWeight: 500,
              }}
            >
              取消
            </Button>
            <Button
              disabled={checkIdMap.size === 0 || !assignToUserId?.length}
              color="blue"
              loading={loading}
              style={{
                fontWeight: 500,
              }}
              onClick={() => {
                submit();
              }}
            >
              确定
            </Button>
          </div>
        </div>
        )
      }
    </>
  );
}
export default observer(BatchModal);
