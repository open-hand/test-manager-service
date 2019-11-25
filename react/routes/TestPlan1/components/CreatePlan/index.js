import React, { useEffect, useMemo } from 'react';
import {
  Modal, Form, TextField, DataSet, TextArea, DateTimePicker,
} from 'choerodon-ui/pro';
import { Choerodon } from '@choerodon/boot';

const key = Modal.key();


export function CreatePlan({
  modal,
}) {
  const dataSet = useMemo(() => new DataSet({
    autoCreate: true,
    fields: [
      {
        name: 'planName', type: 'string', label: '计划名称', required: true, 
      },
      {
        name: 'range',
        type: 'dateTime',
        range: true,
        label: '持续时间',
        required: true,
      },
      {
        name: 'description', type: 'string', label: '描述',
      },
    ],
  }), []);
  return (
    <Form dataSet={dataSet} style={{ width: 512 }}>
      <TextField name="planName" required />
      <TextArea
        name="description"  
      />
      <DateTimePicker range name="range" min={Date.now()} />      
    </Form>
  );
}
export default function openCreatePlan() {
  Modal.open({
    title: '创建计划',
    key,
    drawer: true,
    style: {
      width: 1090,
    },
    children: <CreatePlan />,
  });
}
