/* eslint-disable no-console */
import React, {
  Component, useState, useEffect, useMemo,
} from 'react';
import TestStepTable from '../TestStepTable';

function CreateTestStepTable(props) {
  const { name, pDataSet } = props;
  const [testStepData, setTestStepData] = useState([]);
  useEffect(() => {
    pDataSet.current.set(name, testStepData);
  }, [name, pDataSet, testStepData]);
  return (
    <TestStepTable
      disabled={false}
      data={testStepData}
      setData={setTestStepData}
    />
  );
}
export default CreateTestStepTable;
