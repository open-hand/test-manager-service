import React, { useState } from 'react';
import { TextField } from 'choerodon-ui/pro';
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
function PromptInput({ maxLength, onInput, ...restProps }) {
  const [strNum, setStrNum] = useState(0);
  const handleInput = (e) => {
    setStrNum(e.target.value.length);
    if (onInput) {
      onInput(e);
    }
  };
  return maxLength ? (
    <div className="test-prompt-text-field">
      <TextField
        onInput={handleInput}
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
      {...restProps}
    />
  );
}
PromptInput.propTypes = propTypes;
export default PromptInput;
