import React, { Component } from 'react';
import {
  Input, Icon, Modal, Tooltip, Button, 
} from 'choerodon-ui';
import { stores } from 'choerodon-front-boot';
import _ from 'lodash';
import { FormattedMessage } from 'react-intl';
import {
  cloneStep, updateStep, deleteStep, createIssueStep,
} from '../../../api/IssueManageApi';
import { uploadFile } from '../../../api/FileApi';
import { DragTable } from '../../CommonComponent';
import { TextEditToggle, UploadInTable } from '../../CommonComponent';
import UploadButton from '../CommonComponent/UploadButton';
import './TestStepTable.scss';

const { confirm } = Modal;
const { Text, Edit } = TextEditToggle;
const { AppState } = stores;
const { TextArea } = Input;
let didUpdateFlag = false;
let didCreatedFlag = false;
let createStepId;
class TestStepTable extends Component {
  constructor(props) {
    super(props);
    this.state = {
      data: [],
      isEditing: [],
      createStep: {
        testStep: '',
        testData: '',
        expectedResult: '',
      },
      lastValue: '',
      fileList: [],
      createdStepInfo: {

      },
    };
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.data !== this.props.data && nextProps.data) {
      this.setState({
        data: nextProps.data,
        isEditing: _.map(nextProps.data, (item, index) => (
          {
            stepId: item.stepId,
            index,
            isStepNameEditing: false,
            isStepDataEditing: false,
            isStepExpectedResultEditing: false,
          }
        )),
        lastValue: '',
      });
    }
  }

  componentWillUpdate() {
    didUpdateFlag = false;
  }

  componentDidUpdate() {
    didUpdateFlag = true;
  }

  onDragEnd = (sourceIndex, targetIndex) => {
    const arr = this.state.data.slice();
    if (sourceIndex === targetIndex) {
      return;
    }
    const drag = arr[sourceIndex];
    arr.splice(sourceIndex, 1);
    arr.splice(targetIndex, 0, drag);
    this.setState({ data: arr });
    // arr此时是有序的，取toIndex前后两个的rank
    const lastRank = targetIndex === 0 ? null : arr[targetIndex - 1].rank;
    const nextRank = targetIndex === arr.length - 1 ? null : arr[targetIndex + 1].rank;
    const dragCopy = { ...drag };
    delete dragCopy.attachments;
    const testCaseStepDTO = {
      ...dragCopy,
      lastRank,
      nextRank,
    };
    updateStep(testCaseStepDTO).then((res) => {
      // save success
      const Data = [...this.state.data];
      Data[targetIndex] = res;
      this.setState({
        data: Data,
      });
    });
  }

  setFileList = (data) => {
    this.setState({ fileList: data });
  }

  handleFileUpload = (propFileList) => {
    if (propFileList.length) {
      const fileList = propFileList.filter(i => !i.url);
      const config = {
        attachmentLinkId: createStepId,
        attachmentType: 'CASE_STEP',
      };
      if (fileList.some(one => !one.url)) {
        const fileList = propFileList.filter(i => !i.url);
        const formData = new FormData();
        fileList.forEach((file) => {
          // file.name = encodeURI(encodeURI(file.name));
          formData.append('file', file);
        });
        if (createStepId) {
          uploadFile(formData, config).then((res) => {
            if (res.failed) {
              this.props.leaveLoad();
              Choerodon.prompt('不能有重复附件');
            } else {
              createStepId = undefined;
              this.props.onOk();
            }
          }).catch((error) => {
            window.console.log(error);
            this.props.leaveLoad();
            Choerodon.prompt('网络错误');
          });
        }
        return false;
      }
    } else {
      this.props.onOk();
    }
  }

  handleClickCreate = () => {
    const { issueId, data } = this.props;
    const { isEditing } = this.state;
    const lastRank = data.length
      ? data[data.length - 1].rank : null;
    const testCaseStepDTO = {
      attachments: [],
      issueId,
      lastRank,
      nextRank: null,
      testStep: '',
      testData: '',
      expectedResult: '',
      stepIsCreating: true,
    };
    didCreatedFlag = false;
    this.setState({
      data: [...data, testCaseStepDTO],
      isEditing: [...isEditing, {
        stepId: undefined,
        index: data.length,
        isStepNameEditing: false,
        isStepDataEditing: false,
        isStepExpectedResultEditing: false,
      }],
      createStep: {
        testStep: '',
        testData: '',
        expectedResult: '',
      },
      fileList: [],
      createdStepInfo: {},
      lastValue: '',
    });
  }

  createIssueStep = () => {
    const { issueId, data } = this.props;
    const { createStep, fileList } = this.state;
    const { expectedResult, testStep } = createStep;
    if (expectedResult && testStep) {
      const lastRank = data.length
        ? data[data.length - 1].rank : null;
      const testCaseStepDTO = {
        issueId,
        lastRank,
        nextRank: null,
        ...createStep,
      };
      if (!didCreatedFlag) {
        didCreatedFlag = true;
        createIssueStep(testCaseStepDTO).then((res) => {
          createStepId = res.stepId;
          this.setState({
            createdStepInfo: res,
          });
          this.handleFileUpload(fileList);
        });
      } else {
        setTimeout(() => {
          let { createdStepInfo, createStep, lastValue } = this.state;
          createdStepInfo = {
            ...createdStepInfo,
            ...createStep,
            objectVersionNumber: createdStepInfo.objectVersionNumber || 1,
          };
          this.editStep(createdStepInfo, this.handleFileUpload.bind(this, fileList));
        }, 300);
      }
    } else {
      Choerodon.prompt('测试步骤和预期结果均为必输项');
    }
  }

  editStep = (record, func) => {
    updateStep(record).then((res) => {
      if (func) {
        func();
      } else {
        this.props.onOk();
      }
    });
  };

  cloneStep = (stepId, index) => {
    const { data } = this.state;
    const lastRank = data[index].rank;
    const nextRank = data[index + 1] ? data[index + 1].rank : null;
    this.props.enterLoad();
    cloneStep({
      lastRank,
      nextRank,
      stepId,
      issueId: this.props.issueId,
    }).then((res) => {
      this.props.onOk();
    })
      .catch((error) => {
        this.props.leaveLoad();
      });
  }

  handleDeleteTestStep = (stepId) => {
    const that = this;
    confirm({
      width: 560,
      title: Choerodon.getMessage('确认删除吗？', 'Confirm delete'),
      content:
  <div style={{ marginBottom: 32 }}>
    {Choerodon.getMessage('当你点击删除后，所有与之关联的测试步骤将删除!', 'When you click delete, after which the data will be permanently deleted and irreversible!')
          }
  </div>,
      onOk() {
        return deleteStep({ data: { stepId } })
          .then((res) => {
            that.props.onOk();
          });
      },
      onCancel() { },
      okText: Choerodon.getMessage('删除', 'Delete'),
      okType: 'danger',
    });
  }

  renderStepName = (record) => {
    const { disabled } = this.props;
    const { isEditing, createStep } = this.state;
    if (disabled) {
      return (
        <span>{record.testStep}</span>
      );
    } else {
      const editingStepIndex = _.find(isEditing, item => item.stepId === record.stepId) ? _.find(isEditing, item => item.stepId === record.stepId).index : -1;
      if (editingStepIndex !== -1) {
        return isEditing[editingStepIndex].isStepNameEditing ? (
          <TextArea
            ref={(testStep) => {
              this[`testStep${record.stepId}`] = testStep;
            }}
            autosize
            placeholder="测试步骤"
            onBlur={e => this.handleBlurOrEnter(e, record, 'testStep')}
            defaultValue={record.stepIsCreating ? createStep.testStep : record.testStep}
          />
        ) : (
          <span className="preWrapSpan" style={{ color: record.stepIsCreating && !createStep.testStep ? '#bfbfbf' : '#000' }}>{record.stepIsCreating ? (createStep.testStep ? createStep.testStep : '测试步骤') : record.testStep}</span>
        );
      }
    }
  }

  renderStepTestData = (record) => {
    const { disabled } = this.props;
    const { isEditing, createStep } = this.state;
    if (disabled) {
      return (
        <span>{record.testData ? record.testData : '-'}</span>
      );
    } else {
      const editingStepIndex = _.find(isEditing, item => item.stepId === record.stepId) ? _.find(isEditing, item => item.stepId === record.stepId).index : -1;
      if (editingStepIndex !== -1) {
        return isEditing[editingStepIndex].isStepDataEditing ? (
          <TextArea 
            ref={(testData) => {
              this[`testData${record.stepId}`] = testData;
            }}
            autosize
            onBlur={e => this.handleBlurOrEnter(e, record, 'testData')}
            defaultValue={record.stepIsCreating ? createStep.testData : record.testData}
            placeholder="测试数据"
          />
        ) : (
          <span className="preWrapSpan" style={{ color: record.stepIsCreating && !createStep.testData ? '#bfbfbf' : '#000' }}>{record.stepIsCreating ? (createStep.testData ? createStep.testData : '测试数据') : (record.testData ? record.testData : '-')}</span>
        );
      }
    }
  }

  renderStepExpectedResult = (record) => {
    const { disabled } = this.props;
    const { isEditing, createStep } = this.state;
    if (disabled) {
      return (
        <span>{record.expectedResult}</span>
      );
    } else {
      const editingStepIndex = _.find(isEditing, item => item.stepId === record.stepId) ? _.find(isEditing, item => item.stepId === record.stepId).index : -1;
      if (editingStepIndex !== -1) {
        return isEditing[editingStepIndex].isStepExpectedResultEditing ? (
          <TextArea 
            ref={(expectedResult) => {
              this[`expectedResult${record.stepId}`] = expectedResult;
            }}
            autosize
            onBlur={e => this.handleBlurOrEnter(e, record, 'expectedResult')}
            defaultValue={record.stepIsCreating ? createStep.expectedResult : record.expectedResult}
            placeholder="预期结果"
          />
        ) : (
          <span className="preWrapSpan" style={{ color: record.stepIsCreating && !createStep.expectedResult ? '#bfbfbf' : '#000' }}>{record.stepIsCreating ? (createStep.expectedResult ? createStep.expectedResult : '预期结果') : record.expectedResult}</span>
        );
      }
    }
  }


  handleBlurOrEnter = (e, record, editField) => {
    const { createStep, isEditing, lastValue } = this.state;

    if (!record.stepIsCreating) {
      record[editField] = e.target.value;
      const { expectedResult, testStep } = record;
      if (expectedResult !== '' && testStep !== '' && lastValue !== e.target.value) {
        this.editStep({ ...record, [editField]: e.target.value });
      } else if (expectedResult == '' || testStep == '') {
        const editingStepIndex = _.find(isEditing, item => item.stepId === record.stepId).index;
        isEditing[editingStepIndex][`is${_.upperFirst(editField)}Editing`] = true;
        this.setState({
          isEditing,
        });
        Choerodon.prompt('测试步骤和预期结果均为必输项');
      }
    } else {
      isEditing[isEditing.length - 1][`is${_.upperFirst(editField)}Editing`] = false;
      this.setState({
        isEditing,
      });
      this.setState({
        createStep: {
          ...createStep,
          [editField]: e.target.value,
        },
      });
    }
  }

  handleFieldOnClick = (e, record, editField) => {
    const { disabled } = this.props;
    const { isEditing } = this.state;
    if (!disabled) {
      // let fieldClicked = e.target;
      // let fieldClickedParent = fieldClicked.parentNode;
      _.forEach(isEditing, (ele) => {
        ele.isStepNameEditing = false;
        ele.isStepDataEditing = false;
        ele.isStepExpectedResultEditing = false;
      });
      const editingStepIndex = _.find(isEditing, item => item.stepId === record.stepId).index;
      // isEditing[editingStepIndex].isStepNameEditing = true,
      isEditing[editingStepIndex][`is${_.upperFirst(editField)}Editing`] = true,

      this.setState({
        isEditing,
      });

      setTimeout(() => {
        if (didUpdateFlag) {
          const fieldClicked = e.target;
          const fieldClickedParent = fieldClicked.parentNode;
          if (fieldClicked.tagName === 'DIV') {
            fieldClicked.getElementsByTagName('textArea')[0].focus();
          } else {
            fieldClickedParent.getElementsByTagName('textArea')[0].focus();
          }
        }
      }, 300);
    }
    e.stopPropagation();
  }


  cancelCreateStep = (index) => {
    const { data } = this.state;
    const cancelStep = _.remove(data, (item, i) => index === i);
    this.setState({
      data,
      createStep: {
        testStep: '',
        testData: '',
        expectedResult: '',
      },
    });
  }

  render() {
    const {
      onOk, enterLoad, leaveLoad, disabled,
    } = this.props;

    const {
      isEditing, data, createStep, fileList,
    } = this.state;

    const hasStepIsCreating = data.find(item => item.stepIsCreating);

    const columns = [{
      title: null,
      dataIndex: 'stepId',
      key: 'stepId',
      flex: 1,
      width: 10,
      render(stepId, record, index) {
        return index + 1;
      },
    }, {
      title: <FormattedMessage id="execute_testStep" />,
      dataIndex: 'testStep',
      key: 'testStep',
      flex: 2,
      render: (testStep, record) => (
        <div
          role="none"
          className="item-container"
          style={{ cursor: disabled ? 'auto' : 'pointer' }}
          // onClick={e => this.handleFieldOnClick(e, record, 'testStep')}
          onClick={(e) => {
            if (!disabled) {
              const fieldClicked = e.target;
              const fieldClickedParent = fieldClicked.parentNode;
              _.forEach(isEditing, (ele) => {
                ele.isStepNameEditing = false;
                ele.isStepDataEditing = false;
                ele.isStepExpectedResultEditing = false;
              });
              const editingStepIndex = _.find(isEditing, item => item.stepId === record.stepId).index;
              isEditing[editingStepIndex].isStepNameEditing = true,
              this.setState({
                isEditing,
                lastValue: record.stepIsCreating ? createStep.testStep : record.testStep,
              });

              setTimeout(() => {
                if (fieldClicked.tagName === 'DIV') {
                  fieldClicked.getElementsByTagName('textArea')[0].focus();
                } else {
                  fieldClickedParent.getElementsByTagName('textArea')[0].focus();
                }
              }, 100);
            }
            e.stopPropagation();
          }
          }
        >
          {
            this.renderStepName(record)
          }
        </div>
      ),
    }, {
      title: <FormattedMessage id="execute_testData" />,
      dataIndex: 'testData',
      key: 'testData',
      flex: 2,
      render: (testData, record) => (
        <div
          role="none"
          className="item-container"
          style={{ cursor: disabled ? 'auto' : 'pointer' }}
          onClick={(e) => {
            if (!disabled) {
              const fieldClicked = e.target;
              const fieldClickedParent = fieldClicked.parentNode;
              _.forEach(isEditing, (ele) => {
                ele.isStepNameEditing = false;
                ele.isStepDataEditing = false;
                ele.isStepExpectedResultEditing = false;
              });
              const editingStepIndex = _.find(isEditing, item => item.stepId === record.stepId).index;
              isEditing[editingStepIndex].isStepDataEditing = true,
              this.setState({
                isEditing,
                lastValue: record.stepIsCreating ? createStep.testData : record.testData,
              });

              setTimeout(() => {
                if (fieldClicked.tagName === 'DIV') {
                  fieldClicked.getElementsByTagName('textArea')[0].focus();
                } else {
                  fieldClickedParent.getElementsByTagName('textArea')[0].focus();
                }
              }, 100);
            }
            e.stopPropagation();
          }}
        >
          {
            this.renderStepTestData(record)
          }
        </div>
      ),
    }, {
      title: <FormattedMessage id="execute_expectedOutcome" />,
      dataIndex: 'expectedResult',
      key: 'expectedResult',
      flex: 2,
      render: (expectedResult, record) => (
        <div
          role="none"
          className="item-container"
          style={{ cursor: disabled ? 'auto' : 'pointer' }}
          onClick={(e) => {
            if (!disabled) {
              const fieldClicked = e.target;
              const fieldClickedParent = fieldClicked.parentNode;
              _.forEach(isEditing, (ele) => {
                ele.isStepNameEditing = false;
                ele.isStepDataEditing = false;
                ele.isStepExpectedResultEditing = false;
              });
              const editingStepIndex = _.find(isEditing, item => item.stepId === record.stepId).index;
              isEditing[editingStepIndex].isStepExpectedResultEditing = true,
              this.setState({
                isEditing,
                lastValue: record.stepIsCreating ? createStep.expectedResult : record.expectedResult,
              });
              setTimeout(() => {
                if (fieldClicked.tagName === 'DIV') {
                  fieldClicked.getElementsByTagName('textArea')[0].focus();
                } else {
                  fieldClickedParent.getElementsByTagName('textArea')[0].focus();
                }
              }, 100);
            }
            e.stopPropagation();
          }}
        >
          {
            this.renderStepExpectedResult(record)
          }
        </div>
      ),
    }, {
      title: <FormattedMessage id="execute_stepAttachment" />,
      dataIndex: 'attachments',
      key: 'attachments',
      flex: 2,
      className: 'attachmentsColumn',
      render: (attachments, record) => (
        <div className="item-container item-container-upload">
          {record.stepIsCreating ? (
            <UploadButton
              className="createUploadBtn"
              onRemove={this.setFileList}
              onBeforeUpload={this.setFileList}
              fileList={fileList}
            />
          ) : (
            <UploadInTable
              fileList={attachments}
              onOk={onOk}
              enterLoad={enterLoad}
              leaveLoad={leaveLoad}
              config={{
                attachmentLinkId: record.stepId,
                attachmentType: 'CASE_STEP',
              }}
            />
          )}
        </div>
      ),
    }, {
      title: null,
      dataIndex: 'action',
      key: 'action',
      flex: 2,
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
              <Button disabled={disabled} shape="circle" funcType="flat" icon="library_books" style={{ color: 'black' }} onClick={() => this.cloneStep(record.stepId, index)} />
            </Tooltip>
            <Button disabled={disabled} shape="circle" funcType="flat" icon="delete_forever" style={{ color: 'black' }} onClick={() => this.handleDeleteTestStep(record.stepId)} />
          </div>
        ) : (
          <div>
            <div {...provided.dragHandleProps} />
            <Tooltip title={<FormattedMessage id="excute_save" />}>
              <Button disabled={disabled} shape="circle" funcType="flat" icon="done" style={{ margin: '0 -5px 5px', color: 'black' }} onClick={() => this.createIssueStep()} />
            </Tooltip>
            <Tooltip title={<FormattedMessage id="excute_cancel" />}>
              <Button disabled={disabled} shape="circle" funcType="flat" icon="close" style={{ margin: '0 5px', color: 'black' }} onClick={() => this.cancelCreateStep(index)} />
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
          dataSource={this.state.data}
          columns={columns}
          onDragEnd={this.onDragEnd}
          dragKey="stepId"
          customDragHandle
        />
        <div style={{ marginLeft: 3, marginTop: 10, position: 'relative' }}>
          <Button disabled={disabled || hasStepIsCreating} style={{ color: disabled || hasStepIsCreating ? '#bfbfbf' : '#3F51B5' }} icon="playlist_add" className="leftBtn" funcTyp="flat" onClick={this.handleClickCreate}>
            <FormattedMessage id="issue_edit_addTestDetail" />
          </Button>
        </div>
      </div>
    );
  }
}

export default TestStepTable;
