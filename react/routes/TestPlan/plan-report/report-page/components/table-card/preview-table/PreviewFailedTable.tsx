/* eslint-disable jsx-a11y/click-events-have-key-events */
/* eslint-disable jsx-a11y/no-static-element-interactions */
import React, {
  useMemo, useContext, useCallback, useState, useEffect,
} from 'react';
import { DataSet, Tooltip } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { axios } from '@choerodon/boot';
import { useHistory } from 'react-router-dom';
// @ts-ignore
import Tip from '@/components/Tip';
// @ts-ignore
import StatusTag from '@/components/StatusTag';
// @ts-ignore
import { getProjectId } from '@/common/utils';
import { find } from 'lodash';
import { renderStatus, renderAssignee } from '../renderer';
import context from '../../../context';
import Table, { Column } from './table';

const lineHeight = 25;
const style = {
  height: lineHeight,
  display: 'flex',
  alignItems: 'center',
};
const PreviewFailedTable: React.FC = () => {
  const [data, setData] = useState<any[]>([]);
  const { store, loadTask } = useContext(context);
  const { statusList, planId } = store;
  const loadData = useCallback(() => {
    loadTask?.push('FailedTable');
    axios.post(`/test/v1/projects/${getProjectId()}/plan/${planId}/reporter/issue?page=0&size=0`, {}).then((res: any) => {
      setData(res.content);
      loadTask?.push('finish-FailedTable');
    }).catch(() => {
      loadTask?.push('error');
    });
  }, [planId]);
  useEffect(() => {
    loadData();
  }, [loadData]);
  const columns: Column<any>[] = [
    { title: '问题概要', dataIndex: 'summary' },
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
    <Table<any>
      data={data}
      columns={columns}
      primaryKey="issueId"
    />
  );
};
export default observer(PreviewFailedTable);
