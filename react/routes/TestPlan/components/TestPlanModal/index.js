import React, {
  useEffect, useMemo, useCallback, useState,
} from 'react';
import PropTypes from 'prop-types';
import {
  Modal, Form, DataSet, TextArea, DatePicker, Select, Radio, CheckBox, TextField,
} from 'choerodon-ui/pro';
import { mount } from '@choerodon/inject';
import { Choerodon } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import moment from 'moment';
import UserHead from '@/components/UserHead';
import Tip from '@/components/Tip';
import { getProjectId } from '@/common/utils';
import {
  createPlan, getPlan, editPlan, clonePlan,
  checkPlanName,
} from '@/api/TestPlanApi';
import useHasAgile from '@/hooks/useHasAgile';
import DataSetFactory from './dataSet';
import SelectIssue from './SelectIssue';
import SelectIssueStore from './SelectIssueStore';
import Context from './context';
import styles from './index.less';

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
  const [, setUpdateCount] = useState(0);
  const hasAgile = useHasAgile();
  const { caseSelected: initCaseSelected } = initValue;
  const init = useMemo(() => {
    const {
      startDate, endDate,
    } = initValue;
    return {
      ...initValue,
      range: startDate && endDate ? [startDate, endDate] : undefined,
    };
  }, [initValue]);
  const selectIssueStore = useMemo(() => new SelectIssueStore(), []);
  const dataSetUpdate = useCallback(({ name }) => {
    if (name === 'custom') {
      setUpdateCount((count) => count + 1);
    }
  }, []);
  const dataSet = useMemo(() => new DataSet(DataSetFactory({ initValue }, mode, dataSetUpdate)), [dataSetUpdate, initValue, mode]);
  useEffect(() => {
    dataSet.create(init);
  }, [dataSet, init]);
  useEffect(() => {
    if (mode === 'create') {
      selectIssueStore.loadIssueTree(initCaseSelected);
    }
  }, [initCaseSelected, mode, selectIssueStore]);
  const handleSubmit = useCallback(async () => {
    const data = dataSet.toData()[0];
    const {
      range, custom, __id, __status, objectVersionNumber, description, id, ...rest
    } = data;
    const caseSelected = custom ? selectIssueStore.getSelectedFolders() : null;
    if (mode === 'edit' && !dataSet.dirty) {
      return true;
    }
    try {
      const validate = await dataSet.validate();
      if (dataSet.dirty && validate && data.range && data.range[0] && data.range[1]) {
        const plan = {
          ...rest,
          description: description === null ? '' : description,
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
        <TextField name="name" required maxLength={44} valueChangeAction="input" placeholder="例如：用户管理V1.0全流程测试" />
        <TextArea
          name="description"
          placeholder="在此输入描述内容"
        />
        <Select
          name="managerId"
          searchable
          searchMatcher="param"
          optionRenderer={({ record }) => <UserHead user={record.toData()} />}
        // renderer={({ record }) => <UserHead user={record.toData()} />}
        />
        {hasAgile && mount('agile:SelectSprint', {
          name: 'sprintId',
          style: {
            display: 'block',
          },
        })}
        {hasAgile && mount('agile:SelectVersion', {
          name: 'productVersionId',
          valueField: 'versionId',
          multiple: false,
          style: {
            display: 'block',
          },
        })}
        <DatePicker range name="range" min={Date.now()} />
        {mode === 'create' && (
          <div>
            <div style={{
              display: 'flex',
            }}
            >
              <span>导入用例方式</span>
              <Tip title="导入用例方式" />
            </div>
            <div>
              <div style={{ marginTop: 10 }}>
                <Radio name="custom" value={false} className={styles.radio}>全部用例</Radio>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', marginTop: 15 }}>
                <Radio name="custom" value className={styles.radio}>自选用例</Radio>
                {hasAgile && dataSet.toData() && dataSet.toData()[0]?.custom && <CheckBox style={{ marginTop: -8, marginLeft: -10 }} name="sprintLink">选择当前测试计划所属迭代中问题项关联的所有用例</CheckBox>}
              </div>
            </div>
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
          <div style={{
            display: 'flex',
            marginBottom: 10,
          }}
          >
            <span>是否自动同步</span>
            <Tip title="即用例库的用例更新之后是否同步更新计划中的用例" />
          </div>
          <Radio name="autoSync" value defaultChecked>是</Radio>
          <Radio name="autoSync" value={false} style={{ marginLeft: -20 }}>否</Radio>
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
    children: <ObserverTestPlanModal
      mode="create"
      submit={createPlan}
      onSubmit={onCreate}
      initValue={{
        custom: false,
      }}
    />,
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
const ClonePlan = ({ modal, data: defaultValue, onCLone }) => {
  const { id: planId, data: { name } } = defaultValue;
  const dataSet = useMemo(() => new DataSet({
    autoCreate: true,
    fields: [{
      name: 'name',
      type: 'string',
      label: '计划名称',
      defaultValue: `${name}-副本`,
      required: true,
      validator: async (value) => {
        const hasSame = await checkPlanName(value);
        return hasSame ? '计划名称重复' : true;
      },
    }],
    transport: {
      submit: ({ data }) => ({
        method: 'POST',
        url: `/test/v1/projects/${getProjectId()}/plan/${planId}/clone`,
        params: {
          name: data[0].name,
        },
        data: {},
      }),
    },
  }), [name, planId]);

  const handleSubmit = useCallback(async () => {
    const success = await dataSet.submit();
    if (success) {
      onCLone();
    }
    return success;
  },
  [dataSet, onCLone]);
  useEffect(() => {
    modal.handleOk(handleSubmit);
  }, [modal, handleSubmit]);

  return (
    <Form dataSet={dataSet}>
      <TextField name="name" required maxLength={44} valueChangeAction="input" />
    </Form>
  );
};
export async function openClonePlan({ data, onCLone }) {
  Modal.open({
    title: '复制计划',
    key,
    children: <ClonePlan data={data} onCLone={onCLone} />,
  });
}
