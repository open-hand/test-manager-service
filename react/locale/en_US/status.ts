import { localeAppendPrefixObjectKey } from '@choerodon/agile/lib/utils/locale';

const locale = {
  route: 'Test',
  create: 'Create',
  name: 'Name',
  execute: 'Test Execution Status',
  step: 'Test Step Status',
} as const;
const exportStatus = localeAppendPrefixObjectKey({ intlPrefix: 'status' as const, servicePrefix: 'test', intlObject: locale });
type ILocaleStatusType = {
  ['test.status']: Array<keyof typeof locale>[number]
}
export { exportStatus };
export type { ILocaleStatusType };
