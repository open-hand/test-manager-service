import { localeAppendPrefixObjectKey } from '@choerodon/agile/lib/utils/locale';

const locale = {
  route: 'Automation',
  add: 'Add Test',
  'chose.app': 'Application',
  'run.status': 'Running Status',
  'run.status.wait': 'Waiting',
  'run.status.doing': 'Ongoing',
  'run.status.done': 'Completed',
  'run.status.failed': 'Failed',
  environment: 'Environment',
  'test.framework': 'Test Framework',
  'app.version': 'Application Version',
  result: 'Results',
  'result.todo': 'To Be Executed',
  'result.all.pass': 'All Passed',
  'result.part.pass': 'Partially passed',
  'result.no.pass': 'All failed',
  executive: 'Executor',
  during: 'Execution Time',
  'execute.time': 'Execution Start Time',
  'view.log': 'View Log',
  'retry.execute': 'Retry',
  'test.report': 'Test Report',
} as const;
const exportAutoTest = localeAppendPrefixObjectKey({ intlPrefix: 'autoTest' as const, servicePrefix: 'test', intlObject: locale });
type ILocaleAutoTestType = {
  ['test.autoTest']: Array<keyof typeof locale>[number]
}
export { exportAutoTest };
export type { ILocaleAutoTestType };
