import React, { useMemo } from 'react';
import { DataSet, Table } from 'choerodon-ui/pro';
import { getProjectId } from '@/common/utils';

const { Column } = Table;
function IssueTable({
  folderId,
}) {
  const dataSet = useMemo(() => new DataSet({
    primaryKey: 'caseId',
    autoQuery: true,
    selection: 'multiple',
    transport: {
      read: {
        url: `/test/v1/projects/${getProjectId()}/case/list_by_folder_id?folder_id=${folderId}`,
        method: 'post',
        transformRequest: (data) => {
          // console.log(data);
          const { summary, caseNum } = data;
          return JSON.stringify({
            // contents:
            searchArgs: {
              summary,
              caseNum,
            },
          });
        },
      },
    },
    fields: [
      { name: 'summary', type: 'string', label: '用例名称' },
      { name: 'caseNum', type: 'string', label: '用例编号' },
      { name: 'folder', type: 'string', label: '文件夹' },     
    ],
    queryFields: [
      { name: 'summary', type: 'string', label: '用例名称' },
      { name: 'caseNum', type: 'string', label: '用例编号' }, 
    ],
  }), [folderId]);
  return (
    <Table dataSet={dataSet} labelLayout="none">
      <Column name="summary" />
      <Column name="caseNum" />
      <Column name="folder" />
    </Table>
  );
}

export default IssueTable;
