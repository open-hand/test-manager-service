import React, { PureComponent } from 'react';
import { Choerodon } from '@choerodon/boot';
import PropTypes, { func } from 'prop-types';
import {
  Input, Icon, Select, Tooltip,
} from 'choerodon-ui';
import { Button } from 'choerodon-ui/pro';
import { FormattedMessage } from 'react-intl';
import _ from 'lodash';
import { Table } from 'choerodon-ui/pro';

import './StepTable.less';
import {
  TextEditToggle, UploadInTable, DefectSelect, StatusTags,
} from '../../../../../components';

const { Text, Edit } = TextEditToggle;
const { Column } = Table;
function StepTable(props) {
  const { dataSet } = props;

  const onQuickPassOrFail = (code, record) => {
    // console.log('onQuickPassOrFail', code, record);
  };

  function renderAction({ record }) {
    return (
      <React.Fragment>
        <Tooltip title={<FormattedMessage id="execute_quickPass" />}>
          <Button key="pass" disabled={false} shape="circle" funcType="flat" icon="check_circle" onClick={onQuickPassOrFail.bind(this, 'success', record)} />
        </Tooltip>
        <Tooltip title={<FormattedMessage id="execute_quickFail" />}>
          <Button key="fail" disabled={false} shape="circle" funcType="flat" icon="cancel" onClick={onQuickPassOrFail.bind(this, 'fail', record)} />
        </Tooltip>
      </React.Fragment>
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

  function renderDefects({ record, value: defects }) {
    const disabled = defects.length !== 0;
    // return defects;
    return (
      <TextEditToggle
        noButton
        // saveRef={(bugsToggle) => { this[`bugsToggle_${record.get('id')}`] = bugsToggle; }}
        onSubmit={() => {
        }}
        originData={{ defects }}
      >
        <Text>
          {
            // eslint-disable-next-line no-nested-ternary
            defects.length > 0 ? (
              <div>
                {defects.map((defect, i) => (
                  <div
                    // key={defect.id}
                    style={{
                      fontSize: '13px',
                    }}
                  >
                    {defect}
                  </div>
                ))}
              </div>
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
              // setNeedAdd={(needAdd) => { that.needAdd = needAdd; }}
              executeStepId={record.executeStepId}
            // bugsToggleRef={this[`bugsToggle_${record.stepId}`]}
            />
          </div>
        </Edit>

      </TextEditToggle>
    );
  }
  function renderDescription({ value }) {
    if (value) {
      return value;
    } else {
      return '-';
    }
  }
  return (
    <Table dataSet={dataSet} queryBar="none">
      <Column name="index" renderer={renderIndex} width={80} align="left" />
      <Column name="testStep" align="left" minWidth={200} tooltip="overflow" />
      <Column name="testData" align="left" minWidth={120} tooltip="overflow" />
      <Column name="expectedResult" align="left" minWidth={150} tooltip="overflow" />
      <Column name="stepStatus" width={80} />
      <Column name="stepAttachment" renderer={renderAttachment} align="left" />
      <Column name="description" editor align="left" tooltip="overflow" renderer={renderDescription} />
      <Column name="defects" renderer={renderDefects} width={165} />
      <Column name="action" width={100} lock="right" renderer={renderAction} hidden={dataSet.length === 0} />
    </Table>
  );
}

export default StepTable;
