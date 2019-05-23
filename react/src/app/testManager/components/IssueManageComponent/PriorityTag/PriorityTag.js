import React, { memo } from 'react';
import isEqual from 'react-fast-compare';
import { color2rgba } from '../../../common/utils';

const PriorityTag = ({ style, priority }) => {
  const { colour, name } = priority;
  return (
    <div
      style={{
        ...style,
        whiteSpace: 'nowrap',
        overflow: 'hidden',
        textOverflow: 'ellipsis',
        maxWidth: 100,
        backgroundColor: color2rgba(colour, 0.18),
        color: colour,
        borderRadius: '2px',
        padding: '0 8px',
        display: 'inline-block',
        lineHeight: '20px',
        fontSize: '13px',
        textAlign: 'center',
      }}
    >
      {name || ''}
    </div>
  );
};
export default memo(PriorityTag, isEqual);
