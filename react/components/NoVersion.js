import React from 'react';
import { Link } from 'react-router-dom';
import { agileVersionLink } from '../common/utils';
import noVersion from '../assets/noVersion.svg';

const NoVersion = () => (
  <div style={{
    display: 'flex',
    alignItems: 'center',   
    marginRight: 10,
    marginTop: 10,
    padding: 15,
    border: '1px dashed rgba(0,0,0,0.54)',
  }}
  >
    <img src={noVersion} alt="" />
    <div style={{ marginLeft: 15 }}>
      <div style={{ fontSize: '12px', color: 'rgba(0,0,0,0.65)' }}>创建版本</div>
      <span style={{ fontSize: '14px', marginTop: 10 }}>
        你需要到敏捷管理进行
        <Link style={{ marginLeft: 5 }} to={agileVersionLink()}>创建发布版本</Link>
      </span>
    </div>
  </div>
);
export default NoVersion;
