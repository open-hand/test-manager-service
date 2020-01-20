import React, {
  useEffect, useMemo, useCallback,
} from 'react';
import PropTypes from 'prop-types';
import {
  Modal, Form, DataSet, TextArea, DatePicker, Select, Radio,
} from 'choerodon-ui/pro';
import { Choerodon } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import moment from 'moment';
import UserHead from '@/components/UserHead';
import Tip from '@/components/Tip';
import { PromptInput } from '@/components';
import { getProjectId } from '@/common/utils';
import {
  createPlan, getPlan, editPlan, clonePlan,
} from '@/api/TestPlanApi';
import DataSetFactory from './dataSet';
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
  const dataSet = useMemo(() => new DataSet(DataSetFactory({ initValue })), [initValue]);
  useEffect(() => {
    if (mode === 'create') {
      selectIssueStore.loadIssueTree(initCaseSelected);
    }
  }, [initCaseSelected, mode, selectIssueStore]);
  const handleSubmit = useCallback(async () => {
    const data = dataSet.toData()[0];
    const {
      range, custom, __id, __status, objectVersionNumber, id, ...rest
    } = data;
    const caseSelected = custom ? selectIssueStore.getSelectedFolders() : null;
    if (mode === 'edit' && !dataSet.isModified()) {
      return true;
    }
    try {
      const validate = await dataSet.validate();
      if (dataSet.isModified() && validate) {
        const plan = {
          ...rest,
          custom,
          id,
          objectVersionNumber,
          startDate: moment(range[0]).format('YYYY-MM-DD HH:mm:ss'),
          endDate: moment(range[1]).format('YYYY-MM-DD HH:mm:ss'),
          projectId: getProjectId(),
          caseSelected,
        };
        const result = await submit(plan);
        onSubmit(result);
        return true;
      }
      return false;
    } catch (error) {
      Choerodon.prompt(error.message);
      return false;
    }
  }, [dataSet, mode, onSubmit, selectIssueStore, submit]);
  useEffect(() => {
    modal.handleOk(handleSubmit);
  }, [modal, handleSubmit]);

  return (
    <Context.Provider value={{ SelectIssueStore: selectIssueStore }}>
      <Form dataSet={dataSet} style={{ width: 512 }}>
        <PromptInput name="name" required maxLength={44} />
        <TextArea
          name="description"
        />
        <Select
          name="managerId"
          searchable
          searchMatcher="param"
          optionRenderer={({ record }) => <UserHead user={record.toData()} />}
        // renderer={({ record }) => <UserHead user={record.toData()} />}
        />
        <DatePicker range name="range" min={Date.now()} />
        {mode === 'create' && (
          <div>
            <div>
              <span>导入用例方式</span>
              <Tip title="导入用例方式" />
            </div>
            <Radio name="custom" value={false} defaultChecked>全部用例</Radio>
            <Radio name="custom" value>自选用例</Radio>
          </div>
        )}
      </Form>
      {mode === 'create' && (
        <div style={{ display: dataSet.current && dataSet.current.get('custom') ? 'block' : 'none' }}>
          <SelectIssue />
        </div>
      )}
      <Form dataSet={dataSet} style={{ width: 512 }}>
        <div>
          <div>
            <span>是否自动同步</span>
            <Tip title="即用例库的用例更新之后是否同步更新计划中的用例" />
          </div>
          <Radio name="autoSync" value defaultChecked>是</Radio>
          <Radio name="autoSync" value={false}>否</Radio>
        </div>
      </Form>
    </Context.Provider>
  );
}
TestPlanModal.propTypes = propTypes;
TestPlanModal.defaultProps = defaultProps;
const ObserverTestPlanModal = observer(TestPlanModal);
export function openCreatePlan({
  onCreate,
}) {
  Modal.open({
    title: '创建计划',
    key,
    drawer: true,
    style: {
      width: 1090,
    },
    children: <ObserverTestPlanModal mode="create" submit={createPlan} onSubmit={onCreate} />,
  });
}
export async function openEditPlan({ planId, onEdit }) {
  const planDetail = await getPlan(planId);
  Modal.open({
    title: '修改计划',
    key,
    drawer: true,
    style: {
      width: 780,
    },
    children: <ObserverTestPlanModal
      mode="edit"
      submit={editPlan}
      initValue={planDetail}
      onSubmit={onEdit}
    />,
  });
}
export async function openClonePlan({ planId, onCLone }) {
  // const planDetail = await getPlan(planId);
  Modal.open({
    title: '复制计划',
    key,
    children: '确认复制计划',
    onOk: async () => {
      await clonePlan(planId);
      onCLone();
    },
  });
}
