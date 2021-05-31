import React, { Component } from 'react';
import { Tooltip } from 'choerodon-ui';

import moment from 'moment';
import './CalendarBackItem.less';

class CalendarBackItem extends Component {
  shouldComponentUpdate(nextProps, nextState) {
    return !this.props.date.isSame(nextProps.date);
  }

  render() {
    const { date } = this.props;
    return (
      // 周末字体颜色不同
      <div
        className="CalendarBackItem"
        style={{ color: moment(date).day() === 6 || moment(date).day() === 0 ? '--primary-color' : 'var(--text-color)' }}
      >
        <div className="CalendarBackItem-content">
          <Tooltip title={moment(date).format('LL')} placement="topLeft">
            <div>
              {moment(date).format('dddd')}
            </div>
            <div style={{ marginTop: 5 }}>
              {moment(date).format('MMMDo')}
            </div>
          </Tooltip>
        </div>
      </div>
    );
  }
}

CalendarBackItem.propTypes = {

};

export default CalendarBackItem;
