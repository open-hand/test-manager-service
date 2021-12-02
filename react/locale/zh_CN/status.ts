import { localeAppendPrefixObjectKey } from '@choerodon/agile/lib/utils/locale';

const locale = {
  route: '测试',
  create: '创建状态',
  name: '状态名称',
  execute: '测试执行状态',
  step: '测试步骤状态',
} as const;
const exportStatus = localeAppendPrefixObjectKey({ intlPrefix: 'status' as const, servicePrefix: 'test', intlObject: locale });
type ILocaleStatusType = {
  ['test.status']: Array<keyof typeof locale>[number]
}
export { exportStatus };
export type { ILocaleStatusType };
