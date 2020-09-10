import React, {
  useCallback, useReducer,
} from 'react';
import { Icon } from 'choerodon-ui';
import { text2Delta, delta2Html } from '../../../../../../common/utils';
import { RichTextShow } from '../../../../../../components';

function AutoHeightPrecondition({ data }) {
  const [preconditionState, setPreconditionState] = useReducer((state, action) => {
    switch (action.type) {
      case 'init':
        return ({
          loaded: true,
          iconVisible: action.desHeight > 52,
          desVisible: !(action.desHeight > 52),
          desHeight: action.desHeight,
        });
      case 'visible':
        return ({
          ...state,
          desVisible: !state.desVisible,
        });
      case 'destroy':
        return ({
          loaded: false,
          iconVisible: false,
          desVisible: true,
          desHeight: 0,
        });
      default:
        return state;
    }
  }, {
    loaded: false,
    iconVisible: false,
    desVisible: true,
    desHeight: 0,
  });
  const preconditionHook = useCallback(
    (node) => {
      if (node !== null) {
        setPreconditionState({ type: 'init', desHeight: node.getBoundingClientRect().height });
      }
    },
    [],
  );
  // 检查首行是否含有图片
  function checkImgInHeadLine(arr) {
    let isHasHeadLine = false;
    for (let index = 0; index < arr.length; index += 1) {
      const { insert } = arr[index];
      if (typeof (insert) === 'string' && (insert === '\n' || insert.includes('\n'))) {
        const str = insert.substring(insert.length - 1);
        break;
      }
      if (Object.prototype.hasOwnProperty.call(insert, 'image')) {
        isHasHeadLine = true;
        break;
      }
    }
    return isHasHeadLine;
  }
  function renderRichText(text, isEllipsis = false) {
    const textArr = [{ insert: '前置条件：' }];
    if (text && text !== '') {
      const tempText = text2Delta(text);
      if (Array.isArray(tempText)) {
        if (checkImgInHeadLine(tempText)) {
          textArr[0].insert = '前置条件：\n';
        }
        textArr.push(...tempText);
      } else {
        textArr.push({ insert: tempText });
      }
    }
    return (
      <div className={`c7n-test-execute-detail-card-title-description-head-content${isEllipsis ? '-ellipsis' : ''}`}>
        <RichTextShow data={delta2Html(JSON.stringify(textArr))} />
      </div>
    );
  }
  return (
    <div className="c7n-test-execute-detail-card-title-description-head" ref={data ? preconditionHook : () => { }}>

      {renderRichText(data, preconditionState.iconVisible && !preconditionState.desVisible)}
      <span className="c7n-test-execute-detail-card-title-description-head-more">
        {preconditionState.iconVisible && (
          <Icon
            style={{ cursor: 'pointer ' }}
            type={`expand_${preconditionState.desVisible ? 'less' : 'more'}`}
            onClick={() => { setPreconditionState({ type: 'visible' }); }}
          />
        )}
      </span>
    </div>
  );
}
export default AutoHeightPrecondition;
