import React from 'react';
import PropTypes from 'prop-types';
import { Popover } from 'choerodon-ui';
import { Table } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import Loading from '@choerodon/agile/lib/components/Loading';
import { delta2Text } from '@/common/utils';
import { CKEditorViewer, User } from '../../../../../../components';
import './ExecuteHistoryTable.less';

const { Column } = Table;
const propTypes = {
  dataSet: PropTypes.shape({}).isRequired,
};
const ExecuteHistoryTable = ({
  dataSet,
}) => {
  function renderUser({ value }) {
    return <User user={value} />;
  }
  const renderValue = ({ text, record }) => (record.get('field') === '注释'
    ? (
      <Popover content={<CKEditorViewer value={text} />} title={null}>
        <div
          title={delta2Text(text)}
          style={{
            width: 100,
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'nowrap',
          }}
        >
          {delta2Text(text)}
        </div>
      </Popover>
    ) : text);
  return (
    // 状态
    <Loading loadId="history" loading={dataSet.status === 'loading'}>
      <Table dataSet={dataSet} spin={{ spinning: false }} queryBar="none">
        <Column name="user" renderer={renderUser} />
        <Column name="lastUpdateDate" />
        <Column name="field" />
        <Column
          name="oldValue"
          renderer={renderValue}
        />
        <Column
          name="newValue"
          renderer={renderValue}
        />
      </Table>
    </Loading>
  );
};
ExecuteHistoryTable.propTypes = propTypes;
export default observer(ExecuteHistoryTable);
