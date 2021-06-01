import React from 'react';
import _ from 'lodash';
import classNames from 'classnames';
import { observer } from 'mobx-react-lite';
import { Draggable } from 'react-beautiful-dnd';
import IssueStore from '../../stores/IssueStore';

function getStyle(style, snapshot) {
  if (!snapshot.isDropAnimating) {
    return style;
  }
  return {
    ...style,
    // cannot be 0, but make it super tiny
    transitionDuration: '0.001s',
  };
}
export default observer((props) => {
  const draggingTableItems = IssueStore.getDraggingTableItems;
  const {
    issue, index, handleClickIssue,
  } = props;
  const { clickIssue } = IssueStore;
  const classes = classNames('c7ntest-border', 'c7ntest-table-item', { selected: clickIssue.caseId === issue.caseId });
  return (
    <Draggable key={issue.caseId} draggableId={issue.caseId} index={index}>
      {(provided, snapshotinner) => (
        <tr
          ref={provided.innerRef}
          {...provided.draggableProps}
          {...provided.dragHandleProps}
          style={{
            background: !snapshotinner.isDragging && issue.typeCode !== 'issue_auto_test' && _.find(draggingTableItems, { caseId: issue.caseId }) && 'rgb(250, 250, 252)',
            position: 'relative',
            ...getStyle(provided.draggableProps.style, snapshotinner),
          }}
          onClick={(e) => { handleClickIssue(issue, index, e); }}
          className={classes}
        >
          {snapshotinner.isDragging
            && (
              <div style={{
                position: 'absolute',
                width: 20,
                height: 20,
                background: 'red',
                textAlign: 'center',
                color: 'white',
                borderRadius: '50%',
                top: 0,
                left: 0,
              }}
              >
                {draggingTableItems.length}
              </div>
            )}
          {snapshotinner.isDragging
            && (
              <div className="IssueTable-drag-prompt">
                <div>
                  复制或移动测试用例至文件夹
                </div>
                <div> 按下ctrl/command复制</div>
                <div
                  ref={props.instanceRef}
                >
                  <div>
                    当前状态：
                    <span style={{ fontWeight: 500 }}>移动</span>
                  </div>
                </div>
              </div>
            )}
          {props.children}
        </tr>
      )}
    </Draggable>
  );
});
