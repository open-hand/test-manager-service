import React, { useCallback, useEffect, useState } from 'react';
import SelectUser from '@choerodon/agile/lib/components/select/select-user';
import { IModalProps } from '@choerodon/agile/lib/common/types';
import { Modal } from 'choerodon-ui/pro';
import { batchAssignFolder, batchCancelAssignFolder } from '@/api/TestPlanApi';

interface BatchPlanCycleData {
  planId: string
  cycleId: string
}
const BatchAssign: React.FC<{ modal?: IModalProps, data: BatchPlanCycleData, handleOk?: Function }> = ({ modal, data, handleOk }) => {
  const [assigneeId, setAssigneeId] = useState<string | null>();
  const handleSubmit = useCallback(async () => {
    const newData = { cycle_id: data.cycleId, plan_id: data.planId, assign_user_id: assigneeId };
    assigneeId ? await batchAssignFolder(newData) : await batchCancelAssignFolder(newData);
    handleOk && handleOk();
    return true;
  }, [assigneeId, data, handleOk]);
  useEffect(() => {
    modal?.handleOk(handleSubmit);
  }, [handleSubmit, modal]);
  return (
    <div>
      <SelectUser label="执行人" placeholder="请选择执行人" style={{ width: '100%' }} onChange={setAssigneeId} />
    </div>
  );
};
function openBatchAssignModal(data: BatchPlanCycleData, handleOk: () => void) {
  Modal.open({
    title: '分配执行用例',
    children: <BatchAssign data={data} handleOk={handleOk} />,
  });
}
export default openBatchAssignModal;
