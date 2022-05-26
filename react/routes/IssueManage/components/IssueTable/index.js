import React from 'react';
import TableCache from '@/components/table-cache';
import IssueTable from './IssueTable';

function IssueTableCache(props) {
  return (
    <TableCache type="testManger">
      {(cacheProps) => {
        // summary caseNum customNum sequence createUser creationDate lastUpdateUser lastUpdateDate
        const { listLayoutColumns } = cacheProps.cached;
        if (!listLayoutColumns) {
          return <IssueTable {...props} />;
        }
        const displayColumns = listLayoutColumns.filter((column) => column.display).map((column) => column.columnCode);
        const defaultFilteredColumns = !displayColumns.length ? ['summary'] : displayColumns;
        return <IssueTable {...props} defaultFilteredColumns={defaultFilteredColumns} />;
      }}
    </TableCache>
  );
}
export default IssueTableCache;
