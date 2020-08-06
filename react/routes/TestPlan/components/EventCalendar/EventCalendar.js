/* eslint-disable react/state-in-constructor */
import React, { Component } from 'react';
import isEqual from 'react-fast-compare';
import { observer } from 'mobx-react';
import { toJS } from 'mobx';
import Moment from 'moment';
import { extendMoment } from 'moment-range';
import { DatePicker, Spin } from 'choerodon-ui';
import './EventCalendar.less';
import { Button } from 'choerodon-ui/pro/lib';
import CalendarBackItem from './CalendarBackItem';
import EventItem from './EventItem';
import Store from '../../stores';

const { RangePicker } = DatePicker;
const moment = extendMoment(Moment);

@observer
class EventCalendar extends Component {
  constructor(props) {
    super(props);
    const baseDate = moment();
    const endDate = moment();
    this.currentDate = baseDate;
    this.state = {
      baseDate, // 显示的开始时间
      endDate, // 显示的结束时间
      // eslint-disable-next-line react/no-unused-state
      mode: 'month',
      // eslint-disable-next-line react/no-unused-state
      width: 'auto',
    };
  }

  static getDerivedStateFromProps(nextProps, state) {
    if (JSON.stringify(nextProps.times) !== JSON.stringify(state.times)) {
      let baseDate = moment();
      let endDate = moment();
      const { times } = nextProps;
      if (times && times.length > 0) {
        baseDate = times[0].start ? moment(times[0].start).startOf('day') : moment();
        endDate = times[0].end ? moment(times[0].end).startOf('day') : moment();
      }
      return {
        times,
        baseDate, // 显示的开始时间
        endDate, // 显示的结束时间
      };
    } else {
      return null;
    }
  }

  componentDidMount() {
    this.setSingleWidth();
  }

  componentDidUpdate(prevProps, prevState) {
    this.setSingleWidth();
  }


  setSingleWidth = () => {
    this.singleWidth = document.getElementsByClassName('CalendarBackItem')[0] ? document.getElementsByClassName('CalendarBackItem')[0].offsetWidth : 0;
  }

  calculateTime = () => {
    const { baseDate, endDate } = this.state;
    const start = moment(baseDate).startOf('day');
    const end = moment(endDate).endOf('day');
    return { start, end };
  }


  saveRef = name => (ref) => {
    this[name] = ref;
  }

  /**
   * 鼠标按下，区域拖动
   *
   *
   */
  handleMouseDown = (e) => {
    e.stopPropagation();
    e.preventDefault();
    this.scroller.style.cursor = 'grabbing';
    this.initScrollPosition = {
      x: e.clientX,
      y: e.clientY,
      left: this.scroller.scrollLeft,
      top: this.scroller.scrollTop,
    };
    document.addEventListener('mouseup', this.handleMouseUp);
    document.addEventListener('mousemove', this.handleMouseMove);
  }

  handleMouseMove = (e) => {
    // console.log('move');
    e.stopPropagation();
    e.preventDefault();
    if (this.initScrollPosition) {
      const posX = e.clientX - this.initScrollPosition.x;
      const posY = e.clientY - this.initScrollPosition.y;
      this.scroller.scrollLeft = this.initScrollPosition.left - posX;
      this.scroller.scrollTop = this.initScrollPosition.top - posY;
    }
  }

  handleMouseUp = (e) => {
    this.scroller.style.cursor = 'grab';
    this.setCurrentDate();
    document.removeEventListener('mousemove', this.handleMouseMove);
    document.removeEventListener('mouseup', this.handleMouseUp);
  }

  /**
   * 当滚动条滚动时，更新左侧当前日期，以此为基准进行上一个月，下一个月切换
   *
   *
   */
  setCurrentDate = () => {
    const { scrollLeft } = this.scroller;
    const { baseDate } = this.state;
    const leapDays = Math.floor(scrollLeft / this.singleWidth);
    const currentDate = moment(baseDate).add(leapDays, 'days');
    this.currentDate = currentDate;
  }

