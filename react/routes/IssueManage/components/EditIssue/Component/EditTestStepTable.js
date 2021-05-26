import React, { useContext, useState, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { Tooltip, Button, Modal } from 'choerodon-ui/pro';
import {
  cloneStep, updateStep, deleteStep, createIssueStep,
} from '@/api/IssueManageApi';
import { handleRequestFailed } from '@/common/utils';
import TestStepTable from '@/components/TestStepTable';
import CKEditor from '@/components/CKEditor';
import CKEditorViewer from '@/components/CKEditorViewer';
import EditIssueContext from '../stores';
import './EditTestStepTable.less';

function TestStepWrap({ title, children }) {
  return (
    <div className="c7ntest-edit-test-step-item">
      <div className="c7ntest-edit-test-step-item-title">
        {title}
      </div>
      {children}
    </div>
  );
}

function EditTestStepTable({ onUpdateDetail, IssueStore }) {
  const {
    store, disabled, caseId, prefixCls,
  } = useContext(EditIssueContext);
  const { issueSteps, issueInfo: { description } } = store;
  const [editDescriptionShow, setEditDescriptionShow] = useState(false);
  // const [editDes, setEditDes] = useState('');
  // useEffect(() => {
  //   setEditDes(description);
  //   setEditDescriptionShow(false);
  // }, [description]);

  const onUpdateStep = (newData) => store.loadWithLoading(
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
  function renderDescription() {
    if (editDescriptionShow === undefined) {
      return null;
    }
    if (!description || editDescriptionShow) {
      return (
        editDescriptionShow && (
          <div className="line-start mt-10">
            <CKEditor
              key={caseId}
              autoFocus
              footer
              value={description}
              style={{
                height: 'auto', width: '100%', minHeight: 300,
              }}
              onCancel={() => {
                setEditDescriptionShow(false);
                // setEditDes(description);
              }}
              onOk={(value) => {
                onUpdateDetail({ description: value });
                setEditDescriptionShow(false);
                IssueStore.setDescriptionChanged(false);
              }}
              onChange={(value) => {
                IssueStore.setDescriptionChanged(value !== description);
              }}
            />
          </div>
        )
      );
    }
    return (
      <div>
        <CKEditorViewer value={description} />
      </div>
    );
  }

  return (
    <section id="testStep">
      <TestStepWrap title={(
        <>
          <span className="c7ntest-edit-test-step-item-title-text">前置条件</span>
          <div className="c7ntest-edit-test-step-item-title-btn">
            <Tooltip title="编辑" getPopupContainer={(triggerNode) => triggerNode.parentNode.parentNode}>
              <Button
                icon="edit-o"
                onClick={() => {
                  setEditDescriptionShow(true);
                }}
              />
            </Tooltip>
          </div>
        </>
      )}
      >
        {renderDescription()}
      </TestStepWrap>
      <TestStepWrap title="测试步骤">
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
      </TestStepWrap>

    </section>
  );
}
export default observer(EditTestStepTable);
