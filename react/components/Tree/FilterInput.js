import React from 'react';
import { Icon, TextField } from 'choerodon-ui/pro';
import './FilterInput.less';
import useFormatMessage from '@/hooks/useFormatMessage';

export default function FilterInput({ value, onChange }) {
  const formatMessage = useFormatMessage('test.common');

  return (
    <TextField
      value={value}
      labelLayout="none"
      className="c7n-tree-input"
      prefix={<Icon type="search" style={{ color: 'rgba(0,0,0,0.45)' }} />}
      placeholder={formatMessage({ id: 'search.placeholder' })}
      onChange={onChange}
      clearButton
    />
  );
}
