/* eslint-disable react/state-in-constructor */
import React, {
  Component, useState, useEffect, useMemo, useReducer,
} from 'react';
import { stores, WSHandler, Action } from '@choerodon/boot';
import {
  Modal, Progress, Button, Icon, Tooltip, Select,
} from 'choerodon-ui';
import { Table, Form, DataSet } from 'choerodon-ui/pro';
import _ from 'lodash';
import {
  exportIssues, exportIssuesFromVersion, exportIssuesFromFolder, getExportList, exportRetry,
} from '../../../../api/IssueManageApi';
import './ExportSide.less';
import SelectTree from '../CommonComponent/SelectTree';
import ExportSideDataSet from './store';

const { Column } = Table;
const { AppState } = stores;

const dataSet = new DataSet({
  autoQuery: false,
  autoCreate: true,
  fields: [
    {
      name: 'folder',
      type: 'object',
      required: true,
      label: '文件夹',
      textField: 'fileName',
      valueField: 'folderId',
      ignore: 'always',
    },
    {
      name: 'folderId',
      type: 'number',
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
});
function ExportSide(props) {
  const [folder, setFolder] = useState({ folderId: props.folderId });

  const [exportList, setExportList] = useState([]);
  const exportSideDataSet = useMemo(() => ExportSideDataSet(folder.folderId), [folder.folderId]);
  useEffect(() => {

  }, []);


  const handleFolderChange = (newFolderId) => {
    setFolder(folder);
  };

  const createExport = () => {
    if (folder) {
      exportIssuesFromFolder(folder.folderId).then((data) => {

      });
    } else {
      exportIssues().then((data) => {

      });
    }
  };
  async function handleCreateExport() {
    if (await dataSet.submit()) {
      exportSideDataSet.query();
      return true;
    } else {
      return false;
    }
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
      const ele = document.createElement('a');
      ele.href = fileUrl;
      ele.target = '_blank';
      document.body.appendChild(ele);
      ele.click();
      document.body.removeChild(ele);
    }
  };

  const handleMessage = (message) => {
    if (message === 'ok') {
      return;
    }
    const data = JSON.parse(message);
    const newExportList = [...exportList];
    const { id, rate } = data;
    const index = _.findIndex(newExportList, { id });
    // 存在记录就更新，不存在则新增记录
    if (index >= 0) {
      newExportList[index] = data;
    } else {
      newExportList.unshift(data);
    }
    setExportList(newExportList);
  };


  function renderStatus({ value, text, record }) {
    // record.get('rate') Prgoress
    return (value === 2
      ? <div>已完成</div>
      : (
        <Tooltip title={`进度：${value ? value.toFixed(1) : 0}%`} getPopupContainer={ele => ele.parentNode}>
          <Progress percent={value} showInfo={false} />
        </Tooltip>
      ));
  }

  function renderDropDownMenu({ record }) {
    const action = [{
      service: [],
      text: record.get('status') === 3 ? '重试' : '下载文件',
      action: () => handleDownload(record),
    }];
    return <Action className="action-icon" data={action} />;
  }

  function render() {
    const columns = [
      {
        title: '',
        dataIndex: 'fileUrl',
        key: 'fileUrl',
        render: (fileUrl, record) => (
          <div style={{ textAlign: 'right' }}>
            <Tooltip title={record.status === 3 ? '重试' : '下载文件'} getPopupContainer={ele => ele.parentNode}>
              <Button style={{ marginRight: -3 }} disabled={record.status === 1 || (record.status !== 3 && !fileUrl)} shape="circle" funcType="flat" icon={record.status === 3 ? 'refresh' : 'get_app'} onClick={handleDownload.bind(this, record)} />
            </Tooltip>
          </div>
        ),
      }];
    return (

      <div className="test-export-issue">
        <div className="test-export-issue-header">
          <Form dataSet={dataSet} className="test-export-issue-form">
            <SelectTree name="folder" pDataSet={dataSet} onChange={setFolder} placeholder="文件夹" isForbidRoot={false} />
          </Form>
          <Button className="test-export-issue-btn" type="primary" icon="playlist_add" onClick={handleCreateExport}>新建导出</Button>
        </div>
        <WSHandler
          messageKey={`choerodon:msg:test-issue-export:${AppState.userInfo.id}`}
          onMessage={handleMessage}
        >
          <Table dataSet={exportSideDataSet}>
            <Column name="name" align="left" />
            <Column name="action" width={50} renderer={renderDropDownMenu} />
            <Column name="successfulCount" width={120} align="left" />
            <Column name="creationDate" width={200} align="left" />
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
