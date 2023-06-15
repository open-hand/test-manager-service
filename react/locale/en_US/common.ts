import { localeAppendPrefixObjectKey } from '@choerodon/agile/lib/utils/locale';

const locale = {
  'custom.num': 'Custom Number',
  name: 'Name',
  color: 'Color',
  description: 'Description',
  priority: 'Priority',
  creator: 'Creator',
  'create.date': 'Creation Time',
  'update.user': 'Updater',
  'update.date': 'Update Time',
  rename: 'Rename',
  copy: 'Copy',
  move: 'Move',
  filter: 'Filter',
  status: 'Status',
  'search.placeholder': 'Search',
  'empty.data': 'No Data',
  'priority.hight': 'High',
  'priority.medium': 'Medium',
  'priority.low': 'Low',
} as const;
const exportCommon = localeAppendPrefixObjectKey({ intlPrefix: 'common' as const, servicePrefix: 'test', intlObject: locale });
type ILocaleCommonType = {
  ['test.common']: Array<keyof typeof locale>[number]
}
export { exportCommon };
export type { ILocaleCommonType };
