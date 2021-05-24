import React, {
  useEffect, useState, useCallback, useImperativeHandle,
} from 'react';
import { observer } from 'mobx-react-lite';
import {
  getStatusList,
} from '@/api/TestStatusApi';
import { Select, Form } from 'choerodon-ui/pro';
import { autoTransform, getTransform } from '@/api/agileApi';
import styles from './index.less';

const { Option } = Select;

interface IStatus {
  statusId: string
  statusName: string
}

interface Props {
  issueTypeId: string
  statusId: string
  testTransformRef: React.MutableRefObject<{
    testTransform: (data: {
      agileIssueTypeId: string,
      agileStatusId: string,
      testStatusId: string | null
    }) => Promise<void>,
    selectedStatusId: string | undefined
  }>,
}
const StatusAutoTransform: React.FC<Props> = ({ issueTypeId, statusId, testTransformRef }) => {
  const [statusList, setStatusList] = useState<IStatus[]>([]);
  const [selectedStatusId, setSelectedStatusId] = useState<string | undefined>();
  useEffect(() => {
    getStatusList('CYCLE_CASE').then((res: IStatus[]) => {
      setStatusList(res);
    });
  }, []);

  useEffect(() => {
    getTransform(issueTypeId, statusId).then((res: any) => {
      setSelectedStatusId(res?.testStatusVO?.statusId);
    });
  }, [issueTypeId, statusId]);

  const onTestTransform = useCallback(async (data) => {
    await autoTransform(data);
  }, []);

  useImperativeHandle(testTransformRef, () => ({
    testTransform: onTestTransform,
    selectedStatusId,
  }));

  const handleChange = useCallback((id) => {
    setSelectedStatusId(id);
  }, []);

  return (
    <Form className={styles.statusAutoTransform}>
      <Select
        onChange={handleChange}
        value={selectedStatusId}
        label="测试执行状态"
        style={{
          width: '100%',
        }}
      >
        {
        statusList.map((status) => (
          <Option value={status.statusId} key={status.statusId}>{status.statusName}</Option>
        ))
      }
      </Select>
    </Form>
  );
};

export default observer(StatusAutoTransform);
