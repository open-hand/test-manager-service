import React from 'react';
import CreateIssue from '@choerodon/agile/lib/components/CreateIssue';
import { addBugForExecuteOrStep } from '../../../../../../api/ExecuteDetailApi';

const CreateBug = ({
  onOk,
  onCancel,
  defectType, id,
}) => {
  const request = data => addBugForExecuteOrStep(defectType, id, data);
  return (
    <CreateIssue
      visible
      onOk={onOk}
      onCancel={onCancel}
      request={request}
      defaultTypeCode="bug"
      hiddenIssueType
      title="创建缺陷"
    />
  );
};
export default CreateBug;
