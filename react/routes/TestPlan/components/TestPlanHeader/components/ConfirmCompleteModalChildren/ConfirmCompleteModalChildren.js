import React, { useState, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import ReactEcharts from 'echarts-for-react';
import { getStatusByFolder } from '../../../../../../api/TestPlanApi';
import './ConfirmCompleteModalChildren.less';

function ConfirmCompleteModalChildren({ planName, testPlanStore }) {
  const [statusRes, setStatusRes] = useState({});

  useEffect(() => {
    getStatusByFolder({ planId: testPlanStore.getId()[0], folderId: testPlanStore.getId()[0] }).then((res) => {
      setStatusRes(res);
    });
  }, [testPlanStore]);

  const transformData = (data) => {
    const transformedRes = [];
    data.forEach((item) => {
      transformedRes.push({
        value: item.count,
        name: item.statusName,
        color: item.statusColor,
      });
    });
    return transformedRes;
  }; 

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b} : {c}个 {d}%',
    },
    series: [
      {
        name: '',
        type: 'pie',
        radius: '90%', // 饼图的大小
        center: ['50%', '50%'],
        grid: {
          left: 0,
          right: 0,
          top: 0,
          bottom: 0,
        },
        data: transformData(statusRes.statusVOList || []),
        itemStyle: {
          emphasis: {
            fontSize: 12,
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)',
          },
          color: data => data.data.color,
        },
        label: {
          show: false,  
        },
        labelLine: {
          show: false,  
        },
        hoverOffset: 5,
      },
    ],
  };
  return (
    <div className="c7ntest-completePlan-confirm-modal-children">
      <div>
        <p style={{ marginBottom: 6, fontSize: 14 }}>{`${planName}测试状态如图所示`}</p>
        <p style={{ marginBottom: 0, fontSize: 14 }}>确定要结束此次计划</p>
      </div>
      <div>
        <ReactEcharts
          style={{ height: 180, width: 265 }}
          option={option}
        />
      </div>
    </div>
  );
}

export default observer(ConfirmCompleteModalChildren);
