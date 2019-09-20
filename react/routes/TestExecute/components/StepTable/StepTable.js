import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import {
  Table, Input, Icon, Select, Tooltip, Button,
} from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import _ from 'lodash';
import { UploadButtonNow } from '@choerodon/agile/lib/components/CommonComponent';
import { editCycleStep, addDefects } from '../../../../api/ExecuteDetailApi';
import {
  TextEditToggle, UploadInTable, DefectSelect, StatusTags,
} from '../../../../components';
import { delta2Text } from '../../../../common/utils';
import './StepTable.less';
import ExecuteDetailStore from '../../stores/ExecuteDetailStore';

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

  // 约束附件名长度
  limitAttachmentLength(text, length = 5) {
    const name = text.substring(0, text.indexOf('.'));
    const suffix = text.substring(text.indexOf('.'));
    const ellipsis = '···';
    const nameArr = [...name];
    return nameArr.length > length ? nameArr.slice(0, length).join('') + ellipsis + suffix : text;
  }

  getFileList = attachments => attachments.map((attachment) => {
    const attachmentName = this.limitAttachmentLength(attachment.attachmentName);
    const {
      attachmentLinkId, attachmentType, comment, id, objectVersionNumber, url,
    } = attachment;
    return {
      name: attachment.attachmentName,
      attachmentName,
      attachmentLinkId,
      attachmentType,
      comment,
      id,
      objectVersionNumber,
      url,
    };
  });

  render() {
    const that = this;
    const { stepStatusList, dataSource, disabled } = this.props;
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
    const columns = [
      {
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
      },
      {
        title: <FormattedMessage id="execute_testStep" />,
        dataIndex: 'testStep',
        key: 'testStep',
        width: 190,
        render: testStep => (
          <div
            className="c7ntest-text-wrap"
          >
            {testStep}
          </div>
        ),
      },
      {
        title: <FormattedMessage id="execute_testData" />,
        dataIndex: 'testData',
        key: 'testData',
        width: 190,
        render: testData => (
          <div
            className="c7ntest-text-wrap"
          >
            {testData}
          </div>
        ),
      },
      {
        title: <FormattedMessage id="execute_expectedOutcome" />,
        dataIndex: 'expectedResult',
        key: 'expectedResult',
        width: 190,
        render: expectedResult => (
          <div
            className="c7ntest-text-wrap"
          >
            {expectedResult}
          </div>
        ),
      },
      // 状态
      {
        title: <FormattedMessage id="execute_stepStatus" />,
        dataIndex: 'stepStatus',
        key: 'stepStatus',
        width: 100,
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
      },
      // 附件
      {
        title: <FormattedMessage id="attachment" />,
        dataIndex: 'stepAttachment',
        key: 'caseAttachment',
        width: 190,
        render(stepAttachment, record) {
          return (
            <UploadInTable
              fileList={that.getFileList(stepAttachment.filter(attachment => attachment.attachmentType === 'CYCLE_STEP'))}
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
      // 缺陷
      {
        title: <FormattedMessage id="bug" />,
        dataIndex: 'defects',
        key: 'defects',
        width: 190,
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
            originData={{ defects }}
            onCancel={ExecuteDetailStore.loadDetailList}
          >
            <Text>
              {
                // eslint-disable-next-line no-nested-ternary
                defects.length > 0 ? (
                  <div>
                    {defects.map((defect, i) => (
                      <div
                        key={defect.id}
                        style={{
                          fontSize: '13px',
                        }}
                      >
                        {defect.issueInfosVO && defect.issueInfosVO.issueName}
                      </div>
                    ))}
                  </div>
                ) : (
                  disabled
                    ? null : (
                      <div
                        style={{
                          width: 100,
                          height: 20,
                          color: '#3f51b5',
                        }}
                      >
                          添加缺陷
                      </div>
                    )
                )
              }
            </Text>
            <Edit>
              <div onScroll={(e) => {
                e.stopPropagation();
              }}
              >
                <DefectSelect
                  defaultOpen
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
      width: 100,
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
    const newColumns = !disabled && dataSource.length > 0 ? [...columns, actionColumn] : columns;
    return (
      <div
        className="StepTable"
      >
        <Table
          rowKey="executeStepId"
          filterBar={false}
          dataSource={dataSource}
          // columns={visibleAction ? [...columns, actionColumn] : columns}
          columns={newColumns}
          pagination={false}
          scroll={{ x: 1300, y: 400 }}
        />
      </div>
    );
  }
}

StepTable.propTypes = propTypes;
export default StepTable;
