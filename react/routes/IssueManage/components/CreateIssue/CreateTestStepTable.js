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
    console.log('pDataSet', pDataSet.current, pDataSet.current.get(name));
  }, [name, pDataSet, testStepData]);
  useEffect(() => {
    console.log(' useEffect pDataSet');
  }, []);
  return (
    <TestStepTable
      disabled={false}
      data={testStepData}
    />
  );
}
export default CreateTestStepTable;
