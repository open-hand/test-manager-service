import React, { useContext } from 'react';
import { observer } from 'mobx-react-lite';
import {
  cloneStep, updateStep, deleteStep, createIssueStep,
} from '@/api/IssueManageApi';
import TestStepTable from '../../TestStepTable';
import EditIssueContext from '../stores';

function EditTestStepTable() {
  const {
    store, disabled, caseId, prefixCls, 
  } = useContext(EditIssueContext);
  const { issueSteps } = store;
  const onUpdateStep = newData => store.loadWithLoading(
    updateStep(newData),
  );
  const onCreateIssueStep = newData => store.loadWithLoading(
    createIssueStep({
      issueId: caseId,
      ...newData,
    }),
  );
  const onCloneStep = newData => store.loadWithLoading(
    cloneStep({
      caseId,
      ...newData,
    }),
  );
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
