
import React from 'react';
import { Choerodon } from '@choerodon/boot';
import PropTypes from 'prop-types';
import {
  Input, Icon, Modal, Tooltip, Button,
} from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import { DragTable } from '../../../../components';
import { TextEditToggle } from '../../../../components';
import './TestStepTable.less';

const { confirm } = Modal;
const { Text, Edit } = TextEditToggle;
const { TextArea } = Input;

const propTypes = {
  data: PropTypes.shape([]),
  setData: PropTypes.func,
  onCreate: PropTypes.func,
  onDelete: PropTypes.func,
  onUpdate: PropTypes.func,
  onClone: PropTypes.func,
  onDrag: PropTypes.func,
};
const defaultProps = {
  onCreate: newStep => newStep,
  onUpdate: step => step,
  onDelete: () => {

  },
  onClone: (newData, originStep) => ({
    ...originStep,
    ...newData,
    stepId: Math.random(),
  }),
  onDrag: step => step,
};
let localStepId = 0;
function TestStepTable(props) {
  const {
    onCreate, setData, data, onDelete, onUpdate, onClone, onDrag,
  } = props;
  const onDragEnd = async (sourceIndex, targetIndex) => {
    if (sourceIndex === targetIndex) {
      return;
    }
    const drag = data[sourceIndex];
    data.splice(sourceIndex, 1);
    data.splice(targetIndex, 0, drag);
    setData([...data]);
    try {
      const lastRank = targetIndex === 0 ? null : data[targetIndex - 1].rank;
      const nextRank = targetIndex === data.length - 1 ? null : data[targetIndex + 1].rank;
      const testCaseStepDTO = {
        ...drag,
        lastRank,
        nextRank,
        stepIsCreating: false,
      };
      const result = await onDrag(testCaseStepDTO);  
      data[targetIndex] = result;
      setData([...data]);
    } catch (error) {
      data.splice(targetIndex, 1);
      data.splice(sourceIndex, 0, drag);
      setData([...data]);
    }    
  };


  const handleAddCreating = () => {
    const lastRank = data.length
      ? data[data.length - 1].rank : null;
    const testCaseStepDTO = {
      stepId: localStepId,
      // attachments: [],
      lastRank,
      nextRank: null,
      testStep: '',
      testData: '',
      expectedResult: '',
      stepIsCreating: true,
    };
    localStepId += 1;
    setData([...data, testCaseStepDTO]);
  };
  const onCancelCreateStep = (index) => {
    data.splice(index, 1);
    setData([...data]);
  };


  const onCreateStep = async (newStep, index) => {
    const { expectedResult, testStep } = newStep;
    // eslint-disable-next-line no-param-reassign
    
    if (expectedResult && testStep) {
      try {
        const newStepResult = await onCreate(newStep, index);
        if (newStepResult) {
          delete newStepResult.stepIsCreating;
          data[index] = newStepResult;
          setData([...data]);
        }
      } catch (error) {
        // 
      }
      
      // 清除当前创建的值
    } else {
      Choerodon.prompt('测试步骤和预期结果均为必输项');
    }
  };
  const handleEditStep = async (record, index) => {
    // 创建中的编辑
    if (record.stepIsCreating) {
      data[index] = record;
      setData([...data]);
    } else {
      const result = await onUpdate(record);
      data[index] = result;
      setData([...data]);
    }
  };
  const onCloneStep = async (stepId, index) => {
    const originData = data[index];
    const lastRank = originData.rank;
    const nextRank = data[index + 1] ? data[index + 1].rank : null;
    const newStep = await onClone({
      lastRank,
      nextRank,
      stepId,
    }, originData);
    data.splice(index, 0, newStep);
    setData([...data]);
    localStepId += 1;
  };

  const handleDeleteStep = (index, stepId) => {
    confirm({
      width: 560,
      title: '确认删除吗？',
      async onOk() {
        try {
          await onDelete({ data: { stepId } });
          data.splice(index, 1);
          setData([...data]);
        } catch (error) {
          // console.log(error);
        }
      },
      okText: '删除',
      okType: 'danger',
    });
  };


  function render() {
    const {
      disabled,
    } = props;
    const hasStepIsCreating = data.some(item => item.stepIsCreating);

    const columns = [{
      title: null,
      dataIndex: 'stepId',
      key: 'stepId',
      flex: 0.2,
      width: 10,
      render(stepId, record, index) {
        return index + 1;
      },
    }, {
      title: <FormattedMessage id="execute_testStep" />,
      dataIndex: 'testStep',
      key: 'testStep',
      flex: 2.4,
      render: (testStep, record, index) => {
        const { stepIsCreating } = record;
        return (
          <TextEditToggle
            simpleMode
            originData={testStep}
            formKey="testStep"
            style={{ padding: '5px 0' }}
            onSubmit={(value) => {
              handleEditStep({
                ...record,
                testStep: value,
              }, index);
            }}
          >
            <Text>
              {newValue => (
                stepIsCreating
                  ? (
                    <span className="c7ntest-text-wrap">
                      {newValue || <span style={{ color: 'rgb(191, 191, 191)', whiteSpace: 'nowrap' }}>测试步骤</span>}
                    </span>
                  )
                  : <span className="c7ntest-text-wrap">{newValue || '-'}</span>
              )}
            </Text>
            <Edit>
              <TextArea className="hidden-label" maxLength={500} autoFocus autosize placeholder="测试步骤" />
            </Edit>
          </TextEditToggle>
        );
      },
    }, {
      title: <FormattedMessage id="execute_testData" />,
      dataIndex: 'testData',
      key: 'testData',
      flex: 2,
      render: (testData, record, index) => {
        const { stepIsCreating } = record;
        return (
          <TextEditToggle
            simpleMode
            style={{ padding: '5px 0' }}
            originData={testData}
            formKey="testData"
            onSubmit={(value) => {
              handleEditStep({
                ...record,
                testData: value,
              }, index);
            }}
          >
            <Text>
              {newValue => (
                stepIsCreating
                  ? (
                    <span className="c7ntest-text-wrap">
                      {newValue || <span style={{ color: 'rgb(191, 191, 191)', whiteSpace: 'nowrap' }}>测试数据</span>}
                    </span>
                  )
                  : <span className="c7ntest-text-wrap">{newValue || '-'}</span>
              )}
            </Text>
            <Edit>
              <TextArea className="hidden-label" maxLength={500} autoFocus autosize />
            </Edit>
          </TextEditToggle>
        );
      },
    }, {
      title: <FormattedMessage id="execute_expectedOutcome" />,
      dataIndex: 'expectedResult',
      key: 'expectedResult',
      flex: 2.4,
      render: (expectedResult, record, index) => {
        const { stepIsCreating } = record;
        return (
          <TextEditToggle
            simpleMode
            style={{ padding: '5px 0' }}
            originData={expectedResult}
            formKey="expectedResult"
            onSubmit={(value) => {
              handleEditStep({
                ...record,
                expectedResult: value,
              }, index);
            }}
          >
            <Text>
              {newValue => (
                stepIsCreating
                  ? (
                    <span className="c7ntest-text-wrap">
                      {newValue || <span style={{ color: 'rgb(191, 191, 191)', whiteSpace: 'nowrap' }}>预期结果</span>}
                    </span>
                  )
                  : <span className="c7ntest-text-wrap">{newValue || '-'}</span>
              )}
            </Text>
            <Edit>
              <TextArea className="hidden-label" maxLength={500} autoFocus autosize placeholder="预期结果" />
            </Edit>
          </TextEditToggle>
        );
      },
    },
    {
      title: null,
      dataIndex: 'action',
      key: 'action',
      flex: 'unset 0 0%',
      width: 105,
      render: (text, record, index, provided) => {
        const { stepIsCreating } = record;
        return !stepIsCreating ? (
          <div style={{
            display: 'flex', alignItems: 'center', minWidth: 100,
          }}
          >
            <Tooltip title={<FormattedMessage id="execute_move" />}>
              <Icon type="open_with" {...provided.dragHandleProps} style={{ marginRight: 7 }} />
            </Tooltip>
            <Tooltip title={<FormattedMessage id="execute_copy" />}>
              <Button disabled={disabled} shape="circle" funcType="flat" icon="library_books" style={{ color: 'black' }} onClick={() => onCloneStep(record.stepId, index)} />
            </Tooltip>
            <Button disabled={disabled} shape="circle" funcType="flat" icon="delete_forever" style={{ color: 'black' }} onClick={() => handleDeleteStep(index, record.stepId)} />
          </div>
        ) : (
          <div>
            <div {...provided.dragHandleProps} />
            <Tooltip title={<FormattedMessage id="excute_save" />}>
              <Button disabled={disabled} shape="circle" funcType="flat" icon="done" style={{ margin: '0 -5px 5px', color: 'black' }} onClick={() => onCreateStep(record, index)} />
            </Tooltip>
            <Tooltip title={<FormattedMessage id="excute_cancel" />}>
              <Button disabled={disabled} shape="circle" funcType="flat" icon="close" style={{ margin: '0 5px', color: 'black' }} onClick={() => onCancelCreateStep(index)} />
            </Tooltip>
          </div>
        );
      },
    }];

    return (
      <div className="c7ntest-TestStepTable">
        <DragTable
          disabled={disabled}
          pagination={false}
          filterBar={false}
          dataSource={data}
          columns={columns}
          onDragEnd={onDragEnd}
          dragKey="stepId"
          customDragHandle
          scroll={{ x: true }}
        />
        <div style={{ marginLeft: 3, marginTop: 10, position: 'relative' }}>
          <Button
            disabled={disabled || hasStepIsCreating}
            style={{ color: disabled || hasStepIsCreating ? '#bfbfbf' : '#3F51B5' }}
            icon="playlist_add"
            className="leftBtn"
            funcTyp="flat"
            onClick={handleAddCreating}
          >
            <FormattedMessage id="issue_edit_addTestDetail" />
          </Button>
        </div>
      </div>
    );
  }
  return render();
}
TestStepTable.propTypes = propTypes;
TestStepTable.defaultProps = defaultProps;
export default TestStepTable;
