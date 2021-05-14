/* eslint-disable react/jsx-props-no-spreading */
import React, { useState } from 'react';
import { Choerodon } from '@choerodon/boot';
import PropTypes from 'prop-types';
import {
  Input, Icon, Modal, Tooltip,
} from 'choerodon-ui';
import { Button } from 'choerodon-ui/pro';
import { FormattedMessage } from 'react-intl';
import useClickOnce from '@/hooks/useClickOnce';
import { DragTable, TextEditToggle } from '..';

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
  onCreate: (newStep) => newStep,
  onUpdate: (step) => step,
  onDelete: () => {

  },
  onClone: (newData, originStep) => ({
    ...originStep,
    ...newData,
    stepId: Math.random(),
  }),
  onDrag: (step) => step,
};
function TestStepTable(props) {
  const {
    onCreate, setData, data, onDelete, onUpdate, onClone, onDrag, caseId,
  } = props;
  const { dragKey = 'stepId' } = props;
  const [recordRef, setRecordRef] = useState([]);
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
      // attachments: [],
      lastRank,
      nextRank: null,
      testStep: '',
      testData: '',
      expectedResult: '',
      stepIsCreating: true,
      stepId: Math.random(),
    };
    setData([...data, testCaseStepDTO]);
  };

  const onCancelCreateStep = (index) => {
    data.splice(index, 1);
    setData([...data]);
  };
  /**
   * 检查多段文本是否全为空格
   * @param  {...any} restText
   */
  const checkAllSpace = (...texts) => {
    let isAllSpace = false;
    texts.forEach((text) => {
      if (text !== null && text.length !== 0 && text.trim().length === 0) {
        isAllSpace = true;
      }
    });
    return isAllSpace;
  };

  const onCreateStep = async (newStep, index) => {
    const lastRank = data[index - 1]?.rank;
    const { expectedResult, testStep, testData } = newStep;
    // 特殊字符判断 全为空格时，则进行提示
    if (checkAllSpace(expectedResult, testStep, testData)) {
      Choerodon.prompt('不能有空格');
      return;
    }
    if (expectedResult && testStep) {
      try {
        const newStepResult = await onCreate({ ...newStep, lastRank }, index);
        if (newStepResult) {
          delete newStepResult.stepIsCreating;
          data[index] = newStepResult;
          setData([...data]);
          // 创建成功后自动展开下一个新步骤
          handleAddCreating();
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
  const onCloneStep = useClickOnce((reset) => async (stepId, index) => {
    const originData = data[index];
    const lastRank = originData.rank;
    const nextRank = data[index + 1] ? data[index + 1].rank : null;
    try {
      const newStep = await onClone({
        lastRank,
        nextRank,
        stepId,
      }, originData);
      data.splice(index + 1, 0, newStep);
      setData([...data]);
      reset();
    } catch (error) {
      //
      reset();
    }
  });

  const handleDeleteStep = (index, stepId) => {
    confirm({
      width: 560,
      title: '确认删除吗？',
      async onOk() {
        try {
          await onDelete({ data: { issueId: caseId, stepId } });
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
  /**
   * 根据Id寻找ref记录
   * 若无记录返回undefined
   *
   * @param {*} id
   */
  const findRefByID = (id) => recordRef.find((item) => item.id === id);
  /**
 * 保存每一步ref
 * @param {*} record
 * @param {*} index
 * @param {*} ref
 */
  const saveCreateRef = (record, index, type, ref) => {
    const refData = findRefByID(record[dragKey]);
    if (type === 'second') {
      if (refData) {
        refData.secondRef = ref;
      } else {
        recordRef.push({
          id: record[dragKey], firstRef: {}, secondRef: ref, thirdRef: {},
        });
      }
    } else if (type === 'third') {
      if (refData) {
        refData.thirdRef = ref;
      } else {
        recordRef.push({
          id: record[dragKey], firstRef: {}, secondRef: {}, thirdRef: ref,
        });
      }
    }
  };
  /**
* 自动聚焦新创建步骤第一框框
* @param {*} ref
*/
  const AutoEnterFirstRef = (record, index, ref) => {
    // eslint-disable-next-line no-param-reassign
    const refData = findRefByID(record[dragKey]);
    if (refData) {
      refData.firstRef = ref;
    } else {
      recordRef.push({
        id: record[dragKey], firstRef: ref, secondRef: {}, thirdRef: {},
      });
    }

    if (record.stepIsCreating) {
      setTimeout(() => {
        ref.enterEditing();
        // recordTab.push()
      });
    }
  };

  function render() {
    const {
      disabled,
    } = props;
    const hasStepIsCreating = data.some((item) => item.stepIsCreating);

    const columns = [{
      title: null,
      dataIndex: 'stepId',
      key: 'stepId',
      flex: 0.3,
      render(stepId, record, index) {
        return index + 1;
      },
    }, {
      title: <FormattedMessage id="execute_testStep" defaultMessage="测试步骤" />,
      dataIndex: 'testStep',
      key: 'testStep',
      flex: 2.4,
      render: (testStep, record, index) => {
        const { stepIsCreating } = record;
        return (
          <TextEditToggle
            simpleMode
            saveRef={AutoEnterFirstRef.bind(this, record, index)}
            originData={testStep}
            formKey="testStep"
            style={{ marginLeft: '-5px' }}
            onSubmit={(value) => {
              if (value) {
                handleEditStep({
                  ...record,
                  testStep: value,
                }, index);
              } else {
                Choerodon.prompt('测试步骤为必输项');
              }
            }}
          >
            <Text>
              {(newValue) => (
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
              <TextArea
                className="hidden-label"
                maxLength={500}
                autosize
                autoFocus
                placeholder="测试步骤"
                onKeyDown={(e) => {
                  if (e.keyCode === 9) {
                    setTimeout(() => {
                      const refs = findRefByID(record[dragKey]);
                      refs.firstRef.handleSubmit();
                      refs.secondRef.enterEditing();
                    });
                  }
                }}
              />
            </Edit>
          </TextEditToggle>
        );
      },
    }, {
      title: <FormattedMessage id="execute_testData" defaultMessage="测试数据" />,
      dataIndex: 'testData',
      key: 'testData',
      flex: 2,
      render: (testData, record, index) => {
        const { stepIsCreating } = record;
        return (
          <TextEditToggle
            simpleMode
            style={{ marginLeft: '-5px' }}
            originData={testData}
            saveRef={saveCreateRef.bind(this, record, index, 'second')}
            formKey="testData"
            onSubmit={(value) => {
              handleEditStep({
                ...record,
                testData: value,
              }, index);
            }}
          >
            <Text>
              {(newValue) => (
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
              <TextArea
                className="hidden-label"
                maxLength={500}
                autoFocus
                autosize
                onKeyDown={(e) => {
                  if (e.keyCode === 9) {
                    setTimeout(() => {
                      const refs = findRefByID(record[dragKey]);
                      refs.secondRef.handleSubmit();
                      refs.thirdRef.enterEditing();
                    });
                  }
                }}
              />
            </Edit>
          </TextEditToggle>
        );
      },
    }, {
      title: <FormattedMessage id="execute_expectedOutcome" defaultMessage="预期结果" />,
      dataIndex: 'expectedResult',
      key: 'expectedResult',
      flex: 2.4,
      render: (expectedResult, record, index) => {
        const { stepIsCreating } = record;
        return (
          <TextEditToggle
            simpleMode
            style={{ marginLeft: '-5px' }}
            originData={expectedResult}
            saveRef={saveCreateRef.bind(this, record, index, 'third')}
            formKey="expectedResult"
            onSubmit={(value) => {
              if (value) {
                handleEditStep({
                  ...record,
                  expectedResult: value,
                }, index);
              } else {
                Choerodon.prompt('测试结果为必输项');
              }
            }}
          >
            <Text>
              {(newValue) => (
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
              <TextArea
                className="hidden-label"
                maxLength={500}
                autoFocus
                autosize
                placeholder="预期结果"
              />
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
            <Tooltip title={<FormattedMessage id="execute_move" defaultMessage="移动" />}>
              <Icon type="open_with" {...provided.dragHandleProps} style={{ marginRight: 7 }} />
            </Tooltip>
            <Tooltip title={<FormattedMessage id="execute_copy" defaultMessage="复制" />}>
              <Button disabled={disabled} shape="circle" funcType="flat" icon="library_books" style={{ color: 'black' }} onClick={() => onCloneStep(record.stepId, index)} />
            </Tooltip>
            <Button disabled={disabled} shape="circle" funcType="flat" icon="delete_forever" style={{ color: 'black' }} onClick={() => handleDeleteStep(index, record.stepId)} />
          </div>
        ) : (
          <div>
            <div {...provided.dragHandleProps} />
            <Tooltip title={<FormattedMessage id="excute_save" defaultMessage="保存" />}>
              <Button disabled={disabled} shape="circle" funcType="flat" icon="done" style={{ margin: '0 -5px 5px', color: 'black' }} onClick={() => onCreateStep(record, index)} />
            </Tooltip>
            <Tooltip title={<FormattedMessage id="excute_cancel" defaultMessage="取消" />}>
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
          dragKey={dragKey}
          customDragHandle
          scroll={{ x: true }}
        />
        <div style={{ marginLeft: 3, marginTop: 10, position: 'relative' }}>
          <Button
            disabled={disabled || hasStepIsCreating}
            icon="playlist_add"
            onClick={handleAddCreating}
          >
            <FormattedMessage id="issue_edit_addTestDetail" defaultMessage="添加步骤" />
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
