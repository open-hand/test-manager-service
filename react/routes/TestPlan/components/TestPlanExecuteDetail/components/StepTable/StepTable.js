/* eslint-disable react/jsx-no-bind */
import React, {
  memo, useCallback, useEffect, useState,
} from 'react';
import { Choerodon } from '@choerodon/boot';
import {
  Icon, Tooltip,
} from 'choerodon-ui';
import { Button, Select, Table } from 'choerodon-ui/pro';
import { FormattedMessage } from 'react-intl';
import useHasAgile from '@/hooks/useHasAgile';
import _ from 'lodash';

import { addDefects } from '../../../../../../api/ExecuteDetailApi';
import './StepTable.less';
import {
  TextEditToggle, UploadInTable, StatusTags,
} from '../../../../../../components';
import DefectSelect from './DefectSelect';

const { Text, Edit } = TextEditToggle;
const { Column } = Table;

const DefectSelectText = memo(({
  defects, record, visibleDel, onDelete, onEdit, children: text, isShowContent = true, // 是否展示空白文本内容
}) => {
  const DefectItem = ({ children, data, delBtnVisible = true }) => (
    <Tooltip title={children}>
      <li
        // key={defect.id}
        role="none"
        className="c7n-test-execute-detail-step-table-defects-option"
        onMouseDown={(e) => e.stopPropagation()}
      >
        <div role="none" className={`c7n-test-execute-detail-step-table-defects-option-text${visibleDel ? '-has-btn' : ' '}`} onClick={onEdit.bind(this, data.issueId)}>{children}</div>
        {delBtnVisible && (
          <span
            role="none"
            className="c7n-test-execute-detail-step-table-defects-option-btn"
            onMouseDown={(e) => e.stopPropagation()}
            onClick={onDelete.bind(this, data, record)}
          >
            <Icon
              type="cancel"
              style={{ float: 'right' }}
            />
          </span>
        )}
      </li>
    </Tooltip>
  );
  if (defects && defects.length > 0) {
    return (
      <ul role="none" className="c7n-test-execute-detail-step-table-defects">
        {
          defects.map((defect) => defect.issueInfosVO && (
            <DefectItem data={defect} delBtnVisible={visibleDel}>
              {`${defect.issueInfosVO.issueName} ${defect.issueInfosVO.summary}`}
            </DefectItem>
          ))
        }
      </ul>
    );
  } if (isShowContent) {
    return <div style={{ width: 100, color: '#3f51b5' }}>{text}</div>;
  }
  return '';
});

