import React, { memo } from 'react';
import isEqual from 'react-fast-compare';
import { Popover } from 'choerodon-ui';
import './TestProgressLine.scss';

const TestProgressLine = ({
  progress, style, ...restProps
}) => {
  let total = 0;
  const content = [];
  const inner = [];
  progress.forEach((item) => { total += item.counts; });
  progress.forEach((item) => {
    const { statusName, counts, color } = item;
    content.push(
      <div key={color} className="c7ntest-between" style={{ width: 100 }}>
        <div>{statusName}</div>        
        <div>{counts}</div>
      </div>,
    );    
    const percentage = (item.counts / total) * 100;
    inner.push(<span className="c7ntest-process-line-fill-item" style={{ backgroundColor: item.color, width: `${percentage}%` }} />);
  });
  const renderLine = () => (
    <div className="c7ntest-process-line" style={style}>
      <span className="c7ntest-process-line-unfill" />
      <div className="c7ntest-process-line-fill-area">
        {inner}
      </div>
    </div>
  );
  return (
    progress.length > 0
      ? (
        <Popover
          content={<div>{content}</div>}
          title={null}
        >
          {renderLine()}
        </Popover>
      )
      : renderLine()
  );
};
export default memo(TestProgressLine, isEqual);
