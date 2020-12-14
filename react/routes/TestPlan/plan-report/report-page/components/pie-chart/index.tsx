import React, { useContext, useMemo } from 'react';
import ReactEcharts from 'echarts-for-react';
import { EChartOption } from 'echarts';
import { observer } from 'mobx-react-lite';
import Dot from '@/components/dot';
import context from '../../context';
import Card from '../card';
import styles from './index.less';

export interface Props {
}
const PieChart: React.FC<Props> = () => {
  const { store } = useContext(context);
  const { pieData } = store;
  const { total, statusVOList } = pieData;
  const data = useMemo(() => statusVOList.map((s) => ({
    value: s.count,
    name: s.statusName,
    itemStyle: { color: s.statusColor },
  })), [statusVOList]);
  const options: EChartOption = useMemo(() => ({
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)',
    },
    series: [
      {
        name: '执行情况',
        type: 'pie',
        radius: ['65%', '100%'],
        avoidLabelOverlap: false,
        hoverAnimation: false,
        itemStyle: {
          borderWidth: 1,
          borderColor: '#fff',
        },
        label: {
          show: false,
        },
        labelLine: {
          show: false,
        },
        data,
      },
    ],
    // 环形图中间添加文字
    graphic: [{
      type: 'group',
      left: 'center',
      top: 50,
      children: [{
        type: 'circle',
        shape: {
          r: 50,
        },
        style: {
          fill: '#fff',
        },
      }, {
        type: 'text',
        left: 'center',
        top: -15,
        style: {
          text: '执行总数',
          textAlign: 'center',
          fill: '#716D88',
          fontSize: 14,
        },
      }, {
        type: 'text',
        left: 'center',
        top: 10,
        style: {
          text: total,
          textAlign: 'center',
          fill: '#000',
          width: 30,
          height: 30,
          fontSize: 14,
        },
      }],
    }],
  }), [data, total]);
  return (
    <Card className={styles.pie_chart}>
      <div className={styles.left}>
        <div className={styles.title}>
          计划执行情况
        </div>
        <ReactEcharts
          style={{ width: 200, height: 200 }}
          option={options}
        />
      </div>
      <div className={styles.right}>
        <div className={styles.info_card}>
          <div className={styles.scroll}>
            <table>
              <thead>
                <tr>
                  <th>状态</th>
                  <th>百分比</th>
                  <th>执行数</th>
                </tr>
              </thead>
              <tbody>
                {statusVOList.map((status) => (
                  <tr>
                    <td>
                      <Dot color={status.statusColor}>{status.statusName}</Dot>
                    </td>
                    <td>{`${Math.floor((status.count / total) * 10000) / 100}%`}</td>
                    <td>{status.count}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

        </div>
      </div>
    </Card>
  );
};

export default observer(PieChart);
