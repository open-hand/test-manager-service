import React from 'react';
import Loading from '../Loading';
import './Empty.less';

const Empty = ({
  style,
  loading,
  pic,
  title,
  description,
  extra,
}) => (
  loading ? <Loading /> : (
    <div
      className="c7nagile-Empty"
      style={style}
    >
      <div
        className="c7nagile-Empty-content"        
      >
        <div className="c7nagile-Empty-imgWrap">
          <img src={pic} alt="" className="c7nagile-Empty-imgWrap-img" />
        </div>
        <div
          className="c7nagile-Empty-textWrap"
        >
          <h1 className="c7nagile-Empty-title">
            {title || ''}
          </h1>
          <div className="c7nagile-Empty-description">
            {description || ''}
          </div>
          <div style={{ marginTop: 10 }}>
            {extra}
          </div>
        </div>      
      </div>
    </div>
  )
);
export default Empty;
