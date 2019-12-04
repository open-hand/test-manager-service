/* eslint-disable no-console */
import React, { PureComponent, useEffect, useState } from 'react';
import { Choerodon } from '@choerodon/boot';
import PropTypes, { func } from 'prop-types';
import {
  Input, Icon, Select, Tooltip,
} from 'choerodon-ui';
import { Button } from 'choerodon-ui/pro';
import { FormattedMessage } from 'react-intl';
import _ from 'lodash';
import { Table } from 'choerodon-ui/pro';
import { editCycleStep, addDefects, removeDefect } from '../../../../../../api/ExecuteDetailApi';
import { deleteAttachment } from '@/api/FileApi';
import './StepTable.less';
import {
  TextEditToggle, UploadInTable, StatusTags,
} from '../../../../../../components';
import DefectSelect from './DefectSelect';

const { Text, Edit } = TextEditToggle;
const { Column } = Table;
const { Option } = Select;

function StepTable(props) {
  const { dataSet, ExecuteDetailStore } = props;
  const [statusList, setStatusList] = useState([]);
  /**
   * 对当前页刷新
   */
  const onRefreshCurrent = () => {
    dataSet.query(dataSet.currentPage);
  };
  const onQuickPassOrFail = (code, record) => {
    const status = _.find(statusList, { projectId: 0, statusName: code });
    console.log('code', code, status);
    if (status) {
      record.set('stepStatus', status.statusId);
    } else {
      Choerodon.prompt('未找到对应状态');
    }
  };
  const handleDeleteFile = (record, value) => {
    console.log('value', value, record);
    deleteAttachment(value.id).then((data) => {
      onRefreshCurrent();
      Choerodon.prompt('删除成功');
    }).catch((error) => {
      Choerodon.prompt(`删除失败 ${error}`);
    });
  };

  function renderAction({ record }) {
    return (
      <React.Fragment>
        <Tooltip title={<FormattedMessage id="execute_quickPass" />}>
          <Button key="pass" disabled={false} shape="circle" funcType="flat" icon="check_circle" onClick={onQuickPassOrFail.bind(this, '通过', record)} />
        </Tooltip>
        <Tooltip title={<FormattedMessage id="execute_quickFail" />}>
          <Button key="fail" disabled={false} shape="circle" funcType="flat" icon="cancel" onClick={onQuickPassOrFail.bind(this, '失败', record)} />
        </Tooltip>
      </React.Fragment>
    );
  }
  // 约束附件名长度
  function limitAttachmentLength(text, length = 5) {
    const name = text.substring(0, text.indexOf('.'));
    const suffix = text.substring(text.indexOf('.'));
    const ellipsis = '···';
    const nameArr = [...name];
    return nameArr.length > length ? nameArr.slice(0, length).join('') + ellipsis + suffix : text;
  }

  const getFileList = attachments => attachments.map((attachment) => {
    const attachmentName = limitAttachmentLength(attachment.attachmentName);
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
  // 增加文件
  const onAddFile = (record, data) => {
    const fileList = record.get('stepAttachment');
    record.set('stepAttachment', [...fileList, ...data]);
  };
  function renderAttachment({ record, value }) {
    return (
      <UploadInTable
        fileList={getFileList(value.filter(attachment => attachment.attachmentType === 'CYCLE_STEP'))}
        onOk={onRefreshCurrent}
        handleUpdateFileList={onAddFile.bind(this, record)}
        handleDeleteFile={handleDeleteFile.bind(this, record)}
        config={{
          attachmentLinkId: record.get('executeStepId'),
          attachmentType: 'CYCLE_STEP',
        }}
      />
    );
  }

  const handleAddDefects = (record) => {
    // record.set('defects', record.get('tempDefects'));
    addDefects(record.get('tempDefects').map(i => i.issueInfosVO)).then(() => {
      onRefreshCurrent();
    });
    record.set('tempDefects', []);
  };
  const handleDeleteDefect = (defect, record) => {
    console.log('handleDeleteDefect:', defect, record);

    record.set('defects', _.filter(record.get('defects'), item => item.issueId !== defect.issueId));
  };
  /**
   * 渲染缺陷
   * @param {*} param0 
   */
  function renderDefects({ record, value: defects }) {
    const disabled = defects.length !== 0;
    return (
      <TextEditToggle
        noButton
        onSubmit={() => {
          handleAddDefects(record);
        }}
        originData={{ defects: defects.map(i => i) }}
      >
        <Text>
          {
            // eslint-disable-next-line no-nested-ternary
            defects.length > 0 ? (
              <ul className="c7n-test-execute-detail-step-table-defects">
                {defects.map((defect, i) => (
                  <li
                    // key={defect.id}
                    className="c7n-test-execute-detail-step-table-defects-option"

                  >
                    <div className="c7n-test-execute-detail-step-table-defects-option-text">{defect.issueInfosVO && `${defect.issueInfosVO.issueName} ${defect.issueInfosVO.summary}`}</div>
                    <span
                      role="none"
                      className="c7n-test-execute-detail-step-table-defects-option-btn"
                      onMouseDown={e => e.stopPropagation()}
                      onClick={handleDeleteDefect.bind(this, defect, record)}
                    >
                      <Icon
                        type="cancel"
                        style={{ float: 'right' }}
                      />
                    </span>
                  </li>
                ))}
              </ul>
            ) : (
              disabled
                ? null : (
                  <div
                    style={{
                      width: 100,
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
              getPopupContainer={() => document.getElementsByClassName('c7n-test-execute-detail-card-title')[0]}
              defects={defects}
              ExecuteDetailStore={ExecuteDetailStore}
              setNeedAdd={(needAdd) => { record.set('tempDefects', needAdd); }}
              record={record}
              executeStepId={record.get('executeStepId')}
              handleSubmit={handleAddDefects}
            />
          </div>
        </Edit>

      </TextEditToggle>
    );
  }
  /**
   * 渲染文字部分 无文字显示 -
   * @param {*} param0 
   */
  function renderText({ value }) {
    if (value) {
      return value;
    } else {
      return '-';
    }
  }
  function renderStatus({ value, record }) {
    // const { lookup: statusList } = record.getField('stepStatus').fetchLookup(res=>);
    const status = statusList.length === 0 ? {} : statusList.find(item => item.statusId === Number(value));
    const { statusName = '', statusColor = false } = status || {};

    return <StatusTags name={statusName} color={statusColor} style={{ lineHeight: '.16rem' }} />;
    // return value;
  }

  useEffect(() => {
    dataSet.getField('stepStatus').fetchLookup().then(res => setStatusList(res));
  }, [dataSet, setStatusList]);

  return (
    <Table dataSet={dataSet} queryBar="none" className="c7n-test-execute-detail-step-table" rowHeight="auto">
      <Column name="index" width={80} align="left" />
      <Column name="testStep" align="left" minWidth={200} tooltip="overflow" renderer={renderText} />
      <Column name="testData" align="left" minWidth={120} tooltip="overflow" renderer={renderText} />
      <Column name="expectedResult" align="left" minWidth={150} tooltip="overflow" renderer={renderText} />
      <Column name="stepStatus" width={70} renderer={renderStatus} />
      <Column name="stepAttachment" renderer={renderAttachment} align="left" width={200} className="c7n-test-execute-detail-step-table-file" headerClassName="c7n-test-execute-detail-step-table-file-head" footerClassName="c7n-test-execute-detail-step-table-file-foot" />
      <Column name="description" editor align="left" tooltip="overflow" renderer={renderText} />
      <Column name="defects" renderer={renderDefects} width={220} />
      <Column name="action" width={100} lock="right" renderer={renderAction} hidden={dataSet.length === 0} />
    </Table>
  );
}

export default StepTable;
