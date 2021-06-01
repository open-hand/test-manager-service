/* eslint-disable jsx-a11y/click-events-have-key-events */
/* eslint-disable jsx-a11y/no-static-element-interactions */
import React, { useMemo, useContext, useEffect } from 'react';
import { Table, DataSet, Tooltip } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { useHistory } from 'react-router-dom';
// @ts-ignore
import Tip from '@/components/Tip';
// @ts-ignore
import StatusTag from '@/components/StatusTag';
// @ts-ignore
import { getProjectId } from '@/common/utils';
import { find } from 'lodash';
import { renderStatus, renderAssignee } from './renderer';
import context from '../../context';
import Card from '../card';
import styles from './index.less';
import { issueLink, executeDetailLink } from '../../../../../../common/utils';
import PreviewBugTable from './preview-table/PreviewBugTable';

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
  const dataSet = useMemo(() => new DataSet({
    autoQuery: !preview,
    selection: false,
    transport: {
      read: () => ({
        method: 'post',
        url: `/test/v1/projects/${getProjectId()}/plan/${planId}/reporter/bug`,
      }),
    },
    fields: [{
      name: 'summary',
      label: '缺陷概要',
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
      lookupAxiosConfig: () => ({
        url: `/test/v1/projects/${getProjectId()}/status/query`,
        method: 'post',
        data: {
          statusType: 'CYCLE_CASE',
          projectId: getProjectId(),
        },
      }),
      valueField: 'statusId',
      textField: 'statusName',
    }],
  }), [planId]);
  return (
    <Card className={styles.table_card}>
      <div style={{ width: '100%' }}>
        <div className={styles.header}>
          缺陷
          {!preview ? <Tip title="测试执行所关联的缺陷" /> : null}
        </div>
        {preview ? <PreviewBugTable /> : (
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
            <Column name="assignee" width={150} style={{ color: 'var(--text-color3)' }} renderer={renderAssignee} />
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
