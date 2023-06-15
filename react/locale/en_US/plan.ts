import { localeAppendPrefixObjectKey } from '@choerodon/agile/lib/utils/locale';

const locale = {
  route: 'Plan',
  todo: 'Pending',
  doing: 'Ongoing',
  done: 'Done',
  create: 'Create Plan',
  'current.no.plan': 'There are no {status} plans',
  'start.manual': 'Start manual testing',
  edit: 'Modify Plan',
  copy: 'Copy',
  'time.rank': 'Sort by Time',
  'time.default.rank': 'Default Sort',
  'drag.rank': 'Adjust Plan',
  'batch.assign': 'Batch Assigned',
  import: 'Import Test Case',
  name: 'Plan Name',
  'complete.progress': 'Done: {done}/{total}',
  'start.end.date': 'Start-Stop Time',
  responsible: 'Person in Charge',
  'belong.sprint': 'Sprint',
  'belong.version': 'Version',
  description: 'Description',
  case: 'Test Case',
  calendar: 'Calendar',
  'only.me': 'Mine',
  'plan.executor': 'Plan Executor',
  'execute.name': 'Test Case Name',
  'actual.executor': 'Actual Executor',
  'update.date': 'Update Time',
  'execute.status': 'Status',
  remove: 'Remove',
  report: 'Report',
  completed: 'Finish Plan',
} as const;
const exportPlan = localeAppendPrefixObjectKey({ intlPrefix: 'plan' as const, servicePrefix: 'test', intlObject: locale });
type ILocalePlanType = {
  ['test.plan']: Array<keyof typeof locale>[number]
}
export { exportPlan };
export type { ILocalePlanType };
