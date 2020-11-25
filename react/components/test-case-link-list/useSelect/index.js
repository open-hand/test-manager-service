/* eslint-disable */
import 'choerodon-ui/pro/lib/data-set/style';
import _DataSet from 'choerodon-ui/pro/lib/data-set';
import 'choerodon-ui/pro/lib/button/style';
import _Button from 'choerodon-ui/pro/lib/button';

import React, {
  useState, useMemo, useEffect, useRef, useImperativeHandle, useCallback, 
} from 'react';
import { unstable_batchedUpdates as batchedUpdates } from 'react-dom';
import { omit, debounce } from 'lodash';
import { usePersistFn } from 'ahooks';
import FragmentForSearch from './FragmentForSearch';
import styles from './index.less?modules';

function _toConsumableArray(arr) { return _arrayWithoutHoles(arr) || _iterableToArray(arr) || _unsupportedIterableToArray(arr) || _nonIterableSpread(); }

function _nonIterableSpread() { throw new TypeError('Invalid attempt to spread non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.'); }

function _iterableToArray(iter) { if (typeof Symbol !== 'undefined' && Symbol.iterator in Object(iter)) return Array.from(iter); }

function _arrayWithoutHoles(arr) { if (Array.isArray(arr)) return _arrayLikeToArray(arr); }

function _slicedToArray(arr, i) { return _arrayWithHoles(arr) || _iterableToArrayLimit(arr, i) || _unsupportedIterableToArray(arr, i) || _nonIterableRest(); }

function _nonIterableRest() { throw new TypeError('Invalid attempt to destructure non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.'); }

function _unsupportedIterableToArray(o, minLen) { if (!o) return; if (typeof o === 'string') return _arrayLikeToArray(o, minLen); let n = Object.prototype.toString.call(o).slice(8, -1); if (n === 'Object' && o.constructor) n = o.constructor.name; if (n === 'Map' || n === 'Set') return Array.from(o); if (n === 'Arguments' || /^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n)) return _arrayLikeToArray(o, minLen); }

function _arrayLikeToArray(arr, len) { if (len == null || len > arr.length) len = arr.length; for (var i = 0, arr2 = new Array(len); i < len; i++) { arr2[i] = arr[i]; } return arr2; }

function _iterableToArrayLimit(arr, i) { if (typeof Symbol === 'undefined' || !(Symbol.iterator in Object(arr))) return; const _arr = []; let _n = true; let _d = false; let _e; try { for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) { _arr.push(_s.value); if (i && _arr.length === i) break; } } catch (err) { _d = true; _e = err; } finally { try { if (!_n && _i.return != null) _i.return(); } finally { if (_d) throw _e; } } return _arr; }

function _arrayWithHoles(arr) { if (Array.isArray(arr)) return arr; }

const __awaiter = this && this.__awaiter || function (thisArg, _arguments, P, generator) {
  function adopt(value) {
    return value instanceof P ? value : new P((resolve) => {
      resolve(value);
    });
  }

  return new (P || (P = Promise))((resolve, reject) => {
    function fulfilled(value) {
      try {
        step(generator.next(value));
      } catch (e) {
        reject(e);
      }
    }

    function rejected(value) {
      try {
        step(generator.throw(value));
      } catch (e) {
        reject(e);
      }
    }

    function step(result) {
      result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected);
    }

    step((generator = generator.apply(thisArg, _arguments || [])).next());
  });
};

function applyMiddleWares(data, middleWares) {
  return middleWares.reduce((preData, middleWare) => middleWare(preData), data);
}

function noop(data) {
  return data;
}
/**
 * 从对象中获取值，可以传一个key或路径，比如 date.str
 * @param object
 * @param path
 */

function getValueByPath(object, path) {
  const paths = path.split('.');
  let result = object;

  while (paths.length > 0) {
    const key = paths.shift();

    if (Object.prototype.hasOwnProperty.call(object, key)) {
      // @ts-ignore
      result = result[key];
    } else {
      return undefined;
    }
  }

  return result;
}

