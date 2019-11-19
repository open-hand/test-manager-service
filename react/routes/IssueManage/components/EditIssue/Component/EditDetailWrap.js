import React from 'react';
import './EditDetailWrap.less';

function EditDetailWrap() {
  const TitleWrap = (props) => {
    const {
      style, title, children, className,
    } = props;
    return (
      <div className={`c7ntest-title-wrapper ${className || ''}`} style={style}>
        <div className="c7ntest-title-left">
          {title}
        </div>
        {children}
      </div>
    );
  };

  const ContentWrap = (props) => {
    const {
      style, children, className,
    } = props;
    return (
      <div className={`c7ntest-content-wrapper ${className || ''}`} style={props.style}>

        {children}
      </div>
    );
  };

  const PropertyWrap = (props) => {
    const {
      style, children, className, valueStyle, label = '',
    } = props;
    return (
      <div className={`line-start mt-10 ${className || ''}`}>
        <div className="c7ntest-property-wrapper">
          <span className="c7ntest-property">
            {label}
          </span>
        </div>
        <div className="c7ntest-value-wrapper" style={valueStyle}>
          {children}
        </div>
      </div>
    ); 
  };
  return {
    TitleWrap,
    ContentWrap,
    PropertyWrap,
  };
}
export default EditDetailWrap();