function StepTable(props) {
  const {
    dataSet, ExecuteDetailStore, readOnly = false, operateStatus = false, testStatusDataSet, updateHistory, executeId, openIssue,
  } = props;
  const [lock, setLock] = useState('right');
  const [editing, setEditing] = useState();
  /**
   * 对当前页刷新
   */
  const onRefreshCurrent = () => {
    dataSet.query(dataSet.currentPage);
    updateHistory();
    setEditing(false);
  };
  /**
   * 更新表格的高度 防止lock列高度不变
   */
  const updateTableHeight = (update) => {
    setLock(false);
    update();
    setLock('right');
  };
  const onQuickPassOrFail = (code, record) => {
    const status = _.find(testStatusDataSet.toData(), { projectId: 0, statusName: code });
    if (status) {
      // setEditing(true);
      record.set('stepStatus', status.statusId);
    } else {
      Choerodon.prompt('未找到对应状态');
    }
  };
  const handleDeleteFile = (record, value) => {
    const newFiles = record.get('stepAttachment').filter((file) => file.id !== value.id);
    updateTableHeight(
      () => record.set('stepAttachment', newFiles),
    );
    // deleteAttachment(value.id).then(() => {
    //   onRefreshCurrent();
    //   Choerodon.prompt('删除成功');
    // }).catch((error) => {
    //   Choerodon.prompt(`删除失败 ${error}`);
    // });
  };
  /**
   * 获取操作列是否隐藏
   */
  const getActionHidden = () => {
    if (operateStatus && dataSet.length !== 0) {
      return false;
    }
    return true;
  };
  function renderAction({ record }) {
    return (
      <>
        <Tooltip title={<FormattedMessage id="execute_quickPass" />}>
          <Button key="pass" disabled={editing || getActionHidden()} shape="circle" funcType="flat" icon="check_circle" onClick={onQuickPassOrFail.bind(this, '通过', record)} />
        </Tooltip>
        <Tooltip title={<FormattedMessage id="execute_quickFail" />}>
          <Button key="fail" disabled={editing || getActionHidden()} shape="circle" funcType="flat" icon="cancel" onClick={onQuickPassOrFail.bind(this, '失败', record)} />
        </Tooltip>
      </>
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

  const getFileList = (attachments) => attachments && attachments.map((attachment) => {
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
    updateTableHeight(
      () => record.set('stepAttachment', data),
    );
  };
  function renderAttachment({ record, value }) {
    return (
      <UploadInTable
        fileList={getFileList(value && value.filter((attachment) => attachment.attachmentType === 'CYCLE_STEP'))}
        readOnly={readOnly}
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
    if (record.get('tempDefects')) {
      addDefects(record.get('tempDefects').map((i) => i.issueInfosVO)).then(() => {
        onRefreshCurrent();
      });
    }

    record.set('tempDefects', []);
  };
  const handleDeleteDefect = (defect, record) => {
    updateTableHeight(
      () => record.set('defects', _.filter(record.get('defects'), (item) => item.issueId !== defect.issueId)),
    );
  };
  /**
   * 渲染缺陷 缺陷只在进行中可增添
   * @param {*} param0
   */
  function renderDefects({ record, value: defects, dataSet: stepDataSet }) {
    const disabled = !operateStatus;// 用于未完成 已完成 禁止操作
    return (
      <TextEditToggle
        disabled={disabled}
        noButton
        editButtonMode={defects && defects.length > 0}
        // noButton={false}
        // simpleMode={false}
        onSubmit={() => {
          handleAddDefects(record);
        }}
        originData={{ defects: defects && defects.map((i) => i) }}
      >
        <Text>
          <DefectSelectText
            defects={defects}
            record={record}
            visibleDel={!disabled}
            onEdit={openIssue}
            onDelete={handleDeleteDefect}
            isShowContent={!disabled}
          >
            添加缺陷
          </DefectSelectText>
        </Text>

        <Edit>
          <div
            onScroll={(e) => {
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
              dataSet={dataSet}
              executeStepId={record.get('executeStepId')}
              handleSubmit={handleAddDefects}
              executeId={executeId}
              currentPageIndex={record.index}
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
    }
    return '-';
  }
  function renderStatus({ value }) {
    const status = testStatusDataSet.toData().length === 0 ? {} : testStatusDataSet.toData().find((item) => item.statusId === value);
    const { statusName = '', statusColor = false } = status || {};

    return <StatusTags name={statusName} color={statusColor} style={{ lineHeight: '.16rem' }} />;
  }
  function renderIndex({ record }) {
    return record.index + 1;
  }
  useEffect(() => {
    dataSet.setEditStatus = setEditing;
  }, [dataSet]);
  const hasAgile = useHasAgile();
  return (
    <Table dataSet={dataSet} queryBar="none" className="c7n-test-execute-detail-step-table" rowHeight="auto">
      <Column name="index" width={80} align="left" renderer={renderIndex} />
      <Column name="testStep" align="left" minWidth={200} tooltip="overflow" renderer={renderText} />
      <Column name="testData" align="left" minWidth={120} tooltip="overflow" renderer={renderText} />
      <Column name="expectedResult" align="left" minWidth={150} tooltip="overflow" renderer={renderText} />
      <Column name="stepStatus" align="left" width={85} className="c7n-test-execute-detail-step-table-status" renderer={renderStatus} editor={!editing && operateStatus && <Select optionRenderer={renderStatus} />} />
      <Column name="stepAttachment" renderer={renderAttachment} align="left" width={200} />
      <Column name="description" editor={!editing && !readOnly} align="left" tooltip="overflow" renderer={renderText} />
      {hasAgile && <Column name="defects" renderer={renderDefects} width={230} />}
      <Column name="action" width={100} lock={lock} renderer={renderAction} hidden={getActionHidden()} />
    </Table>
  );
}

export default StepTable;
