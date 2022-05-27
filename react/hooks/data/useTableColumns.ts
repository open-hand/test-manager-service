import {
  useQuery, UseQueryOptions, useMutation, useQueryClient,
} from 'react-query';
import useProjectKey from '@choerodon/agile/lib/hooks/data/useProjectKey';
import { IListLayout } from '@choerodon/agile/lib/api';
import { useCallback, useRef } from 'react';
import { cacheColumnApi } from '@/api/CacheColumn';
// import { cacheColumnApi, IListLayout } from '@/api';

export interface TableColumnConfig {
    /**
     *  这里和敏捷区分
     *  要加上`test`前缀
     */
    type: string
    projectId?: string
}
export function useTableColumnsKey(type: string, projectId?: string) {
  return useProjectKey({ key: ['testTableColumns', type], projectId });
}
export default function useTableColumns(config: TableColumnConfig, options?: UseQueryOptions<IListLayout>) {
  const key = useTableColumnsKey(config.type, config.projectId);
  return useQuery(key, () => cacheColumnApi.project(config.projectId).getDefault(config.type), {
    ...options,
  });
}
export const useUpdateColumnMutation = (type: string, projectId?: string) => {
  const queryClient = useQueryClient();
  const key = useTableColumnsKey(type, projectId);
  return useMutation(
    (listLayout: IListLayout) => cacheColumnApi.project(projectId).update(listLayout),
    {
      onSuccess: (data) => {
        // 用返回数据更新
        queryClient.setQueryData(key, data);
      },
      onSettled: () => {
        queryClient.invalidateQueries(key);
      },
    },
  );
};
/**
 *
 * @param type 保存的表格唯一type
 * @param columnCodes 当前表格所有codes
 * @param projectId
 * @returns
 */
export function useSimpleUpdateColumnCache(type: string, columnCodes: string[], projectId?: string) {
  const mutation = useUpdateColumnMutation(type, projectId);
  const columnCodesRef = useRef<string[]>();
  columnCodesRef.current = columnCodes;
  const updateColumnCache = useCallback((visibleColumns?: string[]) => {
    if (!columnCodesRef.current?.length) {
      return;
    }
    mutation.mutateAsync({
      applyType: type,
      listLayoutColumnRelVOS: columnCodesRef.current.map((columnCode, i) => ({
        columnCode,
        display: !visibleColumns?.length || visibleColumns.includes(columnCode),
        width: 0,
        sort: i,
      })),
    });
  }, [mutation, type]);
  return { updateColumnCache };
}
