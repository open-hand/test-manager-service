import React, { useContext } from 'react';
import { observer } from 'mobx-react-lite';
import { Choerodon } from '@choerodon/boot';
import {
  cloneStep, updateStep, deleteStep, createIssueStep,
} from '@/api/IssueManageApi';
import { handleRequestFailed } from '@/common/utils';
import TestStepTable from '@/components/TestStepTable';
import EditIssueContext from '../stores';

function EditTestStepTable() {
  const {
    store, disabled, caseId, prefixCls,
  } = useContext(EditIssueContext);
  const { issueSteps } = store;
  const onUpdateStep = newData => store.loadWithLoading(
    updateStep(newData), store.loadIssueData,
  );
  const onCreateIssueStep = (newData) => {
    // eslint-disable-next-line no-param-reassign
    delete newData.stepId;// 清除本地排序所用stepId 
    return store.loadWithLoading(
      createIssueStep({
        issueId: caseId,
        ...newData,
      }), store.loadIssueData,
    );
  };
  const onCloneStep = async (newData) => {    
    const result = await handleRequestFailed(store.loadWithLoading(
      cloneStep({
        caseId,
        ...newData,
      }), store.loadIssueData,
    ));
    return result;   
  };
  const onDeleteStep = async (newData) => {
    await deleteStep({
      issueId: caseId,
      ...newData,
    });
    store.loadIssueData();
  };
  return (
    <TestStepTable
      disabled={disabled}
      data={issueSteps}
      setData={(newSteps) => {
        store.setIssueSteps(newSteps);
      }}
      onUpdate={onUpdateStep}
      onCreate={onCreateIssueStep}
      onClone={onCloneStep}
      onDelete={onDeleteStep}
      onDrag={onUpdateStep}
      caseId={caseId}
    />
  );
}
export default observer(EditTestStepTable);
