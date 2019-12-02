import React, { useState, useEffect } from 'react';
import TestStepTable from '@/components/TestStepTable';

function UpdateTestStepTable(props) {
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
export default UpdateTestStepTable;
