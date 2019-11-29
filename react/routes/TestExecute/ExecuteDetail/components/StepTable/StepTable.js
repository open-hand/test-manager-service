import React, { PureComponent } from 'react';
import { Choerodon } from '@choerodon/boot';
import PropTypes, { func } from 'prop-types';
import {
  Input, Icon, Select, Tooltip, Button,
} from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import _ from 'lodash';
import { Table } from 'choerodon-ui/pro';

import './StepTable.less';
import { UploadInTable } from '../../../../../components';


const { Column } = Table;
function StepTable(props) {
  const { dataSet } = props;

  const onQuickPassOrFail = (code, record) => {
    // console.log('onQuickPassOrFail', code, record);
  };

  function renderAction({ record }) {
    return (
      <div style={{ display: 'flex' }}>
        <Tooltip title={<FormattedMessage id="execute_quickPass" />}>
          <Button disabled={false} shape="circle" funcType="flat" icon="check_circle" onClick={onQuickPassOrFail.bind(this, 'success', record)} />
        </Tooltip>
        <Tooltip title={<FormattedMessage id="execute_quickFail" />}>
          <Button disabled={false} shape="circle" funcType="flat" icon="cancel" onClick={onQuickPassOrFail.bind(this, 'fail', record)} />
        </Tooltip>
      </div>
    );
  }
  function renderIndex({ record }) {
    return record.id % 1000;
  }
  function renderAttachment({ record }) {
    return (
      <UploadInTable
        // fileList={that.getFileList(stepAttachment.filter(attachment => attachment.attachmentType === 'CYCLE_STEP'))}
        // onOk={ExecuteDetailStore.loadDetailList}
        // enterLoad={ExecuteDetailStore.enterloading}
        // leaveLoad={ExecuteDetailStore.unloading}
        //
        fileList={[]}
        config={{
          attachmentLinkId: record.executeStepId,
          attachmentType: 'CYCLE_STEP',
        }}
      />
    );
  }
  return (
    <Table dataSet={dataSet}>
      <Column name="index" renderer={renderIndex} width={80} align="left" />
      <Column name="testStep" align="left" minWidth={200} />
      <Column name="testData" align="left" minWidth={120} />
      <Column name="expectedResult" align="left" minWidth={150} />
      <Column name="stepStatus" width={80} />
      <Column name="stepAttachment" renderer={renderAttachment} align="left" />
      <Column name="comment" editor align="left" />
      <Column name="defects" editor />
      <Column name="action" lock="right" renderer={renderAction} hidden={dataSet.length === 0} />
    </Table>
  );
}

export default StepTable;
