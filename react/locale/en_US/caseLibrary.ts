import { localeAppendPrefixObjectKey } from '@choerodon/agile/lib/utils/locale';

const locale = {
  route: 'Test Case Library',
  function: 'Functional Test',
  api: 'API Test',
  'batch.remove': 'Batch Remove',
  import: 'Import',
  export: 'Export',
  'create.root.dir': 'Make First-Level Directory',
  create: 'Create Test Case',
  name: 'Test Case Name',
  num: 'Test Case Number',
  'copy.case': 'Copy',
  'empty.dir': 'No Directory',
  'empty.dir.description': 'There are no directories at present, please create',
} as const;
const exportCaseLibrary = localeAppendPrefixObjectKey({ intlPrefix: 'caseLibrary' as const, servicePrefix: 'test', intlObject: locale });
type ILocaleStoryMapType = {
  ['test.caseLibrary']: Array<keyof typeof locale>[number]
}
export { exportCaseLibrary };
export type { ILocaleStoryMapType };