export default function useSelect(config, ref) {
  const _this = this;

  const _useState = useState([]);
  const _useState2 = _slicedToArray(_useState, 2);
  const data = _useState2[0];
  const setData = _useState2[1];

  const _useState3 = useState(1);
  const _useState4 = _slicedToArray(_useState3, 2);
  const currentPage = _useState4[0];
  const setPage = _useState4[1];

  const _useState5 = useState(false);
  const _useState6 = _slicedToArray(_useState5, 2);
  const canLoadMore = _useState6[0];
  const setCanLoadMore = _useState6[1];

  const textRef = useRef('');
  const dataSetRef = useRef();
  const cacheRef = useRef(new Map());
  const defaultRender = useCallback((item) => getValueByPath(item, config.textField), [config.textField]);
  const firstRef = useRef(true);
  const _config$textField = config.textField;
  const textField = _config$textField === void 0 ? 'meaning' : _config$textField;
  const _config$valueField = config.valueField;
  const valueField = _config$valueField === void 0 ? 'value' : _config$valueField;
  const _config$optionRendere = config.optionRenderer;
  const optionRenderer = _config$optionRendere === void 0 ? defaultRender : _config$optionRendere;
  const requestFn = config.request;
  const _config$middleWare = config.middleWare;
  const middleWare = _config$middleWare === void 0 ? noop : _config$middleWare;
  const afterLoadFn = config.afterLoad;
  const _config$paging = config.paging;
  const paging = _config$paging === void 0 ? true : _config$paging;
  const { props } = config;
  const request = usePersistFn(requestFn);
  const afterLoad = usePersistFn(afterLoadFn || noop);
  const renderer = useCallback((_ref) => {
    const { value } = _ref;
    const { maxTagTextLength } = _ref;

    let _a;

    const item = (_a = cacheRef.current) === null || _a === void 0 ? void 0 : _a.get(value);

    if (item) {
      const result = optionRenderer(item);
      return maxTagTextLength && typeof result === 'string' && result.length > maxTagTextLength ? ''.concat(result.slice(0, maxTagTextLength), '...') : result;
    }

    return null;
  }, [optionRenderer]); // 不分页时，本地搜索

  const localSearch = !paging;
  const loadData = useCallback(function () {
    const _ref2 = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};
    const _ref2$filter = _ref2.filter;
    const filter = _ref2$filter === void 0 ? textRef.current : _ref2$filter;
    const _ref2$page = _ref2.page;
    const page = _ref2$page === void 0 ? 1 : _ref2$page;

    return __awaiter(_this, void 0, void 0, /* #__PURE__ */regeneratorRuntime.mark(function _callee() {
      let res;
      return regeneratorRuntime.wrap((_context) => {
        while (1) {
          switch (_context.prev = _context.next) {
            case 0:
              _context.next = 2;
              return request({
                filter,
                page,
              });

            case 2:
              res = _context.sent;
              batchedUpdates(() => {
                if (paging) {
                  const { list } = res;
                  const { hasNextPage } = res;

                  if (afterLoad && firstRef.current) {
                    afterLoad(list);
                    firstRef.current = false;
                  }

                  setData((d) => (page > 1 ? d.concat(list) : list));
                  setPage(page);
                  setCanLoadMore(hasNextPage);
                } else {
                  if (afterLoad && firstRef.current) {
                    afterLoad(res);
                    firstRef.current = false;
                  }

                  setData(res);
                }
              }); // TODO: 更好的实现

            case 4:
            case 'end':
              return _context.stop();
          }
        }
      }, _callee);
    }));
  }, [afterLoad, paging, request]);
  const searchData = useMemo(() => debounce((filter) => {
    loadData({
      filter,
    });
  }, 500), [loadData]);
  useEffect(() => {
    loadData({
      filter: '',
    });
  }, [loadData]);
  useImperativeHandle(ref, () => ({
    refresh: loadData,
  }));
  const handleLoadMore = useCallback(() => {
    loadData({
      page: currentPage + 1,
    });
  }, [currentPage, loadData]);
  const handleInput = useCallback((e) => {
    const { value } = e.target;
    textRef.current = value;

    if (!localSearch) {
      searchData(value);
    }
  }, [localSearch, searchData]);
  const filterOptions = useCallback((_ref3) => {
    const { record } = _ref3;
    const { text } = _ref3;
    // @ts-ignore
    const meaning = optionRenderer === defaultRender ? getValueByPath(record.data, textField) : optionRenderer(record.data);

    if (!meaning) {
      return true;
    }

    let name = ''; // 一般情况，option的children是一个字符串

    if (typeof meaning === 'string') {
      name = meaning;
    } else if (/* #__PURE__ */React.isValidElement(meaning)) {
      // 其他情况, children是一个元素,那么约定这个元素上的name属性进行搜索
      // @ts-ignore
      // eslint-disable-next-line prefer-destructuring
      name = meaning.props.name;
    } else {
      return true;
    }

    return name.toLowerCase().indexOf(text.toLowerCase()) >= 0;
  }, [defaultRender, optionRenderer, textField]);
  const optionData = useMemo(() => (applyMiddleWares(data, [middleWare]) || []).map((item) => ({
    ...item,
    meaning: item[textField],
    value: item[valueField], 
  })), [data, middleWare, textField, valueField]);
  const finalData = useMemo(() => (canLoadMore ? [].concat(_toConsumableArray(optionData), [{
    loadMoreButton: true,
  }]) : optionData), [canLoadMore, optionData]);
  const loadMoreButton = useMemo(() => 
  /* #__PURE__ */React.createElement(_Button, {
      onClick: function onClick(e) {
        e.stopPropagation();
        handleLoadMore();
      },
      style: {
        margin: '-4px -12px',
        width: 'calc(100% + 24px)',
      },
    }, '\u52A0\u8F7D\u66F4\u591A'),
  [handleLoadMore]);
  const options = useMemo(() => {
    if (!dataSetRef.current) {
      dataSetRef.current = new _DataSet({
        data: finalData,
        paging: false,
      });
    } else {
      dataSetRef.current.loadData(finalData);
    }

    optionData.forEach((item) => {
      let _a;

      (_a = cacheRef.current) === null || _a === void 0 ? void 0 : _a.set(item[valueField], item);
    });
    return dataSetRef.current;
  }, [finalData, optionData, valueField]);

  const renderOption = function renderOption(_ref4) {
    const { record } = _ref4;

    if (!record) {
      return null;
    }

    if (record.get('loadMoreButton') === true) {
      return loadMoreButton;
    }

    return optionRenderer(record.toData());
  };

  const selectProps = {
    searchable: true,
    onInput: handleInput,
    onClear: function onClear() {
      textRef.current = '';
      searchData('');
    },
    // 弹出时自动请求
    onPopupHiddenChange: function onPopupHiddenChange(hidden) {
      if (hidden === false && textRef.current !== '' && paging) {
        textRef.current = '';
        searchData('');
      }
    },
    searchMatcher: paging ? function () {
      return true;
    } : filterOptions,
    valueField,
    // 这里不传递textField，因为由useSelect来渲染
    textField,
    options,
    // @ts-ignore
    optionRenderer: renderOption,
    // TODO: 考虑如何获取record，来渲染，例如用户
    renderer,
    // renderer: renderer ? ({
    //   // @ts-ignore
    //   value, text, name, record, dataSet,
    // }) => {
    //   return (record ? renderer() : null);
    // } : undefined,
    // @ts-ignore
    onOption: function onOption(_ref5) {
      const { record } = _ref5;

      if (record.get('loadMoreButton') === true) {
        return {
          className: styles.load_more,
          disabled: true,
        };
      }

      return {};
    },
    ...omit(props, 'renderer', 'optionRenderer'), 
  };
  return selectProps;
}
export { FragmentForSearch };
