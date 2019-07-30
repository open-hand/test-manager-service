import React, { Component } from 'react';
import {
  Input, Icon, Modal, Tooltip, Button,
} from 'choerodon-ui';
import _ from 'lodash';
import { FormattedMessage } from 'react-intl';
import {
  cloneStep, updateStep, deleteStep, createIssueStep,
} from '../../../../api/IssueManageApi';
import { uploadFile } from '../../../../api/FileApi';
import { DragTable } from '../../../../components';
import { TextEditToggle, UploadInTable } from '../../../../components';
import UploadButton from '../CommonComponent/UploadButton';
import './TestStepTable.scss';

const { confirm } = Modal;
const { Text, Edit } = TextEditToggle;
const { TextArea } = Input;
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
      });
    }
  }


  onDragEnd = (sourceIndex, targetIndex) => {
    const thedata = this.state.data;
    const arr = thedata.slice();
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
      const thedata2 = this.state.data;
      const Data = [...thedata2];
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
        // eslint-disable-next-line no-shadow
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
    return false;
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
          let { createdStepInfo } = this.state;
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
                this.setState({
                  createStep: {
                    ...createStep,
                    testStep: value,
                  },
                });
              } else {
                this.editStep({
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
                    <span>
                      {newValue || <span className="preWrapSpan" style={{ color: 'rgb(191, 191, 191)' }}>测试步骤</span>}
                    </span>
                  )
                  : <span>{newValue || '-'}</span>
              )}
            </Text>
            <Edit>
              <TextArea autoFocus autosize placeholder="测试步骤" />
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
                this.setState({
                  createStep: {
                    ...createStep,
                    testData: value,
                  },
                });
              } else {
                this.editStep({
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
                    <span>
                      {newValue || <span className="preWrapSpan" style={{ color: 'rgb(191, 191, 191)' }}>测试数据</span>}
                    </span>
                  )
                  : <span>{newValue || '-'}</span>
              )}
            </Text>
            <Edit>
              <TextArea autoFocus autosize placeholder="测试数据" />
            </Edit>
          </TextEditToggle>
        );
      },
    }, {
      title: <FormattedMessage id="execute_expectedOutcome" />,
      dataIndex: 'expectedResult',
      key: 'expectedResult',
      flex: 2,
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
                this.setState({
                  createStep: {
                    ...createStep,
                    expectedResult: value,
                  },
                });
              } else {
                this.editStep({
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
                    <span>
                      {newValue || <span className="preWrapSpan" style={{ color: 'rgb(191, 191, 191)' }}>预期结果</span>}
                    </span>
                  )
                  : <span>{newValue || '-'}</span>
              )}
            </Text>
            <Edit>
              <TextArea autoFocus autosize placeholder="预期结果" />
            </Edit>
          </TextEditToggle>
        );
      },
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
