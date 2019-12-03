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
  initValue: PropTypes.shape({}),
  onSubmit: PropTypes.func.isRequired,
};
const defaultProps = {
  initValue: {},
};
function TestPlanModal({
  modal, initValue, submit, onSubmit, mode = 'create',
}) {
  const { caseSelected: initCaseSelected } = initValue;
  const selectIssueStore = useMemo(() => new SelectIssueStore(), []);

  useEffect(() => {
    if (mode === 'create') {
      selectIssueStore.loadIssueTree(initCaseSelected);
    }
  }, [initCaseSelected, mode, selectIssueStore]);
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
TestPlanModal.propTypes = propTypes;
TestPlanModal.defaultProps = defaultProps;
const ObserverTestPlanModal = observer(TestPlanModal);
export default function openCreatePlan({
  onCreate,
}) {
  Modal.open({
    title: '导入用例',
    key,
    drawer: true,
    style: {
      width: 1090,
    },
    children: <ObserverTestPlanModal submit={createPlan} onSubmit={onCreate} />,
  });
}
