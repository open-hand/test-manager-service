import React, {
  useEffect, useMemo, useCallback,
} from 'react';
import PropTypes from 'prop-types';
import { Modal } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { importIssueToFolder } from '@/api/TestPlanApi';
import SelectIssue from './SelectIssue';
import SelectIssueStore from './SelectIssueStore';
import Context from './context';

const key = Modal.key();

const propTypes = {
  onSubmit: PropTypes.func.isRequired,
};

function ImportIssue({
  modal, submit, onSubmit, planId, folderId: planFolderId,
}) {
  const selectIssueStore = useMemo(() => new SelectIssueStore(), []);

  useEffect(() => {
    selectIssueStore.loadIssueTree();
  }, [selectIssueStore]);
  const handleSubmit = useCallback(async () => {
    try {
      if (selectIssueStore.getSelectedIssueNum > 0) {
        const data = selectIssueStore.getSelectedFolders();
        const result = await submit(planId, planFolderId, data);
        onSubmit(result);
        return true;
      }
      return true;
    } catch (error) {
      // console.log(error);
      return false;
    }
  }, [onSubmit, planFolderId, planId, selectIssueStore, submit]);
  useEffect(() => {
    modal.handleOk(handleSubmit);
  }, [modal, handleSubmit]);

  return (
    <Context.Provider value={{ SelectIssueStore: selectIssueStore, planId }}>
      <SelectIssue />
    </Context.Provider>
  );
}
ImportIssue.propTypes = propTypes;
const ObserverImportIssue = observer(ImportIssue);
export default function openImportIssue({
  onSubmit, planId, folderId,
}) {
  Modal.open({
    title: '导入用例',
    key,
    drawer: true,
    style: {
      width: 1090,
    },
    children: <ObserverImportIssue planId={planId} folderId={folderId} submit={importIssueToFolder} onSubmit={onSubmit} />,
  });
}
