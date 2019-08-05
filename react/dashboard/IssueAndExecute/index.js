import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import { DashBoardNavBar, stores } from '@choerodon/boot';
import ReactEcharts from 'echarts-for-react';
import moment from 'moment';
import 'moment/locale/zh-cn';
import 'moment/locale/en-nz';
import _ from 'lodash';
import { getCycleRange, getCreateRange } from '../../api/summaryApi';
import { commonLink } from '../../common/utils';

const { AppState } = stores;
const langauge = AppState.currentLanguage;
if (langauge === 'zh_CN') {
  moment.locale('zh-cn');
}
export default class IssueAndExecute extends Component {
  state = {
    excuteList: [],
    createList: [],
  }

  componentDidMount() {
    this.getInfo();
  }

  getInfo = () => {
    const range = 7;
    Promise.all([
      getCycleRange(moment().format('YYYY-MM-DD'), range),
      getCreateRange(range)])
      .then(([excuteList, createList]) => {
        this.setState({         
          excuteList: this.listTransform(excuteList),        
          createList: this.createTransform(createList, range),        
        });        
      });
  }

  createTransform = (source, range) => Array(Number(range)).fill(0).map((item, i) => {
    const time = moment().subtract(range - i - 1, 'days').format('YYYY-MM-DD');
    if (_.find(source, { creationDay: time })) {
      const { creationDay, issueCount } = _.find(source, { creationDay: time });
      return {
        time: moment(creationDay).format('D/MMMM'),
        value: issueCount,
      };
    } else {
      return {
        time: moment().subtract(range - i - 1, 'days').format('D/MMMM'),
        value: 0,
      };
    }
  });

  listTransform = list => list.map((item, i) => ({
    time: moment().subtract(list.length - i - 1, 'days').format('D/MMMM'),
    value: item,
  }))

  getOption = () => ({
    // title: {
    //   text: '折线图堆叠',
    // },
    tooltip: {
      trigger: 'axis',
    },
    grid: {
      top: '13%',
      left: 38,
      right: '8%',
      bottom: '23%',
      // containLabel: true,
    },
    legend: {
      data: ['创建数', '执行数'],
      x: 'right',
    },
    xAxis: [{
      type: 'category',
      // name: '日期',
      // nameGap: 28,
      nameTextStyle: {
        color: 'black',
      },
      boundaryGap: false,
      axisLine: {
        lineStyle: {
          color: '#EEEEEE',
          // width: 8, // 这里是为了突出显示加上的
        },
      },
      axisLabel: {
        show: true,
        textStyle: {
          color: 'rgba(0,0,0,0.65)',
        },
      },
      splitLine: {
        show: true,
        //  改变轴线颜色
        lineStyle: {
          // 使用深浅的间隔色
          color: ['#EEEEEE'],
        },
      },
      // data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      data: this.state.createList.map(execute => execute.time),
    }],
    yAxis: [{
      type: 'value',
      name: '数量',
      nameTextStyle: {
        color: 'black',
      },
      axisLine: {
        lineStyle: {
          color: '#EEEEEE',
          // width: 8, // 这里是为了突出显示加上的
        },
      },
      axisLabel: {
        show: true,
        textStyle: {
          color: 'rgba(0,0,0,0.65)',
        },
      },
      splitLine: {
        show: true,
        //  改变轴线颜色
        lineStyle: {
          // 使用深浅的间隔色
          color: ['#EEEEEE'],
        },
      },
      minInterval: 1,
      
    }, {
      type: 'value',
      // name: '数值',      
      nameTextStyle: {
        color: 'black',
      },
      axisLine: {
        lineStyle: {
          color: '#EEEEEE',
          // width: 8, // 这里是为了突出显示加上的
        }, 
      },
      axisLabel: {
        show: true,
        textStyle: {
          color: 'rgba(0,0,0,0.65)',
        },        
      },
      splitLine: {
        show: true, 
        //  改变轴线颜色
        lineStyle: {
          // 使用深浅的间隔色
          color: ['#EEEEEE'],
        },                            
      },
      minInterval: 1,
      // inverse: true,
    }],
    series: [
      {
        name: '创建数',
        type: 'line',
        data: this.state.createList.map(create => create.value),
      }, {
        // yAxisIndex: 1,
        name: '执行数',
        type: 'line',   
        data: this.state.excuteList.map(execute => execute.value),
      },
    ],
    color: ['#5266D4', '#00BFA5'],
  })

  render() {
    const menu = AppState.currentMenuType;
    const { type, id: projectId, name } = menu;

    return (
      <div className="c7ntest-dashboard-announcement">
        <div>
          <ReactEcharts
            style={{ height: 200 }}
            option={this.getOption()}
          />
        </div>
        <DashBoardNavBar>
          <Link to={commonLink('/summary')}>{Choerodon.getMessage('转至测试摘要', 'review test summary')}</Link>
        </DashBoardNavBar>
      </div>
    );
  }
}
