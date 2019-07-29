/*
 * @Author: LainCarl 
 * @Date: 2019-01-25 14:53:51 
 * @Last Modified by: LainCarl
 * @Last Modified time: 2019-01-25 15:16:33
 * @Feature: 测试摘要
 */

import React from 'react';
import PropTypes from 'prop-types';
import {
  Table, Button, Icon, Spin, Popover,
} from 'choerodon-ui';
import { Page, Header } from '@choerodon/boot';
import { FormattedMessage } from 'react-intl';
import _ from 'lodash';
import ReactEcharts from 'echarts-for-react';
import { RadioButton, SmartTooltip } from '../../../components/CommonComponent';
import './SummaryHome.scss';

const propTypes = {
  loading: PropTypes.bool.isRequired,
  range: PropTypes.string.isRequired,
  totalExcute: PropTypes.number.isRequired,
  totalCreate: PropTypes.number.isRequired,
  totalTest: PropTypes.number.isRequired,
  notPlan: PropTypes.number.isRequired,
  notRun: PropTypes.number.isRequired,
  caseNum: PropTypes.number.isRequired,
  versionTable: PropTypes.array.isRequired,
  labelTable: PropTypes.array.isRequired,
  componentTable: PropTypes.array.isRequired,
  createList: PropTypes.array.isRequired,
  excuteList: PropTypes.array.isRequired,
  onRefreshClick: PropTypes.func.isRequired,
  onRangeChange: PropTypes.func.isRequired,
};
const SummaryHome = ({
  loading, 
  range, 
  totalExcute,
  totalCreate,
  totalTest,
  notPlan, 
  notRun, 
  caseNum, 
  versionTable,
  labelTable, 
  componentTable,
  createList,
  excuteList,
  onRefreshClick,
  onRangeChange,
}) => { 
  const versionColumns = [{
    title: <FormattedMessage id="summary_version" />,
    dataIndex: 'name',
    key: 'name',
    width: '50%',
    render: name => <SmartTooltip style={{ maxWidth: 200 }}>{name}</SmartTooltip>,
  }, {
    title: <FormattedMessage id="summary_num" />,
    dataIndex: 'num',
    key: 'num',
  }];
  const labelColumns = [{
    title: <FormattedMessage id="summary_label" />,
    dataIndex: 'name',
    key: 'name',
    width: '50%',
    render: name => <SmartTooltip style={{ maxWidth: 200 }}>{name}</SmartTooltip>,
  }, {
    title: <FormattedMessage id="summary_num" />,
    dataIndex: 'num',
    key: 'num',
    width: '50%',
  }];
  const componentColumns = [{
    title: <FormattedMessage id="summary_component" />,
    dataIndex: 'name',
    key: 'name',
    render: name => <SmartTooltip style={{ maxWidth: 200 }}>{name}</SmartTooltip>,
  }, {
    title: <FormattedMessage id="summary_num" />,
    dataIndex: 'num',
    key: 'num',
  }];
  const getCreateOption = () => ({
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
    xAxis: {
      type: 'category',
      name: '日期',
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
      data: createList.map(execute => execute.time),
    },
    yAxis: {
      type: 'value',
      name: '数值',
      nameTextStyle: {
        color: 'black',
      },
      axisLine: {
        lineStyle: {
          color: '#EEEEEE',
          // width: 8, // 这里是为了突出显示加上的
        },
      },
      // axisLabel: {
      //   show: true,
      //   textStyle: {
      //     color: 'rgba(0,0,0,0.65)',
      //   },
      // },
      splitLine: {
        show: true,
        //  改变轴线颜色
        lineStyle: {
          // 使用深浅的间隔色
          color: ['#EEEEEE'],
        },
      },
      minInterval: 1,
    },
    series: [
      {
        name: '创建数',
        type: 'line',
        stack: '总量',
        data: createList.map(execute => execute.value),
      },
    ],
    color: ['#5266D4'],
  });

  const getExecuteOption = () => ({
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
    xAxis: {
      type: 'category',
      name: '日期',
      // nameGap: 28,
      nameTextStyle: {
        color: 'black',
      },
      // nameLocation: 'middle',
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
      data: excuteList.map(execute => execute.time),
    },
    yAxis: {
      type: 'value',
      name: '数值',
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
    },
    series: [
      {
        name: '执行数',
        type: 'line',
        stack: '总量',
        data: excuteList.map(execute => execute.value),
      },
    ],
    color: ['#00BFA5'],
  });
  return (
    <Page>
      <Header title={<FormattedMessage id="summary_title" />}>
        <Button onClick={onRefreshClick}>
          <Icon type="autorenew icon" />
          <span><FormattedMessage id="refresh" /></span>
        </Button>
      </Header>
      <Spin spinning={loading}>
        <div className="c7ntest-content-container">
          <div className="c7ntest-statistic-container">
            <Popover
              placement="topLeft"
              content={<div><FormattedMessage id="summary_totalTest_tip" /></div>}
              title={null}
            >
              <div className="c7ntest-statistic-item-container">
                <div className="c7ntest-statistic-item-colorBar" />
                <div>
                  <div className="c7ntest-statistic-item-title"><FormattedMessage id="summary_totalTest" /></div>
                  <div className="c7ntest-statistic-item-num">{totalTest}</div>
                </div>
              </div>
            </Popover>
            <Popover
              placement="topLeft"
              content={(
                <div>
                  <FormattedMessage id="summary_total_tip1" />
                  <FormattedMessage
                    id="summary_total_tip2"
                    values={{
                      text: <strong style={{ color: 'rgb(255, 85, 0)' }}>{Choerodon.getMessage('未执行', 'not executed')}</strong>,
                    }}
                  />
                  <FormattedMessage id="summary_totalRest_tip3" />
                </div>
)
                }
              title={null}
            >
              <div className="c7ntest-statistic-item-container">
                <div className="c7ntest-statistic-item-colorBar" style={{ borderColor: '#FFB100' }} />
                <div>
                  <div className="c7ntest-statistic-item-title"><FormattedMessage id="summary_totalRest" /></div>
                  <div className="c7ntest-statistic-item-num">{notRun}</div>
                </div>
              </div>
            </Popover>
            <Popover
              placement="topLeft"
              content={(
                <div>
                  <FormattedMessage id="summary_total_tip1" />
                  <FormattedMessage
                    id="summary_total_tip2"
                    values={{
                      text: <strong style={{ color: 'rgb(255, 85, 0)' }}>{Choerodon.getMessage('未执行以外', 'out of not executed')}</strong>,
                    }}
                  />
                  <FormattedMessage id="summary_totalExexute_tip3" />
                </div>
)
                }
              title={null}
            >
              <div className="c7ntest-statistic-item-container">
                <div className="c7ntest-statistic-item-colorBar" style={{ borderColor: '#00BFA5' }} />
                <div>
                  <div className="c7ntest-statistic-item-title"><FormattedMessage id="summary_totalExexute" /></div>
                  <div className="c7ntest-statistic-item-num">{caseNum - notRun}</div>
                </div>
              </div>
            </Popover>
            <Popover
              placement="topLeft"
              content={<div><FormattedMessage id="summary_totalNotPlan_tip" /></div>}
              title={null}
            >
              <div className="c7ntest-statistic-item-container">
                <div className="c7ntest-statistic-item-colorBar" style={{ borderColor: '#FF7043' }} />
                <div>
                  <div className="c7ntest-statistic-item-title"><FormattedMessage id="summary_totalNotPlan" /></div>
                  <div className="c7ntest-statistic-item-num">{totalTest - notPlan}</div>
                </div>
              </div>
            </Popover>
          </div>
          <div className="c7ntest-tableArea-container">
            <div className="c7ntest-table-container">
              <div className="c7ntest-table-title">
                <FormattedMessage id="summary_testSummary" />
                {'（'}
                <FormattedMessage id="summary_summaryByVersion" />
                {'）'}
              </div>
              <Table
                  // rowKey="name"
                autoScroll={false}
                style={{ height: 180 }}
                columns={versionColumns}
                pagination={{ pageSize: 5, showSizeChanger: false }}
                dataSource={versionTable}
                filterBar={false}
              />
            </div>
            <div className="c7ntest-table-container" style={{ margin: '0 15px' }}>
              <div className="c7ntest-table-title">
                <FormattedMessage id="summary_testSummary" />
                {'（'}
                <FormattedMessage id="summary_summaryByComponent" />
                {' ）'}
              </div>
              <Table
                  // rowKey="name"
                autoScroll={false}
                style={{ height: 180 }}
                columns={componentColumns}
                pagination={{ pageSize: 5, showSizeChanger: false }}
                dataSource={componentTable}
                filterBar={false}
              />
            </div>
            <div className="c7ntest-table-container">
              <div className="c7ntest-table-title">
                <FormattedMessage id="summary_testSummary" />
                {'（'}
                <FormattedMessage id="summary_summaryByLabel" />
                {' ）'}
              </div>
              <Table
                  // rowKey="name"
                autoScroll={false}
                style={{ height: 180 }}
                columns={labelColumns}
                pagination={{ pageSize: 5, showSizeChanger: false }}
                dataSource={labelTable}
                filterBar={false}
              />
            </div>
          </div>
          <div style={{ margin: '30px 20px 18px 20px', display: 'flex', alignItems: 'center' }}>
            <div>
              <FormattedMessage id="summary_summaryTimeLeap" />
              {'：'}
            </div>
            <RadioButton
              defaultValue={range}
              onChange={onRangeChange}
              data={[{
                value: '7',
                text: [7, <FormattedMessage id="day" />],
              }, {
                value: '15',
                text: [15, <FormattedMessage id="day" />],
              },
              {
                value: '30',
                text: [30, <FormattedMessage id="day" />],
              }]}
            />
          </div>
          <div className="c7ntest-chartArea-container">
            <div className="c7ntest-chart-container">
              <div style={{ fontWeight: 500, margin: '12px 12px 0 12px' }}><FormattedMessage id="summary_testCreate" /></div>
              <div style={{ height: 260 }}>
                <ReactEcharts
                  option={getCreateOption()}
                />
              </div>
              <div style={{ color: 'rgba(0,0,0,0.65)', marginLeft: 38 }}>
                <FormattedMessage id="summary_testCreated" />
                {'：'}
                <span style={{ color: 'black', fontWeight: 500 }}>{totalCreate}</span>
                {'，'}
                <FormattedMessage id="summary_testLast" />
                <span style={{ color: 'black', fontWeight: 500 }}>
                  {' '}
                  {range}
                  {' '}
                </span>
                <FormattedMessage id="day" />
              </div>
            </div>
            <div className="c7ntest-chart-container" style={{ marginLeft: 16 }}>
              <div style={{ fontWeight: 500, margin: '12px 12px 0 12px' }}><FormattedMessage id="summary_testExecute" /></div>
              <div style={{ height: 260 }}>
                <ReactEcharts option={getExecuteOption()} />
              </div>
              <div style={{ color: 'rgba(0,0,0,0.65)', marginLeft: 38 }}>
                <FormattedMessage id="summary_testExecuted" />
                {'：'}
                <span style={{ color: 'black', fontWeight: 500 }}>{totalExcute}</span>
                {'，'}
                <FormattedMessage id="summary_testLast" />
                <span style={{ color: 'black', fontWeight: 500 }}>
                  {' '}
                  {range}
                  {' '}
                </span>
                <FormattedMessage id="day" />
              </div>
            </div>
          </div>
        </div>
      </Spin>
    </Page>
  );
};


SummaryHome.propTypes = propTypes;

export default SummaryHome;
