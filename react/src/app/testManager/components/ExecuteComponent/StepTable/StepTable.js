import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import {
  Table, Input, Icon, Select, Tooltip, Button,
} from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import _ from 'lodash';
import { editCycleStep, addDefects } from '../../../api/ExecuteDetailApi';
import {
  TextEditToggle, UploadInTable, DefectSelect, StatusTags,
} from '../../CommonComponent';
import { delta2Text } from '../../../common/utils';
import './StepTable.scss';
import ExecuteDetailStore from '../../../store/project/TestExecute/ExecuteDetailStore';

const { TextArea } = Input;
const { Option } = Select;
const { Text, Edit } = TextEditToggle;
const propTypes = {
  disabled: PropTypes.bool,
  dataSource: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
  stepStatusList: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
};
class StepTable extends PureComponent {
  editCycleStep = (values) => {
    const data = { ...values };
    delete data.defects;
    delete data.caseAttachment;
    delete data.stepAttachment;
    editCycleStep([data]).then(() => {
      ExecuteDetailStore.loadDetailList();
    }).catch((error) => {
      window.console.log(error);
      Choerodon.prompt('网络错误');
    });
  };

  quickPassOrFail = (stepData, text) => {
    const { stepStatusList } = this.props;
    const data = { ...stepData };
    if (_.find(stepStatusList, { projectId: 0, statusName: text })) {
      data.stepStatus = _.find(stepStatusList, { projectId: 0, statusName: text }).statusId;
      delete data.defects;
      delete data.caseAttachment;
      delete data.stepAttachment;
      data.assignedTo = data.assignedTo || 0;
      editCycleStep([data]).then((Data) => {
        ExecuteDetailStore.loadDetailList();
      }).catch((error) => {
        window.console.log(error);
        Choerodon.prompt('网络错误');
      });
    } else {
      Choerodon.prompt('未找到对应状态');
    }
  };

  quickPass(stepData, e) {
    e.stopPropagation();
    this.quickPassOrFail(stepData, '通过');
  }

  quickFail(stepData, e) {
    e.stopPropagation();
    this.quickPassOrFail(stepData, '失败');
  }


