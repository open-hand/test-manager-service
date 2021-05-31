import React from 'react';
import noRight from '../assets/testExecuteEmpty.svg';

const NoCycle = () => (
  <div style={{
    display: 'flex', alignItems: 'center', height: 250, margin: '88px auto', padding: '50px 75px', 
  }}
  >
    <img src={noRight} alt="" />
    <div style={{ marginLeft: 40 }}>
      <div style={{ fontSize: '14px', color: 'var(--text-color3)' }}>根据当前选定的测试循环没有查询到循环信息</div>
      <div style={{ fontSize: '16px', marginTop: 10 }}>您可以点击测试用例进行详细测试执行</div>
    </div>
  </div>
);
export default NoCycle;
