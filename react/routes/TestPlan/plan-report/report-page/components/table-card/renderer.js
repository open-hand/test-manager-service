import React from 'react';
import StatusTag from '@/components/StatusTag';

export function renderStatus({ value }) {
  if (!value) {
    return null;
  }
  const { name, type } = value;
  return (
    <StatusTag
      status={{
        name,
        type,
      }}
    />
  );
}
