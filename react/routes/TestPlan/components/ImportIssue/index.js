import React, {
  useEffect, useMemo, useCallback,
} from 'react';
import PropTypes from 'prop-types';
import { Modal } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import {
  createPlan, getPlan, editPlan,
} from '@/api/TestPlanApi';
import SelectIssue from './SelectIssue';
import SelectIssueStore from './SelectIssueStore';
import Context from './context';

const key = Modal.key();

const propTypes = {
  onSubmit: PropTypes.func.isRequired,
};

function ImportIssue({
  modal, submit, onSubmit, planId,
}) {
  const selectIssueStore = useMemo(() => new SelectIssueStore(), []);

  useEffect(() => {
    selectIssueStore.loadIssueTree();
  }, [selectIssueStore]);
  const handleSubmit = useCallback(async () => {
    // try {
    //   if () {

    //     const result = await submit(plan);
    //     onSubmit(result);
    //     return true;
    //   }
    //   return false;
    // } catch (error) {
    //   // console.log(error);
    //   return false;
    // }
  }, []);
  useEffect(() => {
    modal.handleOk(handleSubmit);
  }, [modal, handleSubmit]);

  return (
    <Context.Provider value={{ SelectIssueStore: selectIssueStore }}>
      <SelectIssue />
    </Context.Provider>
  );
}
ImportIssue.propTypes = propTypes;
const ObserverImportIssue = observer(ImportIssue);
export default function openImportIssue({
  onCreate, planId,
}) {
  Modal.open({
    title: '导入用例',
    key,
    drawer: true,
    style: {
      width: 1090,
    },
    children: <ObserverImportIssue planId={planId} submit={createPlan} onSubmit={onCreate} />,
  });
}
