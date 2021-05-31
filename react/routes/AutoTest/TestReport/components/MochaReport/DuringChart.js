import React, { Component } from 'react';
import ReactEcharts from 'echarts-for-react';
import { observer } from 'mobx-react';
import ReportStore from './reportStore';


@observer
class DuringChart extends Component {
  getOption() {
    // const { intl: { formatMessage } } = this.props;
    const tests = ReportStore.getTests;
    // console.log(tests);
    const { stats } = ReportStore;
    // console.log(tests);
    const averageDuration = [];
    // averageDuration.length = pipelineTime && pipelineTime.length ? pipelineTime.length : 0;
    // const ava = pipelineTime && pipelineTime.length ? ((_.reduce(pipelineTime, (sum, n) => sum + parseFloat(n), 0)) / pipelineTime.length) : 0;
    // _.fill(averageDuration, ava);
    return {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'none',
        },
        backgroundColor: '#fff',
        // dataZoom: [{
        //   start: 0,
        //   end: 10,
        //   handleIcon: 'M10.7,11.9v-1.3H9.3v1.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4v1.3h1.3v-1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9z M13.3,24.4H6.7V23h6.6V24.4z M13.3,19.6H6.7v-1.4h6.6V19.6z',
        //   handleSize: '80%',
        //   handleStyle: {
        //     color: '#fff',
        //     shadowBlur: 3,
        //     shadowColor: 'rgba(0, 0, 0, 0.6)',
        //     shadowOffsetX: 2,
        //     shadowOffsetY: 2,
        //   },
        // }],
        dataZoom: [{
          type: 'inside',
        }, {
          type: 'slider',
        }],
        textStyle: {
          color: '#000',
          fontSize: 13,
          lineHeight: 20,
        },
        padding: [10, 15],
        extraCssText:
          'box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.2); border: 1px solid #ddd; border-radius: 0;',
        formatter(params, ticket) {
          const { name, value, dataIndex } = params[0];
          return `<div>
            <div>${'全名'}：${tests[dataIndex].fullTitle}</div>
            <div>${'名称'}：${name}</div>
            <div>${'时长'}：${value > 1000 ? `${value / 1000}s` : `${value}ms`}</div>
          </div>`;
        },
      },
      dataZoom: [{
        type: 'slider',
        show: true,
        // xAxisIndex: [0],
        // left: '9%',
        // bottom: -5,
        startValue: tests.length - 20 || 0,
        endValue: tests.length - 1, // 初始化滚动条
      }],
      // grid: {
      //   left: '2%',
      //   right: '3%',
      //   bottom: '3%',
      //   containLabel: true,
      // },
      xAxis: {
        type: 'category',
        // axisTick: { show: false },
        // axisLine: {
        //   lineStyle: {
        //     color: '#eee',
        //     type: 'solid',
        //     width: 2,
        //   },
        // },
        // axisLabel: {
        //   margin: 13,
        //   textStyle: {
        //     color: 'var(--text-color3)',
        //     fontSize: 12,
        //   },
        //   rotate: 40,
        //   formatter(value) {
        //     return `${value.substr(0, value.indexOf('-') + 5)}`;
        //   },
        // },
        // splitLine: {
        //   lineStyle: {
        //     color: ['#eee'],
        //     width: 1,
        //     type: 'solid',
        //   },
        // },
        data: tests.map(during => during.title),
      },
      yAxis: {
        name: '时间(ms)',
        type: 'value',

        nameTextStyle: {
          fontSize: 13,
          color: '#000',
        },
        axisTick: { show: false },
        axisLine: {
          lineStyle: {
            color: '#eee',
            type: 'solid',
            width: 2,
          },
        },

        axisLabel: {
          margin: 19.3,
          textStyle: {
            color: 'var(--text-color3)',
            fontSize: 12,
          },
        },
        splitLine: {
          lineStyle: {
            color: '#eee',
            type: 'solid',
            width: 1,
          },
        },
        // min: (pipelineTime && pipelineTime.length) ? null : 0,
        // max: (pipelineTime && pipelineTime.length) ? null : 4,
      },
      series: [
        {
          type: 'bar',
          barWidth: '30%',
          itemStyle: {
            color: 'var(--primary-color)',
            // borderColor: '#4D90FE',
            emphasis: {
              shadowBlur: 10,
              shadowColor: 'rgba(0,0,0,0.20)',
            },
          },
          data: tests.map(during => during.duration),
        },
        {
          type: 'line',
          symbol: 'none',
          lineStyle: {
            color: 'rgba(0, 0, 0, 0.36)',
            width: 2,
            type: 'dashed',
            border: '1px solid #4D90FE',
          },
          data: averageDuration,
        },
      ],
    };
  }

  render() {
    return (
      <ReactEcharts
        // style={{ height: 200, flex: 1 }}
        option={this.getOption()}
      />
    );
  }
}

DuringChart.propTypes = {

};

export default DuringChart;
