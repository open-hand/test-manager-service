import { localeAppendPrefixObjectKey } from '@choerodon/agile/lib/utils/locale';

const locale = {
  route: '自动化',
  add: '添加测试',
  'chose.app': '选择应用',
  'run.status': '运行状态',
  'run.status.wait': '等待中',
  'run.status.doing': '进行中',
  'run.status.done': '完成',
  'run.status.failed': '失败',
  environment: '环境',
  'test.framework': '测试框架',
  'app.version': '应用版本',
  result: '测试结果',
  'result.todo': '未执行',
  'result.all.pass': '全部通过',
  'result.part.pass': '部分通过',
  'result.no.pass': '全未通过',
  executive: '执行方',
  during: '时长',
  'execute.time': '执行时间',
  'view.log': '查看日志',
  'retry.execute': '重新执行',
  'test.report': '测试报告',
} as const;
const exportAutoTest = localeAppendPrefixObjectKey({ intlPrefix: 'autoTest' as const, servicePrefix: 'test', intlObject: locale });
type ILocaleAutoTestType = {
  ['test.autoTest']: Array<keyof typeof locale>[number]
}
export { exportAutoTest };
export type { ILocaleAutoTestType };
