import React from 'react';
import noVersion from '../assets/noVersion.svg';

const NoVersion = ({ onCreateClick }) => (
  <div style={{
    display: 'flex',
    alignItems: 'center',
    marginTop: 10,
    padding: 15,
    // border: '1px dashed var(--text-color3)',
  }}
  >
    <img src={noVersion} alt="" />
    <div style={{ marginLeft: 15 }}>
      <div style={{ fontSize: '12px', color: 'var(--text-color3)' }}>没有一级目录</div>
      <span style={{ fontSize: '14px', marginTop: 10 }}>
        你需要
        <a role="none" style={{ marginLeft: 5 }} onClick={onCreateClick}>创建一级目录</a>
      </span>
    </div>
  </div>
);
export default NoVersion;
