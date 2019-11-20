/* eslint-disable react/state-in-constructor */
import React, {
  Component, useState, useEffect, useMemo,
} from 'react';
import { FormattedMessage } from 'react-intl';
import { Content, stores, WSHandler } from '@choerodon/boot';
import {
  Modal, Progress, Button, Icon, Tooltip, Select,
} from 'choerodon-ui';
import { Table } from 'choerodon-ui/pro';
import _ from 'lodash';
import moment from 'moment';
import { SelectVersion, SelectFolder } from '../../../../components';
import {
  exportIssues, exportIssuesFromVersion, exportIssuesFromFolder, getExportList, exportRetry,
} from '../../../../api/IssueManageApi';
import { humanizeDuration, getProjectName } from '../../../../common/utils';
import './ExportSide.scss';
import SelectTree from '../CommonComponent/SelectTree';
import ExportSideDataSet from './store';

const { Option } = Select;
const { Sidebar } = Modal;
const { Column } = Table;
const { AppState } = stores;

function ExportSide(props) {
  const [loading, setLoading] = useState(true);
  const [versionId, setVersionId] = useState('all');
  const [folderId, setFolderId] = useState(null);
  const [exportList, setExportList] = useState([]);
  const exportSideDataSet = useMemo(() => ExportSideDataSet(), []);
  useEffect(() => {
    getExportList().then((res) => {
      setExportList(res);
      setLoading(false);
    });
  }, []);


  const handleFolderChange = (newFolderId) => {
    setFolderId(newFolderId);
  };

  const createExport = () => {
    if (folderId) {
      exportIssuesFromFolder(folderId).then((data) => {

      });
    } else if (versionId && versionId !== 'all') {
      exportIssuesFromVersion(versionId).then((data) => {

      });
    } else {
      exportIssues().then((data) => {

      });
    }
  };

  const handleDownload = (record) => {
    const { fileUrl, status, id } = record;
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

  function render() {
    const columns = [
      {
        title: '导出来源',
        dataIndex: 'sourceType',
        key: 'sourceType',
        // width: 100,
        render: (sourceType, record) => {
          const ICONS = {
            1: 'project',
            2: 'version',
            4: 'folder',
          };
          return (
            <div className="c7ntest-center">
              <Icon type={ICONS[sourceType]} />
              <span className="c7ntest-text-dot" style={{ marginLeft: 10 }}>{record.name}</span>
            </div>
          );
        },
      },
      {
        title: '用例个数',
        dataIndex: 'successfulCount',
        key: 'successfulCount',
        // width: 100,
      },
      {
        title: '导出时间',
        dataIndex: 'creationDate',
        key: 'creationDate',
        // width: 160,
        render: creationDate => moment(creationDate).format('YYYY-MM-DD h:mm:ss'),
      }, {
        title: '耗时',
        dataIndex: 'during',
        key: 'during',
        // width: 100,
        render: (during, record) => <div>{onHumanizeDuration(record)}</div>,
      }, {
        title: '进度',
        dataIndex: 'rate',
        key: 'rate',
        render: (rate, record) => (record.status === 2
          ? <div>已完成</div>
          : (
            <Tooltip title={`进度：${rate ? rate.toFixed(1) : 0}%`} getPopupContainer={ele => ele.parentNode}>
              <Progress percent={rate} showInfo={false} />
            </Tooltip>
          )),
      }, {
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

      <div className="c7ntest-ExportSide">
        <div style={{ marginBottom: 24 }}>
          <SelectTree setData={setFolderId} placeholder="文件夹" />
          <Button type="primary" icon="playlist_add" onClick={createExport}>新建导出</Button>
        </div>
        <WSHandler
          messageKey={`choerodon:msg:test-issue-export:${AppState.userInfo.id}`}
          onMessage={handleMessage}
        >
          <Table dataSet={exportSideDataSet}>
            <Column name="sourceType" align="left" />
            <Column name="action" width={50} align="left" />
            <Column name="successfulCount" width={100} align="left" />
            <Column name="creationDate" width={150} align="left" />
            <Column name="during" />
            <Column name="rate" />
          </Table>
        </WSHandler>
      </div>
    );
  }

  return render();
}


export default ExportSide;
