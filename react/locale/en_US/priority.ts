import { localeAppendPrefixObjectKey } from '@choerodon/agile/lib/utils/locale';

const locale = {
  route: 'Test Priority',
  create: 'Create',
} as const;
const exportPriority = localeAppendPrefixObjectKey({ intlPrefix: 'priority' as const, servicePrefix: 'test', intlObject: locale });
type ILocalePriorityType = {
  ['test.priority']: Array<keyof typeof locale>[number]
}
export { exportPriority };
export type { ILocalePriorityType };
