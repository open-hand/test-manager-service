import React, { useMemo } from 'react';
import { toJS } from 'mobx';
import JsonBig from 'json-bigint';
import CreateIssue from '@choerodon/agile/lib/components/CreateIssue';
import { addBugForExecuteOrStep } from '../../../../../../api/ExecuteDetailApi';
import { getProjectId } from '../../../../../../common/utils';

const CreateBug = ({
  onOk,
  onCancel, description,
  defectType, id,
}) => {
  const request = (data) => addBugForExecuteOrStep(defectType, id, data);
  const defaultValue = useMemo(() => {
    const defaultValueStr = sessionStorage.getItem('test.plan.execute.detail.create.bug.default.value');
    const defaultValueObj = JsonBig.parse(defaultValueStr) || {};
    if (getProjectId().toString() !== String(defaultValueObj.projectId)) {
      sessionStorage.removeItem('test.plan.execute.detail.create.bug.default.value');
      return {};
    }
    return defaultValueObj;
  }, []);
  return (
    <CreateIssue
      visible
      onOk={onOk}
      defaultDescription={toJS(description)}
      chosenAssignee={defaultValue.assigneeId}
      onCancel={onCancel}
      request={request}
      defaultTypeCode="bug"
      hiddenIssueType
      title="创建缺陷"
    />
  );
};
export default CreateBug;
