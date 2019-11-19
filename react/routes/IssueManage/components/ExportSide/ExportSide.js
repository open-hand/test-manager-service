/* eslint-disable react/state-in-constructor */
import React, { Component } from 'react';
import { FormattedMessage } from 'react-intl';
import { Content, stores, WSHandler } from '@choerodon/boot';
import {
  Modal, Progress, Table, Button, Icon, Tooltip, Select,
} from 'choerodon-ui';
import _ from 'lodash';
import moment from 'moment';
import { SelectVersion, SelectFolder } from '../../../../components';
import {
  exportIssues, exportIssuesFromVersion, exportIssuesFromFolder, getExportList, exportRetry,
} from '../../../../api/IssueManageApi';
import { humanizeDuration, getProjectName } from '../../../../common/utils';
import './ExportSide.scss';

const { Option } = Select;
const { Sidebar } = Modal;
const { AppState } = stores;

class ExportSide extends Component {
  state = {
    loading: true,
    visible: false,
    versionId: 'all',
    folderId: null,
    exportList: [],
  }


  handleClose = () => {
    this.setState({
      visible: false,
    });
  }

  open = () => {
    this.setState({
      visible: true,
      loading: true,
      // folderId: null,
    });
    getExportList().then((exportList) => {
      this.setState({
        exportList,
        loading: false,
      });
    });
  }

  handleOk = () => {
    this.setState({
      visible: false,
    });
  }

  handleVersionChange = (versionId) => {
    this.setState({
      versionId,
      folderId: null,
    });
  }

  handleFolderChange = (folderId) => {
    this.setState({
      folderId,
    });
  }

  createExport = () => {
    const { versionId, folderId } = this.state;
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
  }

  handleDownload = (record) => {
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
  }

  handleMessage = (message) => {
    if (message === 'ok') {
      return;
    }
    const data = JSON.parse(message);
    const temp = this.state.exportList;
    const exportList = [...temp];
    const { id, rate } = data;
    const index = _.findIndex(exportList, { id });
    // 存在记录就更新，不存在则新增记录
    if (index >= 0) {
      exportList[index] = data;
    } else {
      exportList.unshift(data);
    }
    this.setState({
      exportList,
    });
  }

  humanizeDuration = (record) => {
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
  }

  render() {
    const {
      visible, versionId, folderId, exportList, loading,
    } = this.state;
    const columns = [{
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
      render: (during, record) => <div>{this.humanizeDuration(record)}</div>,
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
            <Button style={{ marginRight: -3 }} disabled={record.status === 1 || (record.status !== 3 && !fileUrl)} shape="circle" funcType="flat" icon={record.status === 3 ? 'refresh' : 'get_app'} onClick={this.handleDownload.bind(this, record)} />
          </Tooltip>
        </div>
      ),
    }];
    return (
      <Sidebar
        title="导出用例"
        visible={visible}
        footer={<Button onClick={this.handleClose} type="primary" funcType="raised"><FormattedMessage id="close" /></Button>}
      >
        <Content
          style={{
            padding: '5px 0 10px 0',
          }}
        >
          <div className="c7ntest-ExportSide">
            <div style={{ marginBottom: 24 }}>
              {/* <SelectVersion allowClear value={versionId} onChange={this.handleVersionChange}>
                <Option value="all" key="all">所有版本</Option>
              </SelectVersion> */}
              <SelectFolder style={{ width: 200, margin: '0 24px' }} label="文件夹" disabled={!versionId} versionId={versionId} value={folderId} allowClear onChange={this.handleFolderChange} />
              <Button type="primary" icon="playlist_add" onClick={this.createExport}>新建导出</Button>
            </div>
            <WSHandler
              messageKey={`choerodon:msg:test-issue-export:${AppState.userInfo.id}`}
              onMessage={this.handleMessage}
            >
              <Table columns={columns} dataSource={exportList} loading={loading} />
            </WSHandler>
          </div>
        </Content>
      </Sidebar>
    );
  }
}


export default ExportSide;
