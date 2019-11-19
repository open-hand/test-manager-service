import React, { Component, Fragment } from 'react';
import {
  cloneStep, updateStep, deleteStep, createIssueStep,
} from '../../../../../api/IssueManageApi';
import TestStepTable from '../../TestStepTable';

function EditTestStepTable(props) {
  const {
    disabled, issueId, data, reloadIssue, enterLoad, leaveLoad,
  } = props;

  const onUpdateStep = (newData) => {
    enterLoad();
    updateStep(newData).then((res) => {
      reloadIssue();
      leaveLoad();
    });
  };
  const onCreateIssueStep = (newData) => {
    enterLoad();
    createIssueStep({
      issueId,
      ...newData,
    }).then((res) => {
      reloadIssue();
      leaveLoad();
    });
  };
  const onCloneStep = (newData) => {
    cloneStep({
      issueId,
      ...newData,
    }).then((res) => {
      reloadIssue();
    //   leaveLoad();
    })
      .catch((error) => {
        leaveLoad();
      });
  };
  const onDeleteStep = (newData) => {
    deleteStep(newData)
      .then((res) => {
        reloadIssue();
      });
  };
  return (
    <TestStepTable
      disabled={disabled}
      data={data}
      updateStep={onUpdateStep}
      createIssueStep={onCreateIssueStep}
      cloneStep={onCloneStep}
      deleteStep={onDeleteStep}
    />
  );
}
export default EditTestStepTable;
