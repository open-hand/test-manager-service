/* eslint-disable jsx-a11y/click-events-have-key-events */
/* eslint-disable jsx-a11y/no-static-element-interactions */
import React, {
  useMemo, useContext, useEffect,
} from 'react';
import { Table, DataSet, Tooltip } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { useHistory } from 'react-router-dom';
// @ts-ignore
import StatusTag from '@/components/StatusTag';
// @ts-ignore
import { getProjectId } from '@/common/utils';
import { find } from 'lodash';
import { getStatusList } from '@/api/TestStatusApi';
import { renderStatus, renderAssignee } from './renderer';
import context from '../../context';
import Card from '../card';
import styles from './index.less';
import { issueLink, executeDetailLink } from '../../../../../../common/utils';
import PreviewFailedTable from './preview-table/PreviewFailedTable';

const { Column } = Table;
const lineHeight = 25;
const style = {
  height: lineHeight,
  display: 'flex',
  alignItems: 'center',
};
const BugTable: React.FC = () => {
  const { store, preview } = useContext(context);
  const { planId, statusList } = store;
  const history = useHistory();
  const statusDataSet = useMemo(() => new DataSet({
    data: [],
  }), []);
  const dataSet = useMemo(() => new DataSet({
    autoQuery: false,
    selection: false,
    transport: {
      read: () => ({
        method: 'post',
        url: `/test/v1/projects/${getProjectId()}/plan/${planId}/reporter/issue`,
      }),
    },
    fields: [{
      name: 'summary',
      label: '问题概要',
    }, {
      name: 'statusMapVO',
      label: '状态',
    }, {
      name: 'assignee',
      label: '经办人',
    }, {
      name: 'testFolderCycleCases',
      label: '关联测试',
    }, {
      name: 'testStatus',
      label: '测试状态',
    }],
    queryFields: [{
      name: 'summary',
      label: '问题概要',
    }, {
      name: 'caseSummary',
      label: '关联测试',
    }, {
      name: 'statusId',
      label: '状态',
      lookupAxiosConfig: () => ({
        url: `/agile/v1/projects/${getProjectId()}/schemes/query_status_by_project_id?apply_type=${'agile'}`,
        method: 'get',
      }),
      valueField: 'id',
      textField: 'name',
    }, {
      name: 'executionStatus',
      label: '执行状态',
      options: statusDataSet,
      valueField: 'statusId',
      textField: 'statusName',
    }],
  }), [planId, statusDataSet]);
  useEffect(() => {
    (async () => {
      const res = await getStatusList('CYCLE_CASE');
      statusDataSet.loadData(res);
      const failedStatus = find(res, ((status) => String(status.projectId) === '0' && status.statusName === '失败'));
      if (failedStatus) {
        dataSet?.queryDataSet?.current?.set('executionStatus', failedStatus.statusId);
      }
    })();
  }, [dataSet, statusDataSet]);
  return (
    <Card className={styles.table_card}>
      <div style={{ width: '100%' }}>
        <div className={styles.header}>
          关联的问题项
        </div>
        {preview ? <PreviewFailedTable /> : (
          <Table
            dataSet={dataSet}
            rowHeight="auto"
          >
            <Column
              name="summary"
              renderer={({ value, record }) => (
                <Tooltip title={value}>
                  <span
                    className="c7n-test-table-cell-click"
                    onClick={() => {
                      history.push(issueLink(record?.get('issueId'), null, value));
                    }}
                    style={{
                      whiteSpace: 'nowrap',
                      display: 'inline-block',
                      maxWidth: '100%',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                    }}
                  >
                    {value}
                  </span>
                </Tooltip>
              )}
            />
            {/* @ts-ignore */}
            <Column name="statusMapVO" width={150} renderer={renderStatus} />
            {/* @ts-ignore */}
            <Column name="assignee" width={150} style={{ color: 'rgba(0, 0, 0, 0.65)' }} renderer={renderAssignee} />
            <Column
              name="testFolderCycleCases"
              renderer={({ value }) => (
                <div>
                  {/* @ts-ignore */}
                  {value.map((v) => (
                    <div
                      style={style}
                      className="c7n-test-table-cell-click"
                      onClick={() => {
                        history.push(executeDetailLink(v.executeId, {
                          plan_id: planId,
                          cycle_id: '',
                        }));
                      }}
                    >
                      {v.summary}
                    </div>
                  ))}
                </div>
              )}
            />
            <Column
              name="testStatus"
              width={150}
              renderer={({ record }) => {
                // @ts-ignore
                const testFolderCycleCases = record.get('testFolderCycleCases');
                return (
                  <div>
                    {/* @ts-ignore */}
                    {testFolderCycleCases.map((cycle) => {
                      const { executionStatus } = cycle;
                      const status = find(statusList, { statusId: executionStatus });
                      return (
                        <div style={style}>
                          {status && (
                          <StatusTag
                            // @ts-ignore
                            style={{
                              width: 'auto',
                            }}
                            // @ts-ignore
                            status={{
                              // @ts-ignore
                              name: status.statusName,
                              // @ts-ignore
                              colour: status.statusColor,
                            }}
                          />
                          )}
                        </div>
                      );
                    })}
                  </div>
                );
              }}
            />
          </Table>
        )}

      </div>
    </Card>
  );
};
export default observer(BugTable);
