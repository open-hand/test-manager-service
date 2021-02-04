import React, { useCallback, ReactNode } from 'react';
import { Icon } from 'choerodon-ui';
import styles from './Table.less';

export interface Column<T = {}> {
  title: string
  dataIndex: string
  render?: (record: T) => React.ReactNode
  width?: number
}
interface Props<T> {
  columns: Column<T>[]
  data: T[]
  primaryKey: string
}
export function createColumns<T extends {}>(columns: Column<T>[]): Column<T>[] {
  return columns;
}
function Table<T extends { [key: string]: any }>({
  columns,
  data,
  primaryKey,
}: Props<T>) {
  const renderTreeLikeData = useCallback((treeData: T[], level: number = 0): ReactNode => treeData.map((item) => {
    const hasChildren = item.children
      && item.children instanceof Array
      && item.children.length > 0;
    return (
      <>
        <tr key={item[primaryKey]} data-level={level}>
          {columns.map((column, index) => (
            <td>
              <span style={{ display: 'flex', alignItems: 'center' }}>
                {index === 0 && <span style={{ display: 'inline-block', flexShrink: 0, width: level * 20 }} />}
                {index === 0 && (
                <Icon
                  type="arrow_drop_down"
                  style={{
                    visibility: hasChildren ? 'visible' : 'hidden',
                    marginRight: 5,
                    marginTop: -2,
                  }}
                />
                )}
                {column.render
                  ? column.render(item) : item[column.dataIndex]}
              </span>
            </td>
          ))}
        </tr>
        {hasChildren && renderTreeLikeData(item.children, level + 1)}
      </>
    );
  }), [columns, primaryKey]);
  return (
    <table className={styles.table}>
      <colgroup>
        {columns.map((column) => (
          <col key={column.dataIndex} width={column.width} />
        ))}
      </colgroup>
      <thead>
        <tr>
          {columns.map((column, index) => (
            <th key={column.dataIndex}>
              {index === 0 && <span style={{ width: 22, display: 'inline-block' }} />}
              {column.title}
            </th>
          ))}
        </tr>
      </thead>
      <tbody>
        {renderTreeLikeData(data)}
      </tbody>
    </table>
  );
}
export default Table;
