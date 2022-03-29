import { stores } from '@choerodon/boot';

const { AppState } = stores;
export type ICategoryCode =
  // 需求管理
  'N_REQUIREMENT' |
  // devops
  'N_DEVOPS' |
  // 测试管理
  'N_TEST' |
  // 敏捷项目
  'N_AGILE' |
  // 子项目
  'N_PROGRAM_PROJECT' |
  // 瀑布管理
  'N_WATERFALL' |
  // 瀑布管理开启冲刺
  'N_WATERFALL_AGILE' |
  // 项目群
  'N_PROGRAM' |
  // 运维项目
  'N_OPERATIONS'
const useCategoryCodes = (): ICategoryCode[] => (
  AppState.menuType.categories
    ? AppState.menuType.categories.map((c: any) => c.code)
    : []);

export default useCategoryCodes;
