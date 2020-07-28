import React, { useMemo } from 'react';
import {
  Modal, Form, TextField, DataSet, 
} from 'choerodon-ui/pro';
import { handleRequestFailed } from '@/common/utils';
import { addFolder } from '@/api/IssueManageApi';

const key = Modal.key();


export function CreateFolder({
  modal, onCreate,
}) {
  const dataSet = useMemo(() => new DataSet({
    autoCreate: true,
    fields: [
      {
        name: 'name', type: 'string', label: '目录名称', required: true, 
      },     
    ],
  }), []);
  const handleOk = async () => {
    const isValidate = await dataSet.validate();
    if (isValidate) {
      const [values] = dataSet.toData();
      const data = {
        // parentId: 0,
        rootNode: true,
        name: values.name,
        type: 'cycle',
      };
      
      await handleRequestFailed(addFolder(data));      
      onCreate();
      modal.close();      
    }
    return false;
  };
  modal.handleOk(handleOk);
  return (
    <Form dataSet={dataSet}>
      <TextField name="name" required maxLength={20} />     
    </Form>
  );
}
export default function openCreatePlan({ onCreate }) {
  Modal.open({
    title: '创建目录',
    key,
    drawer: true,
    style: {
      width: 340,
    },
    children: <CreateFolder onCreate={onCreate} />,
  });
}
