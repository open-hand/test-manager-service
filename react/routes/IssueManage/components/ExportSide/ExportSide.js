import React, {
  useState, useEffect, useMemo,
} from 'react';
import { stores, WSHandler, Action } from '@choerodon/boot';
import {
  Progress, Tooltip,
} from 'choerodon-ui';
import {
  Button,

  Table, Form, DataSet, message,
} from 'choerodon-ui/pro';

import Record from 'choerodon-ui/pro/lib/data-set/Record';
import _ from 'lodash';
import moment from 'moment';
import { exportRetry } from '@/api/IssueManageApi';
import './ExportSide.less';
import { getProjectId, humanizeDuration, renameDownload } from '@/common/utils';
import SelectTree from '../SelectTree';
import ExportSideDataSet from './store';

const { Column } = Table;
const { AppState } = stores;

function ExportSide(props) {
  const dataSet = useMemo(() => new DataSet({
    autoQuery: false,
    autoCreate: true,
    fields: [
      {
        name: 'folder',
        type: 'object',
        required: true,
        label: '目录',
        textField: 'fileName',
        valueField: 'folderId',
        ignore: 'always',
      },
      {
        name: 'folderId',
        type: 'string',
        bind: 'folder.folderId',
      },
    ],

    transport: {
      submit: ({ data }) => ({
        url: `/test/v1/projects/${AppState.currentMenuType.id}/case/download/excel/folder?organizationId=${AppState.currentMenuType.organizationId}&userId=${AppState.userInfo.id}`,
        method: 'get',
        data: {
          folder_id: data[0].folderId,
        },
      }),
    },
  }), []);

  const exportSideDataSet = useMemo(() => ExportSideDataSet(), []);
  useEffect(() => {

  }, []);

  async function handleCreateExport() {
    if (await dataSet.submit()) {
      // exportSideDataSet.query();
      return true;
    }
    return false;
  }

  const handleDownload = (record) => {
    const fileUrl = record.get('fileUrl');
    const id = record.get('id');
    const status = record.get('status');
    if (status === 3) {
      exportRetry(id);
      return;
    }
    if (fileUrl) {
      renameDownload(fileUrl, `${AppState.currentMenuType.name}-${record.get('name')}.xlsx`);
    }
  };
  /**
 * 计算耗时
 * @param {*} record
 */
  const onHumanizeDuration = (record) => {
    const { creationDate, lastUpdateDate } = record;
    const startTime = moment(creationDate);
    const lastTime = moment(lastUpdateDate);
    let diff = lastTime.diff(startTime);
    // console.log(diff);
    if (diff <= 0) {
      diff = moment().diff(startTime);
    }
    return creationDate && lastUpdateDate
      ? humanizeDuration(diff)
      : null;
  };
  const handleMessage = (res) => {
    if (res === 'ok') {
      return;
    }
    const data = JSON.parse(res);
    const { id, rate, successfulCount } = data;
    if (data.code === 'no-case-in-folder') {
      const record = exportSideDataSet.find((item) => item.get('id') === id);
      exportSideDataSet.remove(record);
      message.warn(data.message);
      return;
    }
    const newData = {
      ...data,
      lastUpdateDate: moment(data.creationDate).format('YYYY-MM-DD HH:mm:ss'),
      during: onHumanizeDuration(data),
    };
    const index = exportSideDataSet.findIndex((record) => record.get('id') === id);
    // 存在记录就更新，不存在则新增记录
    if (index >= 0) {
      exportSideDataSet.get(index).set('rate', rate);
      exportSideDataSet.get(index).set('during', onHumanizeDuration(data));
      if (data.status !== exportSideDataSet.get(index).get('status')) {
        exportSideDataSet.get(index).set('status', data.status);
        exportSideDataSet.get(index).set('fileUrl', data.fileUrl);
        if (successfulCount) {
          exportSideDataSet.get(index).set('successfulCount', data.successfulCount);
        }
      }
    } else {
      exportSideDataSet.unshift(new Record(newData));
    }
  };

  function renderStatus({ value, record }) {
    // record.get('rate') Prgoress
    return (value === 2
      ? <div>已完成</div>
      : (
        <Tooltip title={`进度：${record.get('rate') ? record.get('rate').toFixed(1) : 0}%`} getPopupContainer={(ele) => ele.parentNode}>
          <Progress percent={record.get('rate')} showInfo={false} />
        </Tooltip>
      ));
  }

  function renderDropDownMenu({ record }) {
    const action = [{
      service: [],
      text: record.get('status') === 3 ? '重试' : '下载文件',
      action: () => handleDownload(record),
    }];
    return record.get('status') !== 1 && <Action className="action-icon" data={action} />;
  }

  function render() {
    const { folderId } = props;
    return (

      <div className="test-export-issue">
        <div className="test-export-issue-header">
          <Form dataSet={dataSet} className="test-export-issue-form">
            <SelectTree defaultValue={folderId} name="folder" parentDataSet={dataSet} placeholder="目录" isForbidRoot={false} />
          </Form>
          <Button className="test-export-issue-btn" type="primary" icon="playlist_add" onClick={handleCreateExport}>新建导出</Button>
        </div>
        <h3 className="test-export-issue-table-title">导出记录</h3>
        <WSHandler
          messageKey={`test-issue-export-${getProjectId()}`}
          onMessage={handleMessage}
        >
          <Table dataSet={exportSideDataSet} className="test-export-issue-table">
            <Column name="name" align="left" />
            <Column name="action" width={50} renderer={renderDropDownMenu} />
            <Column name="successfulCount" width={120} align="left" />
            <Column name="lastUpdateDate" width={200} align="left" />
            <Column name="during" width={200} align="left" />
            <Column name="status" renderer={renderStatus} align="left" />
          </Table>
        </WSHandler>
      </div>
    );
  }

  return render();
}

export default ExportSide;