  render() {
    const that = this;
    const { disabled, stepStatusList, dataSource } = this.props;
    const options = stepStatusList.map((status) => {
      const { statusName, statusId, statusColor } = status;
      return (
        <Option value={statusId} key={statusId} title={statusName}>
          <StatusTags
            color={statusColor}
            name={statusName}
          />
        </Option>
      );
    });
    const columns = [{
      title: '步骤',
      dataIndex: 'order',
      key: 'order',
      width: 60,
      render: (order, record, index) => (
        <div
          className="c7ntest-text-wrap"
        >
          {index + 1}
        </div>
      ),
    }, {
      title: <FormattedMessage id="execute_testStep" />,
      dataIndex: 'testStep',
      key: 'testStep',
      render: testStep => (
        <div
          className="c7ntest-text-wrap"
        >
          {testStep}
        </div>
      ),
    }, {
      title: <FormattedMessage id="execute_testData" />,
      dataIndex: 'testData',
      key: 'testData',
      render: testData => (
        <div
          className="c7ntest-text-wrap"
        >
          {testData}
        </div>
      ),
    }, {
      title: <FormattedMessage id="execute_expectedOutcome" />,
      dataIndex: 'expectedResult',
      key: 'expectedResult',
      render: expectedResult => (
        <div
          className="c7ntest-text-wrap"
        >
          {expectedResult}
        </div>
      ),
    },
    {
      title: <FormattedMessage id="execute_stepStatus" />,
      dataIndex: 'stepStatus',
      key: 'stepStatus',
      render(stepStatus, record) {
        return (
          <div style={{ width: 85 }}>
            <TextEditToggle
              noButton
              disabled={disabled}
              formKey="stepStatus"
              onSubmit={value => that.editCycleStep({ ...record, stepStatus: value })}
              originData={stepStatus}
            >
              <Text>
                {(data) => {
                  const targetStatus = _.find(stepStatusList, { statusId: data });
                  const statusColor = targetStatus && targetStatus.statusColor;
                  const statusName = targetStatus && targetStatus.statusName;
                  return (
                    <StatusTags
                      color={statusColor}
                      name={statusName}
                    />
                  );
                }}

              </Text>
              <Edit>
                <Select
                  autoFocus
                  defaultOpen
                  getPopupContainer={() => document.getElementsByClassName('StepTable')[0]}
                >
                  {options}
                </Select>
              </Edit>
            </TextEditToggle>
          </div>
        );
      },
    }, {
      title: <FormattedMessage id="execute_stepAttachment" />,
      dataIndex: 'stepAttachment',
      key: 'stepAttachment',
      render(stepAttachment) {
        return (
          <div>
            {stepAttachment.filter(attachment => attachment.attachmentType === 'CASE_STEP').length > 0 ? stepAttachment.filter(attachment => attachment.attachmentType === 'CASE_STEP').map(attachment => (
              <div style={{
                display: 'flex', fontSize: '12px', flexShrink: 0, margin: '5px 2px', alignItems: 'center',
              }}
              >
                <Icon type="attach_file" style={{ fontSize: '12px', color: 'rgba(0,0,0,0.65)' }} />
                <a className="c7ntest-text-dot" style={{ margin: '2px 5px', fontSize: '13px' }} href={attachment.url} target="_blank" rel="noopener noreferrer">{attachment.attachmentName}</a>
              </div>
            )) : '-'
            }
          </div>
        );
      },
    }, {
      title: <FormattedMessage id="execute_comment" />,
      dataIndex: 'comment',
      key: 'comment',
      render(comment, record) {
        return (
          <TextEditToggle
            noButton
            disabled={disabled}
            formKey="comment"
            onSubmit={(value) => { that.editCycleStep({ ...record, comment: value }); }}
            originData={delta2Text(comment)}
          >
            <Text>
              {data => (
                <div
                  className="c7ntest-text-wrap"
                  style={{ minHeight: 20 }}
                >
                  {delta2Text(data) === '' ? '-' : delta2Text(data)}
                </div>
              )}
            </Text>
            <Edit>
              <TextArea autosize autoFocus />
            </Edit>
          </TextEditToggle>
        );
      },
    }, {
      title: <FormattedMessage id="attachment" />,
      dataIndex: 'stepAttachment',
      key: 'caseAttachment',
      render(stepAttachment, record) {
        return (
          <UploadInTable
            fileList={stepAttachment.filter(attachment => attachment.attachmentType === 'CYCLE_STEP')}
            onOk={ExecuteDetailStore.loadDetailList}
            enterLoad={ExecuteDetailStore.enterloading}
            leaveLoad={ExecuteDetailStore.unloading}
            config={{
              attachmentLinkId: record.executeStepId,
              attachmentType: 'CYCLE_STEP',
            }}
          />
        );
      },
    },
    {
      title: <FormattedMessage id="bug" />,
      dataIndex: 'defects',
      key: 'defects',
      render: (defects, record) => (
        <TextEditToggle
          noButton
          saveRef={(bugsToggle) => { this[`bugsToggle_${record.stepId}`] = bugsToggle; }}
          disabled={disabled}
          onSubmit={() => {
            if (that.needAdd.length > 0) {
              ExecuteDetailStore.enterloading();
              addDefects(that.needAdd).then((res) => {
                ExecuteDetailStore.loadDetailList();
              });
            } else {
              ExecuteDetailStore.loadDetailList();
            }
          }}
          // originData={{ defects }}
          onCancel={ExecuteDetailStore.loadDetailList}
        >
          <Text>
            {
              defects.length > 0 ? (
                <div>
                  {defects.map((defect, i) => (
                    <div
                      key={defect.id}
                      style={{
                        fontSize: '13px',
                      }}
                    >
                      {defect.issueInfosDTO && defect.issueInfosDTO.issueName}
                    </div>
                  ))}
                </div>
              ) : (
                <div
                  style={{
                    width: 100,
                    height: 20,
                  }}
                />
              )
            }
          </Text>
          <Edit>
            <div onScroll={(e) => {
              e.stopPropagation();
            }}
            >
              <DefectSelect
                getPopupContainer={() => document.getElementsByClassName('StepTable')[0]}               
                defects={defects}
                setNeedAdd={(needAdd) => { that.needAdd = needAdd; }}
                executeStepId={record.executeStepId}
                bugsToggleRef={this[`bugsToggle_${record.stepId}`]}
              />
            </div>
          </Edit>

        </TextEditToggle>
      ),
    },
    ];
    const actionColumn = {
      title: '',
      key: 'action',
      width: 90,
      fixed: 'right',
      render: (text, record) => (
        record.projectId !== 0
        && (
          <div style={{ display: 'flex' }}>
            <Tooltip title={<FormattedMessage id="execute_quickPass" />}>
              <Button disabled={disabled} shape="circle" funcType="flat" icon="check_circle" onClick={this.quickPass.bind(this, record)} />
            </Tooltip>
            <Tooltip title={<FormattedMessage id="execute_quickFail" />}>
              <Button disabled={disabled} shape="circle" funcType="flat" icon="cancel" onClick={this.quickFail.bind(this, record)} />
            </Tooltip>
          </div>
        )
      ),
    };
    return (
      <div
        className="StepTable"
      >
        <Table
          rowKey="executeStepId"
          filterBar={false}
          dataSource={dataSource}
          columns={disabled ? columns : [...columns, actionColumn]}
          pagination={false}
          scroll={{ x: 1300, y: 400 }}
        />
      </div>
    );
  }
}

StepTable.propTypes = propTypes;
export default StepTable;
