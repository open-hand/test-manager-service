import React, { useState, useEffect } from 'react';
import TestStepTable from '../TestStepTable';

function CreateTestStepTable(props) {
  const { name, parentDataSet, caseId } = props;
  const [testStepData, setTestStepData] = useState([]);
  useEffect(() => {
    parentDataSet.current.set(name, testStepData);
  }, [name, parentDataSet, testStepData]);
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
