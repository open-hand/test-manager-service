
import React, { Component, useState, useEffect } from 'react';
import { Choerodon } from '@choerodon/boot';
import {
  Input, Icon, Modal, Tooltip, Button,
} from 'choerodon-ui';
import _ from 'lodash';
import { FormattedMessage } from 'react-intl';

import { DragTable } from '../../../../components';
import { TextEditToggle, UploadInTable } from '../../../../components';
import './TestStepTable.less';

const { confirm } = Modal;
const { Text, Edit } = TextEditToggle;
const { TextArea } = Input;
let createStepId;

/**
 * 测试步骤组件
 * @param  data 数据源
 * @function updateStep(newData) 远程更新测试步骤 覆盖本地数据操作
 * @function createIssueStep(newData) 远程创建测试步骤 覆盖本地数据操作
 * @function cloneStep(newData) 远程克隆测试步骤 覆盖本地数据操作
 * @function deleteStep(newData) 远程刪除测试步骤 覆盖本地数据操作
 * @function onOk 数据操作回调函数  
 */
let localStepId = 0;
function TestStepTable(props) {
  // console.log('TestStepTable', props);
  const [data, setData] = useState(props.data);
  // const [isEditing, setIsEditing] = useState([]);
  // useReducer 
  const [createStep, setCreateStep] = useState({
    testStep: '',
    testData: '',
    expectedResult: '',
  });
  const [didCreatedFlag, setDidCreatedFlag] = useState(false);
  const [createdStepInfo, setCreatedStepInfo] = useState({});
  // rank 用于内部rank排序
  const [rank, setRank] = useState([]);
  // 测试远程加载数据清空 
  useEffect(() => {
    setData(props.data);
  }, [props.data]);

  // useEffect(() => {
  //   setIsEditing(_.map(data, (item, index) => (
  //     {
  //       stepId: item.stepId,
  //       index,
  //       isStepNameEditing: false,
  //       isStepDataEditing: false,
  //       isStepExpectedResultEditing: false,
  //     }
  //   )));
  //   console.log('useEffect', data);
  // }, [data]);


  const onDragEnd = (sourceIndex, targetIndex) => {
    const { data: propsData, updateStep } = props;
    if (sourceIndex === targetIndex) {
      return;
    }
    const drag = propsData[sourceIndex];
    propsData.splice(sourceIndex, 1);
    propsData.splice(targetIndex, 0, drag);
    setData(propsData);
    if (updateStep) {
      const lastRank = targetIndex === 0 ? null : propsData[targetIndex - 1].rank;
      const nextRank = targetIndex === propsData.length - 1 ? null : propsData[targetIndex + 1].rank;
      const dragCopy = { ...drag };
      delete dragCopy.attachments;
      const testCaseStepDTO = {
        ...dragCopy,
        lastRank,
        nextRank,
        stepIsCreating: false,
      };
      updateStep(testCaseStepDTO);
    }
  };


  const handleClickCreate = () => {
    const { data: propsData } = props;
    const lastRank = propsData.length
      ? propsData[propsData.length - 1].rank : null;
    const testCaseStepDTO = {
      attachments: [],
      lastRank,
      nextRank: null,
      testStep: '',
      testData: '',
      expectedResult: '',
      stepIsCreating: true,
    };
    setDidCreatedFlag(false);
    setData([...propsData, testCaseStepDTO]);
    // setIsEditing([...isEditing, {
    //   stepId: undefined,
    //   index: propsData.length,
    //   isStepNameEditing: false,
    //   isStepDataEditing: false,
    //   isStepExpectedResultEditing: false,
    // }]);
    setCreateStep({
      testStep: '',
      testData: '',
      expectedResult: '',
    });
    setCreatedStepInfo({});
  };

  const editStep = (record, func) => {
    const { updateStep } = props;
    /** 远程编辑   */
    if (updateStep) {
      updateStep(record);
    }
  };

  const onCeateIssueStep = (index) => {
    const { data: propsData, createIssueStep } = props;
    const { expectedResult, testStep } = createStep;
    if (expectedResult && testStep) {
      const lastRank = propsData.length
        ? propsData[propsData.length - 1].rank : null;
      const testCaseStepDTO = {
        stepId: localStepId,
        lastRank,
        nextRank: null,
        ...createStep,
      };
      localStepId += 1;
      if (!didCreatedFlag) {
        setDidCreatedFlag(true);
        // stepIsCreating
        propsData[index] = {
          ...testCaseStepDTO,
        };
        setData(propsData);
        if (createIssueStep) {
          delete testCaseStepDTO.stepId;
          createIssueStep(testCaseStepDTO);
        }
      } else {
        setTimeout(() => {
          setCreatedStepInfo({
            ...createdStepInfo,
            ...createStep,
            objectVersionNumber: createdStepInfo.objectVersionNumber || 1,
          });
          editStep(createdStepInfo);
        }, 300);
      }
    } else {
      Choerodon.prompt('测试步骤和预期结果均为必输项');
    }
  };


  const onCloneStep = (stepId, index) => {
    const { data: propsData, cloneStep } = props;
    const lastRank = propsData[index].rank;
    const nextRank = propsData[index + 1] ? propsData[index + 1].rank : null;
    setCreateStep({
      testStep: '',
      testData: '',
      expectedResult: '',
    });

    if (cloneStep) {
      cloneStep({
        lastRank,
        nextRank,
        stepId,
      });
    } else {
      // 本地数据操作
      const oneData = data[index];
      propsData.push({
        ...oneData,
        stepId: localStepId,
      });
      setData(propsData);
      localStepId += 1;
    }
  };

  const handleDeleteTestStep = (index, stepId) => {
    const { data: propsData, deleteStep, onOk } = props;
    if (deleteStep) {
      confirm({
        width: 560,
        title: Choerodon.getMessage('确认删除吗？', 'Confirm delete'),
        content:
          // eslint-disable-next-line react/jsx-indent
          <div style={{ marginBottom: 32 }}>
            {Choerodon.getMessage('当你点击删除后，所有与之关联的测试步骤将删除!', 'When you click delete, after which the data will be permanently deleted and irreversible!')}
          </div>,
        onOk() {
          /** 远程删除 */
          deleteStep({ data: { stepId } });
          // return deleteStep({ data: { stepId } })
          //   .then((res) => {
          //     onOk();
          //   });
        },
        onCancel() { },
        okText: Choerodon.getMessage('删除', 'Delete'),
        okType: 'danger',
      });
    } else {
      propsData.splice(index, 1);
      setData(propsData);
      setCreateStep({
        testStep: '',
        testData: '',
        expectedResult: '',
      });
    }
  };


  const cancelCreateStep = (index) => {
    const { data: propsData } = props;
    propsData.splice(index, 1);
    setData(propsData);
    setCreateStep({
      testStep: '',
      testData: '',
      expectedResult: '',
    });
  };

  function render() {
    const {
      onOk, enterLoad, leaveLoad, disabled,
    } = props;
    const hasStepIsCreating = data.find(item => item.stepIsCreating);

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
      render: (testStep, record) => {
        const { stepIsCreating } = record;
        return (
          <TextEditToggle
            simpleMode
            originData={stepIsCreating ? createStep.testStep : testStep}
            formKey="testStep"
            style={{ padding: '5px 0' }}
            // rules={[{ required: true, message: '请输入测试步骤' }]}
            onSubmit={(value) => {
              if (stepIsCreating) {
                setCreateStep({
                  ...createStep,
                  testStep: value,
                });
              } else {
                editStep({
                  ...record,
                  testStep: value,
                });
              }
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
      render: (testData, record) => {
        const { stepIsCreating } = record;
        return (
          <TextEditToggle
            simpleMode
            style={{ padding: '5px 0' }}
            originData={stepIsCreating ? createStep.testData : testData}
            formKey="testData"
            onSubmit={(value) => {
              if (stepIsCreating) {
                setCreateStep({
                  ...createStep,
                  testData: value,
                });
              } else {
                editStep({
                  ...record,
                  testData: value,
                });
              }
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
              {/* <TextArea autoFocus autosize placeholder="测试数据" /> */}
            </Edit>
          </TextEditToggle>
        );
      },
    }, {
      title: <FormattedMessage id="execute_expectedOutcome" />,
      dataIndex: 'expectedResult',
      key: 'expectedResult',
      flex: 2.4,
      render: (expectedResult, record) => {
        const { stepIsCreating } = record;
        return (
          <TextEditToggle
            simpleMode
            style={{ padding: '5px 0' }}
            originData={stepIsCreating ? createStep.expectedResult : expectedResult}
            formKey="expectedResult"
            // rules={[{ required: true, message: '请输入预期结果' }]}
            onSubmit={(value) => {
              if (stepIsCreating) {
                setCreateStep({
                  ...createStep,
                  expectedResult: value,
                });
              } else {
                editStep({
                  ...record,
                  expectedResult: value,
                });
              }
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
      render: (attachments, record, index, provided, snapshot) => {
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
            <Button disabled={disabled} shape="circle" funcType="flat" icon="delete_forever" style={{ color: 'black' }} onClick={() => handleDeleteTestStep(index, record.stepId)} />
          </div>
        ) : (
          <div>
            <div {...provided.dragHandleProps} />
            <Tooltip title={<FormattedMessage id="excute_save" />}>
              <Button disabled={disabled} shape="circle" funcType="flat" icon="done" style={{ margin: '0 -5px 5px', color: 'black' }} onClick={() => onCeateIssueStep(index)} />
            </Tooltip>
            <Tooltip title={<FormattedMessage id="excute_cancel" />}>
              <Button disabled={disabled} shape="circle" funcType="flat" icon="close" style={{ margin: '0 5px', color: 'black' }} onClick={() => cancelCreateStep(index)} />
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
          createIssueStep={onCeateIssueStep}
          hasStepIsCreating={hasStepIsCreating}
          dragKey="stepId"
          customDragHandle
          scroll={{ x: true }}
        />
        <div style={{ marginLeft: 3, marginTop: 10, position: 'relative' }}>
          <Button disabled={disabled || hasStepIsCreating} style={{ color: disabled || hasStepIsCreating ? '#bfbfbf' : '#3F51B5' }} icon="playlist_add" className="leftBtn" funcTyp="flat" onClick={handleClickCreate}>
            <FormattedMessage id="issue_edit_addTestDetail" />
          </Button>
        </div>
      </div>
    );
  }
  return render();
}

export default TestStepTable;
