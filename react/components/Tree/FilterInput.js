import React from 'react';
import { Icon, TextField } from 'choerodon-ui/pro';
import './FilterInput.less';

export default function FilterInput({ value, onChange }) {
  return (
    <TextField
      value={value}
      labelLayout="none"
      className="c7n-tree-input"
      prefix={<Icon type="search" style={{ color: 'rgba(0,0,0,0.45)' }} />}
      placeholder="请输入搜索条件"
      onChange={onChange}
      clearButton
    />
  );
}
