import { localeAppendPrefixObjectKey } from '@choerodon/agile/lib/utils/locale';

const locale = {
  route: '用例库',
  function: '功能测试',
  api: 'API测试',
  'batch.remove': '批量移除用例',
  import: '导入用例',
  export: '导出用例',
  'create.root.dir': '创建一级目录',
  create: '创建用例',
  name: '用例名称',
  num: '用例编号',
  'copy.case': '复制用例',
  'empty.dir': '暂无目录',
  'empty.dir.description': '当前项目下暂无目录，请创建',
} as const;
const exportCaseLibrary = localeAppendPrefixObjectKey({ intlPrefix: 'caseLibrary' as const, servicePrefix: 'test', intlObject: locale });
type ILocaleCaseLibraryType = {
  ['test.caseLibrary']: Array<keyof typeof locale>[number]
}
export { exportCaseLibrary };
export type { ILocaleCaseLibraryType };
