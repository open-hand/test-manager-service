import React, { memo } from 'react';
import PropTypes from 'prop-types';
import isEqual from 'react-fast-compare';
import { FormattedMessage } from 'react-intl';
import { Tooltip } from 'choerodon-ui';
import { Table } from 'choerodon-ui/pro';
import { delta2Html, delta2Text } from '../../../../../common/utils';
import { RichTextShow, User } from '../../../../../components';
import './ExecuteHistoryTable.less';

const { Column } = Table;
const propTypes = {
  dataSet: PropTypes.shape({}).isRequired,
};
const ExecuteHistoryTable = ({
  dataSet,
}) => {
  const columns = [{
    title: <FormattedMessage id="execute_executive" />,
    dataIndex: 'user',
    key: 'user',
    render(user) {
      return (<User user={user} />);
    },
  }, {
    title: <FormattedMessage id="execute_executeTime" />,
    dataIndex: 'lastUpdateDate',
    key: 'lastUpdateDate',
    width: '25%',
    render: lastUpdateDate => <div className="c7ntest-text-dot">{lastUpdateDate}</div>,
  }, {
    title: '操作类型',
    dataIndex: 'field',
    key: 'field',
  }, {
    title: <FormattedMessage id="execute_history_oldValue" />,
    dataIndex: 'oldValue',
    key: 'oldValue',
    render(oldValue, record) {
      switch (record.field) {
        case '注释': {
          return (
            <Tooltip title={<RichTextShow data={delta2Html(oldValue)} />}>
              <div
                title={delta2Text(oldValue)}
                style={{
                  width: 100,
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap',
                }}
              >
                {delta2Text(oldValue)}
              </div>
            </Tooltip>
          );
        }
        default: {
          return (
            <div
              className="c7ntest-text-dot"
            >
              {oldValue}
            </div>
          );
        }
      }
    },
  }, {
    title: <FormattedMessage id="execute_history_newValue" />,
    dataIndex: 'newValue',
    key: 'newValue',
    render(newValue, record) {
      switch (record.field) {
        case '注释': {
          return (
            <Tooltip title={<RichTextShow data={delta2Html(newValue)} />}>
              <div
                style={{
                  width: 100,
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap',
                }}
              >
                {delta2Text(newValue)}
              </div>
            </Tooltip>
          );
        }
        default: {
          return (
            <div
              className="c7ntest-text-dot"
            >
              {newValue}
            </div>
          );
        }
      }
    },
  }];
  return (
    // <Table
    //   className="c7ntest-ExecuteHistoryTable"
    //   filterBar={false}
    //   dataSource={dataSource}
    //   columns={columns}
    //   pagination={pagination}
    //   onChange={onChange}
    // />
    // 状态
    <Table dataSet={dataSet}>
      <Column name="user" />
      <Column name="lastUpdateDate" />
      <Column name="user" />
      <Column name="oldValue" />
      <Column name="newValue" />
    </Table>
  );
};
ExecuteHistoryTable.propTypes = propTypes;
export default memo(ExecuteHistoryTable, isEqual);
