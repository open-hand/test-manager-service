import React, { memo } from 'react';
import isEqual from 'react-fast-compare';
import { Icon } from 'choerodon-ui';
import './TypeTag.scss';

const TypeTag = ({
  type, style, showName,
}) => {
  const {
    colour: typeColor, name: typeName, typeCode, icon,
  } = type || {}; 
  return (
    <div className="c7ntest-typeTag" style={style}>
      <Icon
        style={{
          fontSize: '26px',
          color: typeColor || '#fab614',
        }}
        type={icon || 'help'}
      />     
      {
        showName && (
          <span className="name">{typeName || ''}</span>
        )
      }
    </div>
  );
};
export default memo(TypeTag, isEqual);
