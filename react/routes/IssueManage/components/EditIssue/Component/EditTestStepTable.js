import React, { useContext, useState } from 'react';
import { observer } from 'mobx-react-lite';
import { Choerodon } from '@choerodon/boot';
import WYSIWYGViewer from '@choerodon/agile/lib/components/WYSIWYGViewer';
import { Tooltip, Button } from 'choerodon-ui/pro';
import {
  cloneStep, updateStep, deleteStep, createIssueStep,
} from '@/api/IssueManageApi';
import { handleRequestFailed } from '@/common/utils';
import TestStepTable from '@/components/TestStepTable';
import { uploadFile } from '@/api/IssueManageApi';
import { delta2Html, text2Delta } from '@/common/utils';
import { openFullEditor, WYSIWYGEditor } from '@/components';
import EditIssueContext from '../stores';
import IssueDescription from './IssueDescription';
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

function EditTestStepTable({ onUpdateDetail }) {
  const {
    store, disabled, caseId, prefixCls,
  } = useContext(EditIssueContext);
  const [editDescriptionShow, setEditDescriptionShow] = useState(false);
  const { issueSteps, issueInfo: { description } } = store;
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
    let delta;
    if (editDescriptionShow === undefined) {
      return null;
    }
    if (!description || editDescriptionShow) {
      delta = text2Delta(description);
      return (
        editDescriptionShow && (
          <div className="line-start mt-10">
            <WYSIWYGEditor
              autoFocus
              bottomBar
              defaultValue={delta}
              style={{ height: 200, width: '100%' }}
              handleDelete={() => {
                setEditDescriptionShow(false);
              }}
              handleSave={(value) => {
                onUpdateDetail({ description: value });
                setEditDescriptionShow(false);
              }}
            />
          </div>
        )
      );
    } else {
      delta = delta2Html(description);
      return (
        <div>
          <IssueDescription style={{ paddingRight: 20 }} data={delta} />
        </div>
      );
    }
  }
  function handleOpenFullEditor() {
    openFullEditor({
      initValue: description,
      onOk: async (value) => { await onUpdateDetail({ description: value }); },
    });
  }
  return (
    <section id="testStep">
      <TestStepWrap title={(
        <>
          <span className="c7ntest-edit-test-step-item-title-text">前置条件</span>
          <div className="c7ntest-edit-test-step-item-title-btn">
            <Tooltip title="全屏编辑" getPopupContainer={(triggerNode) => triggerNode.parentNode}>
              <Button color="primary" icon="zoom_out_map" onClick={handleOpenFullEditor} />
            </Tooltip>
            <Tooltip title="编辑" getPopupContainer={(triggerNode) => triggerNode.parentNode.parentNode}>
              <Button
                color="primary"
                icon="mode_edit mlr-3"
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
