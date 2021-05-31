import React, { useContext } from 'react';
import { Tooltip } from 'choerodon-ui';
import { Table } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';

const { Column } = Table;

const TestStepTable = (props) => {
  const { dataSet } = props;

  const renderIndex = ({
    value, dataSet,
  }) => dataSet.findIndex((item) => Number(item.get('stepId')) === Number(value)) + 1;

  const renderColumn = ({ value }) => (
    <Tooltip title={value} placement="topLeft">
      {value || '-'}
    </Tooltip>
  );
  return (
    <Table labelLayout="float" pristine dataSet={dataSet} queryBar="none">
      <Column
        name="stepId"
        width={50}
        renderer={renderIndex}
      />
      <Column name="testStep" renderer={renderColumn} />
      <Column name="testData" renderer={renderColumn} />
      <Column name="expectedResult" renderer={renderColumn} />
    </Table>
  );
};

export default observer(TestStepTable);
