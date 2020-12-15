/* eslint-disable import/prefer-default-export */
import React from 'react';
import StatusTag from '@/components/StatusTag';
import UserHead from '@/components/UserHead';

export function renderStatus({ value }) {
  if (!value) {
    return null;
  }
  const { name, type } = value;
  return (
    <StatusTag
      style={{
        width: 'auto',
      }}
      status={{
        name,
        type,
      }}
    />
  );
}
export function renderAssignee({ value }) {
  if (!value) {
    return null;
  }
  return <UserHead user={value} head={false} />;
}
