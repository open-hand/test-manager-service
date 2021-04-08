import React, { useEffect, useMemo, useState } from 'react';
import { toJS } from 'mobx';
import JsonBig from 'json-bigint';
import CreateIssue from '@choerodon/agile/lib/components/CreateIssue';
import { getUser } from '@/api/IamApi';
import { addBugForExecuteOrStep } from '../../../../../../api/ExecuteDetailApi';
import { getProjectId } from '../../../../../../common/utils';

const CreateBug = ({
  onOk,
  onCancel, description,
  defectType, id,
}) => {
  const request = (data) => addBugForExecuteOrStep(defectType, id, data);
  const [chosenAssignee, setChosenAssignee] = useState();
  useEffect(() => {
    const defaultValueStr = sessionStorage.getItem('test.plan.execute.detail.create.bug.default.value');
    const defaultValueObj = JsonBig.parse(defaultValueStr) || {};
    if (getProjectId().toString() !== String(defaultValueObj.projectId)) {
      sessionStorage.removeItem('test.plan.execute.detail.create.bug.default.value');
      setChosenAssignee(true);
    } else {
      getUser(defaultValueObj.assigneeId).then((res) => {
        const { list } = res;
        setChosenAssignee(list[0] ? list[0] : true);
      });
    }
  }, []);
  return (
    <CreateIssue
      visible={chosenAssignee}
      onOk={onOk}
      defaultDescription={toJS(description)}
      chosenAssignee={typeof (chosenAssignee) === 'boolean' ? undefined : chosenAssignee}
      onCancel={onCancel}
      request={request}
      defaultTypeCode="bug"
      enabledTypeCodes={['bug']}
      title="创建缺陷"
    />
  );
};
export default CreateBug;
