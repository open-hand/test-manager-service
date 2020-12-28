/* eslint-disable react/require-default-props */
import React, {
  useCallback, useEffect, useLayoutEffect, useRef, useState,
} from 'react';
import { TextField } from 'choerodon-ui/pro';
import { observer, useObservable } from 'mobx-react-lite';
import PropTypes from 'prop-types';
import './PromptInput.less';

const propTypes = {
  maxLength: PropTypes.number,
  onInput: PropTypes.func,
};
/**
 * 带有字符提示的TextField输入框
 * @param {*}
 */
function PromptInput({
  maxLength, onInput, name, ...restProps
}) {
  const [strNum, setStrNum] = useState(0);
  const [count, setCount] = useState([]);
  const dataSetRef = useRef({});
  const handleInput = (e) => {
    setStrNum(e.target.value.length);
    if (onInput) {
      onInput(e);
    }
  };
  const initPromptValue = () => {
    if (!dataSetRef.current || !name || count.length > 5) {
      Array.isArray(count) && count.forEach((item) => clearTimeout(item));
      return;
    }
    if (!dataSetRef.current.current) {
      const timeOutId = setTimeout(initPromptValue, 300);
      setCount((old) => {
        const temp = [];
        temp.push(...(old || []), timeOutId);
      });
      return;
    }
    const initValue = dataSetRef.current.current.get(name);
    initValue && setStrNum(String(initValue).length);
    dataSetRef.current = false;
    count.forEach((item) => clearTimeout(item));
    // console.log('initValue', initValue);
  };
  const handleSaveRef = useCallback((ref) => {
    if (ref && ref.dataSet) {
      dataSetRef.current = ref.dataSet;
      initPromptValue();
    }
  }, [name]);
  return maxLength ? (
    <div className="test-prompt-text-field">
      <TextField
        ref={handleSaveRef}
        onInput={handleInput}
        name={name}
        maxLength={maxLength}
        {...restProps}
      />
      <div className="test-prompt-text-field-info">
        {`${strNum}/${maxLength}`}
      </div>

    </div>
  ) : (
    <TextField
      onInput={handleInput}
      name={name}
      {...restProps}
    />
  );
}
PromptInput.propTypes = propTypes;
export default observer(PromptInput);
