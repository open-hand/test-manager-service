import React, { useContext, useState, useEffect } from 'react';
import { observer } from 'mobx-react-lite';

import TestStepTable from '@/components/TestStepTable';

function EditTestStepTable(props) {
  const {
    executeId, parentDataSet, name,
  } = props;
  const { current } = parentDataSet;
  const [testStepData, setTestStepData] = useState([]);
  const onClone = (newData, originStep) => ({
    ...originStep,
    _status: 'add',
    executeStepId: Math.random(),
  });
  const onCreate = (newStep) => ({
    ...newStep,
    _status: 'add',
    executeStepId: Math.random(),
  });
  useEffect(() => {
    if (current) {
      current.set(name, testStepData.filter((step) => !step.stepIsCreating));
    }
  }, [current, name, testStepData]);
  useEffect(() => {
    if (current && current.get('caseStepVOS').length !== 0) {
      setTestStepData(current.get('caseStepVOS'));
    }
  }, [current]);
  return (
    <TestStepTable
      dragKey="executeStepId"
      disabled={false}
      data={testStepData}
      setData={setTestStepData}
      caseId={executeId}
      onClone={onClone}
      onCreate={onCreate}
    />
  );
}
export default observer(EditTestStepTable);
