import { localeAppendPrefixObjectKey } from '@choerodon/agile/lib/utils/locale';

const locale = {
  'custom.num': '自定义编号',
  name: '名称',
  color: '颜色',
  description: '描述',
  priority: '优先级',
  creator: '创建人',
  'create.date': '创建时间',
  'update.user': '更新人',
  'update.date': '更新时间',
  rename: '重命名',
  copy: '复制',
  move: '移动',
  filter: '过滤表',
  status: '状态',
  'search.placeholder': '请输入搜索条件',
  'empty.data': '暂无数据',
  'priority.hight': '高',
  'priority.medium': '中',
  'priority.low': '低',
} as const;
const exportCommon = localeAppendPrefixObjectKey({ intlPrefix: 'common' as const, servicePrefix: 'test', intlObject: locale });
type ILocaleCommonType = {
  ['test.common']: Array<keyof typeof locale>[number]
}
export { exportCommon };
export type { ILocaleCommonType };
