import React, {
  useCallback, useEffect, useMemo, useState,
} from 'react';
import SelectUser from '@choerodon/agile/lib/components/select/select-user';
import { IModalProps } from '@choerodon/agile/lib/common/types';
import { Radio, Modal, DataSet } from 'choerodon-ui/pro';
import { batchAssignFolder, batchCancelAssignFolder } from '@/api/TestPlanApi';

interface BatchPlanCycleData {
  planId: string
  cycleId: string
}
const BatchAssign: React.FC<{ modal?: IModalProps, data: BatchPlanCycleData, handleOk?: Function }> = ({ modal, data, handleOk }) => {
  const [operationType, setOperationType] = useState<string>('select');
  const dataSet = useMemo(() => new DataSet({
    autoCreate: true,
    fields: [{
      name: 'assigneeId',
      label: '执行人',
      required: operationType === 'select',
    }],
  }), [operationType]);
  const handleSubmit = useCallback(async () => {
    if (!await dataSet.current?.validate()) {
      return false;
    }
    const assigneeId = dataSet.current?.get('assigneeId');
    const newData = { cycle_id: data.cycleId, plan_id: data.planId, assign_user_id: assigneeId };
    operationType === 'select' ? await batchAssignFolder(newData) : await batchCancelAssignFolder(newData);
    handleOk && handleOk();
    return true;
  }, [data.cycleId, data.planId, dataSet, handleOk, operationType]);
  useEffect(() => {
    modal?.handleOk(handleSubmit);
  }, [handleSubmit, modal]);
  return (
    <div>
      <div style={{ marginBottom: '.2rem' }}>
        <Radio name="select" value="select" checked={operationType === 'select'} onChange={setOperationType}>选择执行人</Radio>
        <Radio name="select" value="clear" checked={operationType !== 'select'} onChange={setOperationType}>清空执行人</Radio>
      </div>
      {operationType === 'select' && <SelectUser dataSet={dataSet} name="assigneeId" placeholder="请选择执行人" style={{ width: '100%' }} />}
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
