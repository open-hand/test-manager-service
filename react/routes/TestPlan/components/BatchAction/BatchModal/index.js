import React, {
  useState,
} from 'react';
import {
  Button,
} from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import SelectUser from '@choerodon/agile/lib/components/select/select-user';
import SelectFocusLoad from '@/components/SelectFocusLoad';
import styles from './index.less';

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
        <div style={{
          padding: '15px 15px 20px',
          height: 160,
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'space-between',
        }}
        >
          <SelectUser
            clearButton
            placeholder="批量指派"
            onChange={handleAssignToChange}
            value={testPlanStore.assignToUserId}
            style={{ display: 'flex', margin: '15px 0' }}
            selected={testPlanStore.assignToUserId}
            className={styles.assignToUserSelect}
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
              color="primary"
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
