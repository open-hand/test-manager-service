/* eslint-disable */

import React, { Fragment } from 'react'; // 使用render自定义option的渲染时，套一个这个组件，用来搜索

const FragmentForSearch = function FragmentForSearch(_ref) {
  const { children } = _ref;
  return /* #__PURE__ */React.createElement(Fragment, null, children);
};

export default FragmentForSearch;
