import React from 'react';
import { addBugForExecuteOrStep } from '../../../../api/ExecuteDetailApi';

let CreateIssue = null;
try {
  CreateIssue = require('@choerodon/agile-pro/lib/components/CreateIssue').default;
} catch (error) {
  CreateIssue = require('@choerodon/agile/lib/components/CreateIssue').default;
}

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
