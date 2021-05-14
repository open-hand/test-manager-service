import React, {
  useState,
} from 'react';
import {
  Button,
} from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
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
          <SelectFocusLoad
            allowClear
            loadWhenMount
            style={{ display: 'flex', margin: '15px 0' }}
            placeholder="批量指派"
            type="user"
            value={testPlanStore.assignToUserId}
            onChange={handleAssignToChange}
            dropdownClassName={styles.assignSelectDropdown}
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
