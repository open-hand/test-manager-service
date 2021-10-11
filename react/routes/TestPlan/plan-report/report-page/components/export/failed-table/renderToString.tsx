/* eslint-disable jsx-a11y/click-events-have-key-events */
/* eslint-disable jsx-a11y/no-static-element-interactions */
import React from 'react';
// @ts-ignore
import { find } from 'lodash';
import StatusTag from '@/components/StatusTag';
import { renderStatus, renderAssignee } from '../../table-card/renderer';
import Table, { Column } from '../table';
import Section from '../section';

const lineHeight = 25;
const style = {
  height: lineHeight,
  display: 'flex',
  alignItems: 'center',
};

interface PreviewFailedTableProps {
  data: any[]
  statusList: any[]
}
const PreviewFailedTable: React.FC<PreviewFailedTableProps> = ({ data, statusList }) => {
  const columns: Column<any>[] = [
    { title: '工作项概要', dataIndex: 'summary' },
    { title: '状态', dataIndex: 'statusMapVO', render: ({ statusMapVO }) => renderStatus({ value: statusMapVO }) },
    { title: '经办人', dataIndex: 'assignee', render: ({ assignee }) => renderAssignee({ value: assignee }) },
    {
      title: '关联测试',
      dataIndex: 'testFolderCycleCases',
      render: ({ testFolderCycleCases }) => (
        <div>
          {/* @ts-ignore */}
          {testFolderCycleCases.map((v) => (
            <div
              style={style}
              className="c7n-test-table-cell-click"
            >
              {v.summary}
            </div>
          ))}
        </div>
      ),
    },
    {
      title: '测试状态',
      dataIndex: 'testStatus',
      render: ({ testFolderCycleCases }) => (
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
      ),
    },
  ];
  return (
    <Section title="关联的工作项">
      <Table<any>
        data={data}
        columns={columns}
        primaryKey="issueId"
      />
    </Section>
  );
};
export default PreviewFailedTable;
