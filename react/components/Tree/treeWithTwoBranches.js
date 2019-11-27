/*eslint-disable*/
export const treeWithTwoBranches = {
  rootId: '1',
  items: {
    1: {
      id: '1',
      children: ['1-1', '1-2'], // 一级目录
      hasChildren: true,
      isExpanded: true,
      isChildrenLoading: false,
      data: {
        title: 'root',
      },
    },
    '1-1': {
      id: '1-1',
      children: ['1-1-1', '1-1-2'],
      hasChildren: true,
      isExpanded: true,
      isChildrenLoading: false,
      data: {
        title: 'Choerodon',
      },
    },
    '1-2': {
      id: '1-2',
      children: ['1-2-1', '1-2-2'],
      hasChildren: true,
      isExpanded: true,
      isChildrenLoading: false,
      data: {
        title: 'Choerodon2',
      },
    },
    '1-1-1': {
      id: '1-1-1',
      children: [],
      hasChildren: false,
      isExpanded: false,
      isChildrenLoading: false,
      data: {
        title: 'Choerodon敏捷',
      },
    },
    '1-1-2': {
      id: '1-1-2',
      children: [],
      hasChildren: false,
      isExpanded: false,
      isChildrenLoading: false,
      data: {
        title: 'Choerodon测试',
      },
    },
    '1-2-1': {
      id: '1-2-1',
      children: [],
      hasChildren: false,
      isExpanded: false,
      isChildrenLoading: false,
      data: {
        title: 'Choerodon敏捷',
      },
    },
    '1-2-2': {
      id: '1-2-2',
      children: ['1-2-2-1'],
      hasChildren: true,
      isExpanded: false,
      isChildrenLoading: false,
      data: {
        title: 'Choerodon测试',
      },
    },
    '1-2-2-1': {
      id: '1-2-2-1',
      children: [],
      hasChildren: false,
      isExpanded: false,
      isChildrenLoading: false,
      data: {
        title: 'Choerodon测试子',
      },
    },
  },
};
