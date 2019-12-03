import React, { useContext } from 'react';
import { Table } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';

const { Column } = Table;

const TestStepTable = (props) => {
  const { dataSet } = props;
  return (
    <Table labelLayout="float" pristine dataSet={dataSet}>
      <Column 
        name="stepId"
      />
      <Column name="testStep" />
      <Column name="testData" />
      <Column name="expectedResult" />
    </Table>
  );
};

export default observer(TestStepTable);
