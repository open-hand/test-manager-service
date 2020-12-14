import React, { useMemo, useContext } from 'react';
import { Table, DataSet } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import Tip from '@/components/Tip';
import StatusTag from '@/components/StatusTag';
import { getProjectId } from '@/common/utils';
import { find } from 'lodash';
import { renderStatus } from './renderer';
import context from '../../context';
import Card from '../card';
import styles from './index.less';

const { Column } = Table;
const lineHeight = 25;
const style = {
  height: lineHeight,
  display: 'flex',
  alignItems: 'center',
};
const BugTable: React.FC = () => {
  const { store } = useContext(context);
  const { planId, statusList } = store;
  const dataSet = useMemo(() => new DataSet({
    autoQuery: true,
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
  }), [planId]);
  return (
    <Card className={styles.table_card}>
      <div>
        <div className={styles.header}>
          未通过测试的问题项
          <Tip title="未通过测试的问题项" />
        </div>
        <Table
          dataSet={dataSet}
          rowHeight="auto"
        >
          <Column name="summary" />
          <Column name="statusMapVO" width={150} renderer={renderStatus} />
          <Column name="assignee" width={150} style={{ color: 'rgba(0, 0, 0, 0.65)' }} />
          <Column
            name="testFolderCycleCases"
            style={{ color: 'rgba(0, 0, 0, 0.65)' }}
            renderer={({ value }) => (
              <div>
                {/* @ts-ignore */}
                {value.map((v) => <div style={style}>{v.summary}</div>)}
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
      </div>
    </Card>
  );
};
export default observer(BugTable);
