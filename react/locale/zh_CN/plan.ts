import { localeAppendPrefixObjectKey } from '@choerodon/agile/lib/utils/locale';

const locale = {
  route: '计划',
  todo: '未开始',
  doing: '进行中',
  done: '已完成',
  create: '创建计划',
  'current.no.plan': '当前项目下无{status}的计划',
  'start.manual': '开始手工测试',
  edit: '修改计划',
  copy: '复制此计划',
  'time.rank': '时间排序',
  'time.default.rank': '默认排序',
  'drag.rank': '调整计划结构',
  'batch.assign': '批量指派',
  import: '导入用例',
  name: '计划名称',
  'complete.progress': '已测: {done}/{total}',
  'start.end.date': '起止时间',
  responsible: '负责人',
  'belong.sprint': '所属冲刺',
  'belong.version': '所属版本',
  description: '描述',
  case: '测试用例',
  calendar: '计划日历',
  'only.me': '只看我的',
  'plan.executor': '计划执行人',
  'execute.name': '执行名称',
  'actual.executor': '实际执行人',
  'update.date': '更新时间',
  'execute.status': '状态',
  remove: '删除',
  report: '计划报告',
  completed: '完成计划',
} as const;
const exportPlan = localeAppendPrefixObjectKey({ intlPrefix: 'plan' as const, servicePrefix: 'test', intlObject: locale });
type ILocalePlanType = {
  ['test.plan']: Array<keyof typeof locale>[number]
}
export { exportPlan };
export type { ILocalePlanType };
