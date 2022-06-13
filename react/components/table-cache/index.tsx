import React from 'react';
import { usePersistFn, useUnmount } from 'ahooks';
import { localPageCacheStore } from '@choerodon/agile/lib/stores/common/LocalPageCacheStore';
import Loading from '@/components/Loading';
import useTableColumns from '@/hooks/data/useTableColumns';
import { ListLayoutColumnVO } from '@/api/CacheColumn';

interface Cache {
  pagination: {
    current: number,
    pageSize: number
  }
  visibleColumns: string[]
  listLayoutColumns?: ListLayoutColumnVO[]
}

export interface TableCacheRenderProps {
  cached: Cache
  updateCache: (cache: Cache) => void
}
export interface TableCacheProps {
  type: string
  projectId?: string
  children: (props: TableCacheRenderProps) => React.ReactElement<any, any> | null
}
const TableCache: React.FC<TableCacheProps> = ({
  type, children, projectId,
}) => {
  const cached = localPageCacheStore.getItem(`columns**${type}**${projectId}`);
  const { isLoading, data } = useTableColumns({ type, projectId });
  const updateCache = usePersistFn(({ pagination, visibleColumns }) => {
    localPageCacheStore.setItem(`columns**${type}**${projectId}`, {
      pagination,
      visibleColumns,
    });
  });
  if (isLoading) {
    return <Loading loading />;
  }
  return children({
    cached: {
      listLayoutColumns: data?.listLayoutColumnRelVOS,
      ...cached,
    },
    updateCache,
  });
};

export default TableCache;