  /**
   * 时间范围改变
   *
   * @memberof EventCalendar
   */
  handleRangeChange = (range) => {
    this.setState({
      baseDate: range[0],
      endDate: range[1],
    });
  }

  /**
   * 左右切换日期
   *
   * @paramter mode "pre"前一个月 "next" 后一个月
   */
  skipTo = (mode) => {
    const { baseDate, endDate } = this.state;
    // 计算目标时间
    const targetDate = mode === 'pre'
      ? moment(this.currentDate).subtract(1, 'months')
      : moment(this.currentDate).add(1, 'months');
    // 当前时间范围
    const range = moment.range(baseDate, endDate);
    // 如果目标时间在当前范围
    if (range.contains(targetDate)) {
      const skipRange = moment.range(baseDate, targetDate);
      const days = skipRange.diff('days');
      // 目标位置dom
      const targetDOM = document.getElementsByClassName('CalendarBackItem')[days]; // findDOMNode(this[`item_${days}`]);// eslint-disable-line react/no-find-dom-node
      if (targetDOM) {
        const left = targetDOM.offsetLeft;
        this.scroller.scrollLeft = left;
        this.currentDate = targetDate;
      }
    } else {
      // 设置滚动到最右或最左侧，并且设置当前时间
      this.scroller.scrollLeft = mode === 'pre' ? 0 : this.BackItems.scrollWidth;
      this.currentDate = mode === 'pre' ? baseDate : endDate;
    }
  }

  render() {
    const {
      showMode, times, calendarLoading, handleRankByDate,
    } = this.props;
    const { context: { testPlanStore } } = this.props;
    const { start, end } = this.calculateTime();
    const range = moment.range(start, end);
    const timeArray = Array.from(range.by('day'));
    const dateFormat = 'YYYY/MM/DD';
    return (
      <Spin spinning={calendarLoading}>
        <div className="c7ntest-EventCalendar" style={{ height: showMode === 'multi' ? '100%' : '162px' }}>
          {/* 头部 */}
          <div className="c7ntest-EventCalendar-header" style={{ marginTop: '-50px', flexDirection: 'row-reverse', display: testPlanStore.mainActiveTab === 'testPlanTable' ? 'none' : 'flex' }}>
            <div className="c7ntest-EventCalendar-header-title" style={{ zIndex: 100 }}>
              <div className="c7ntest-EventCalendar-header-skip">
                <Button
                  style={{ fontSize: 13 }}
                  color="blue"
                  icon="application_allocation"
                  onClick={handleRankByDate}
                >
                  时间排序
                </Button>
                <RangePicker
                  // placement="bottomRight"
                  onChange={this.handleRangeChange}
                  value={[start, end]}
                  format={dateFormat}
                  allowClear={false}
                />
              </div>
            </div>
            <div className="c7ntest-flex-space" />
          </div>
          <div role="none" className="c7ntest-EventCalendar-content" ref={this.saveRef('scroller')} onMouseDown={this.handleMouseDown}>
            <div style={{
              display: 'table', minWidth: '100%', minHeight: '100%', position: 'relative',
            }}
            >
              <div className="c7ntest-EventCalendar-fixed-header">
                {timeArray.map(m => (<CalendarBackItem date={m} />))}
              </div>
              <div className="c7ntest-EventCalendar-eventContainer">
                <div className="c7ntest-EventCalendar-BackItems" ref={this.saveRef('BackItems')}>
                  {
                    timeArray.map(() => <div className="c7ntest-EventCalendar-BackItems-item" />)
                  }
                </div>
                {times.map(event => (
                  <EventItem
                    key={event.key}
                    itemRange={moment.range(event.start, event.end)}
                    data={event}
                    range={range}
                  />
                ))}
              </div>
            </div>
          </div>
        </div>
      </Spin>
    );
  }
}

export default props => (
  <Store.Consumer>
    {context => (
      <EventCalendar {...props} context={context} />
    )}
  </Store.Consumer>
);
