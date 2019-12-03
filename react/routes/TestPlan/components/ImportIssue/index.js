import React, {
  useEffect, useMemo, useCallback,
} from 'react';
import PropTypes from 'prop-types';
import {
  Modal, Form, TextField, DataSet, TextArea, DateTimePicker, Select, Radio,
} from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import moment from 'moment';
import UserHead from '@/components/UserHead';
import Tip from '@/components/Tip';
import { getProjectId } from '@/common/utils';
import {
  createPlan, getPlan, editPlan,
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
      if (dataSet.validate()) {
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
      // console.log(error);
      return false;
    }
  }, [dataSet, mode, onSubmit, selectIssueStore, submit]);
  useEffect(() => {
    modal.handleOk(handleSubmit);
  }, [modal, handleSubmit]);

  return (
    <Context.Provider value={{ SelectIssueStore: selectIssueStore }}>
      <Form dataSet={dataSet} style={{ width: 512 }}>
        <TextField name="name" required maxLength={30} />
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
        <DateTimePicker range name="range" min={Date.now()} />
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
