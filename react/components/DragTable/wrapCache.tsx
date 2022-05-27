import React, { useCallback, useRef } from 'react';
import { useSimpleUpdateColumnCache } from '@/hooks/data/useTableColumns';
/**
 *  注入更新表格列方法
 */
function wrapDragTableCache(Element: any, type: string, defaultVisibleColumnCodes?: string[]) {
  function DragColumnCacheTable(props: any) {
    const { tableRef, cached } = props;
    const hiddenColumnCodes = ['checkbox', 'more', 'action'];
    const columnCodes = props.columns?.map((column: any) => column.key).filter((columnCode:string) => !hiddenColumnCodes.includes(columnCode));
    const { updateColumnCache } = useSimpleUpdateColumnCache(type, columnCodes);
    const handleColumnFilterChange = useCallback((visibleColumns) => {
      updateColumnCache(visibleColumns);
    }, [updateColumnCache]);
    const { listLayoutColumns } = cached || {};
    if (!listLayoutColumns) {
      return <Element ref={tableRef} {...props} selectedKeys={defaultVisibleColumnCodes} onColumnFilterChange={handleColumnFilterChange} />;
    }
    const displayColumns = listLayoutColumns.filter((column: any) => column.display).map((column: any) => column.columnCode);
    const defaultFilteredColumns = !displayColumns.length ? ['summary'] : displayColumns;
    return <Element ref={tableRef} selectedKeys={defaultFilteredColumns} {...props} onColumnFilterChange={handleColumnFilterChange} />;
  }
  function RCForwardRef(props: any, tableRef: any) {
    return <DragColumnCacheTable {...props} tableRef={tableRef} />;
  }
  RCForwardRef.displayName = 'DragColumnCacheTable';
  return React.forwardRef(RCForwardRef);
}
export default wrapDragTableCache;
