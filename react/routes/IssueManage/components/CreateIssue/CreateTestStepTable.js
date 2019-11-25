import React, { useState, useEffect } from 'react';
import TestStepTable from '../TestStepTable';

function CreateTestStepTable(props) {
  const { name, pDataSet, caseId } = props;
  const [testStepData, setTestStepData] = useState([]);
  useEffect(() => {
    pDataSet.current.set(name, testStepData);
  }, [name, pDataSet, testStepData]);
  return (
    <TestStepTable
      disabled={false}
      data={testStepData}
      setData={setTestStepData}
      caseId={caseId}
    />
  );
}
export default CreateTestStepTable;
