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
    stepId: Math.random(),
  });
  useEffect(() => {
    if (current) {
      current.set(name, testStepData);
    }
  }, [current, name, testStepData]);
  useEffect(() => {
    if (current) {
      setTestStepData(current.get('caseStepVOS'));
    }
  }, [current]);
  return (
    <TestStepTable
      disabled={false}
      data={testStepData}
      setData={setTestStepData}
      caseId={executeId}
      onClone={onClone}
    />
  );
}
export default observer(EditTestStepTable);
