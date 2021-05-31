import React, { memo } from 'react';
import './EmptyBlock.less';

const EmptyBlock = ({
  style,
  border,
  pic,
  title,
  des,
}) => (
  <div
    className="c7ntest-emptyBlock"
    style={style}
  >
    <div
      className="c7ntest-wrap"
      style={{
        border: border ? '1px dashed var(--text-color3)' : '',
      }}
    >
      <div className="c7ntest-imgWrap">
        <img src={pic} alt="" className="c7ntest-img" />
      </div>
      <div
        className="c7ntest-textWrap"
      >
        <h1 className="c7ntest-title">
          {title || ''}
        </h1>
        <div className="c7ntest-des">
          {des || ''}
        </div>
      </div>
    </div>
  </div>
);
export default memo(EmptyBlock);
