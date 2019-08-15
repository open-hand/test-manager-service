import React, { Component } from 'react';
import {
  Page, Header, Content, WSHandler, stores,
} from '@choerodon/master';
import {
  Table, Button, Input, Dropdown, Menu, Pagination, Modal, Progress,
  Spin, Icon, Select, Divider, Tooltip,
} from 'choerodon-ui';
import moment from 'moment';
import FileSaver from 'file-saver';
import { FormattedMessage } from 'react-intl';
import { importIssue } from '../../../api/FileApi';
import { commonLink, humanizeDuration, getProjectName } from '../../../common/utils';
import { SelectVersion } from '../../../components';
import { getImportHistory, cancelImport, downloadTemplate } from '../../../api/IssueManageApi';
import './ImportIssue.less';

const { AppState } = stores;
const { confirm, Sidebar } = Modal;

class ImportIssue extends Component {
  state = {
    visible: false,
    importVisible: false,
    uploading: false,
    /* progress: 0, */
    importRecord: null,
    file: null,
    version: null,
    step: 1,
    fileName: false,
    versionName: false,
  };

  getImportHistory = () => {
    getImportHistory().then((data) => {
      this.setState({
        importRecord: data,
        step: data.status === 1 ? 3 : 1,
      });
    });
  };

  handleClose = () => {
    this.setState({
      visible: false,
    });
  };

  upload = () => {
    const { file, version, importRecord } = this.state;
    if (!file || !version) {
      Choerodon.prompt('请选择文件和目标版本');
      return;
    }
    const formData = new FormData();
    formData.append('file', file);
    this.setState({
      uploading: true,
    });
    importIssue(formData, version).then(() => {
      this.uploadInput.value = '';
      this.changeStep(1);
      this.setState({
        file: null,
        uploading: false,
        visible: false,
        importRecord: {
          ...importRecord,
          status: 1,
        },
      });
    }).catch((e) => {
      this.setState({
        uploading: false,
      });
      Choerodon.prompt('网络错误');
    });
  };

  humanizeDuration = (record) => {
    const { creationDate, lastUpdateDate } = record;
    const startTime = moment(creationDate);
    const lastTime = moment(lastUpdateDate);

    const diff = lastTime.diff(startTime);
    return creationDate && lastUpdateDate
      ? humanizeDuration(diff / 1000)
      : null;
  };

  renderRecord = (tag) => {
    const { importRecord, fileName, versionName } = this.state;
    if (!importRecord) {
      return '';
    }
    const {
      versionName: version, failedCount, fileUrl, successfulCount,
    } = importRecord;
    if (failedCount) {
      return (
        <div className="c7ntest-ImportIssue-record-normal-text">
          {fileName
            ? (
              <span className="c7ntest-ImportIssue-fileName">
                <Icon type="folder_open" className="c7ntest-ImportIssue-icon" />
                <span>{fileName}</span>
              </span>
            )
            : ''
          }
          <span className="c7ntest-ImportIssue-text">
            {!version && !versionName
              ? <React.Fragment>导入</React.Fragment>
              : (
                <React.Fragment>
                  {'导入到 '}
                  <span className="c7ntest-ImportIssue-version">{version || versionName}</span>
                  {' 版本'}
                </React.Fragment>
              )
            }
            {'失败 '}
            <span style={{ color: '#F44336' }}>
              {failedCount}
            </span>
            {' 条用例'}
            {fileUrl
              ? (
                <a href={fileUrl}>
                  {' 点击下载失败详情'}
                </a>
              ) : ''
            }
          </span>
        </div>
      );
    } else if (tag) {
      return (
        <div className="c7ntest-ImportIssue-record-normal-text">
          {fileName
            ? (
              <span className="c7ntest-ImportIssue-fileName">
                <Icon type="folder_open" className="c7ntest-ImportIssue-icon" />
                <span>{fileName}</span>
              </span>
            )
            : ''
          }
          <span className="c7ntest-ImportIssue-text">
            {'导入到 '}
            <span className="c7ntest-ImportIssue-version">{version || versionName}</span>
            {' 版本成功 '}
            <span style={{ color: '#0000FF' }}>
              {successfulCount}
            </span>
            {' 条用例'}
          </span>
        </div>
      );
    }
    return '';
  };

  handleOk = () => {
    this.setState({
      visible: false,
    });
  };

  beforeUpload = (e) => {
    if (e.target.files[0]) {
      this.setState({
        file: e.target.files[0],
        fileName: e.target.files[0].name,
      });
    }
  };

  handleMessage = (data) => {
    const { importRecord } = this.state;
    const {
      rate, id, status, fileUrl, 
    } = data;
    if (importRecord.status === 4 && id === importRecord.id && status !== 4) {
      return;
    }
    if (fileUrl) {
      window.location.href = fileUrl;
    }
    this.setState({
      /* progress: rate.toFixed(1), */
      importRecord: data,
    });
  };

  handleCancelImport=() => {
    const { importRecord } = this.state;
    cancelImport(importRecord.id).then((res) => {
      this.handleImportClose();
    });
  };

  open = () => {
    this.setState({
      importVisible: true,
    });
    this.getImportHistory();
  };

  handleImportClose = () => {
    this.setState({
      visible: false,
      importVisible: false,
      uploading: false,
      /* progress: 0, */
      importRecord: null,
      file: null,
      version: null,
      step: 1,
      versionName: false,
      fileName: false,
    });
  };

  exportExcel = () => {
    downloadTemplate().then((excel) => {
      const blob = new Blob([excel], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      const fileName = '导入模板.xlsx';
      FileSaver.saveAs(blob, fileName);
    });
  };

  importExcel = () => {
    this.setState({
      visible: true,
    });
  };

  changeStep = (value) => {
    const { step } = this.state;
    this.setState({
      step: step + value,
    });
  };

  footer = () => {
    const { step, importRecord } = this.state;
    const { status } = importRecord || {};
    if (step === 1) {
      return [
        <Button type="primary" funcType="raised" onClick={() => this.changeStep(1)}>
          <FormattedMessage id="next" />
        </Button>,
        <Button funcType="raised" onClick={this.handleImportClose}>
          <FormattedMessage id="cancel" />
        </Button>,
      ];
    } else if (step === 2) {
      return [
        <Button type="primary" funcType="raised" onClick={() => this.changeStep(-1)}>
          <FormattedMessage id="previous" />
        </Button>,
        <Button funcType="raised" onClick={this.handleImportClose}>
          <FormattedMessage id="cancel" />
        </Button>,
      ];
    } else {
      return [
        <Button type="primary" funcType="raised" onClick={this.handleImportClose}>
          <FormattedMessage id="finish" />
        </Button>,
        <Button funcType="raised" disabled={status && status !== 1} onClick={this.handleCancelImport}>
          <FormattedMessage id="issue_import_cancel" />
        </Button>,
      ];
    }
  };

  renderProgress = () => {
    const { importRecord, fileName } = this.state;
    const {
      rate = 0,
      status,
    } = importRecord;
    if (status === 1) {
      return (
        <div style={{ width: 512 }}>
          {fileName
            ? (
              <span className="c7ntest-ImportIssue-fileName">
                <Icon type="folder_open" className="c7ntest-ImportIssue-icon" />
                <span>{fileName}</span>
              </span>
            )
            : ''
          }
          <span className="c7ntest-ImportIssue-text">正在导入</span>
          <Progress
            className="c7ntest-ImportIssue-progress"
            percent={(rate).toFixed(0)}
            size="small"
            status="active"
            showInfo={false}
          />
        </div>
      );
    } else if (status === 2) {
      return this.renderRecord(true);
    } else {
      return (
        <div>
          {'正在查询导入信息，请稍后'}
        </div>
      );
    }
  };

  renderForm = () => {
    const { step, uploading, importRecord } = this.state;
    if (step === 1) {
      return (
        <React.Fragment>
          <Button type="primary" funcType="flat" onClick={() => this.exportExcel()}>
            <Icon type="get_app icon" />
            <FormattedMessage id="issue_download_tpl" />
          </Button>
          {this.renderRecord()}
        </React.Fragment>
      );
    } else if (step === 2) {
      return (
        <Button loading={uploading} type="primary" funcType="flat" onClick={() => this.importExcel()}>
          <Icon type="archive icon" />
          <FormattedMessage id="issue_import" />
        </Button>
      );
    } else {
      return (
        <WSHandler
          messageKey={`choerodon:msg:test-issue-import:${AppState.userInfo.id}`}
          onMessage={this.handleMessage}
        >
          {this.renderProgress()}
        </WSHandler>
      );
    }
  };

  render() {
    const {
      visible, uploading, file, version, importVisible,
    } = this.state;
    return (
      <Sidebar
        title="导入用例"
        visible={importVisible}
        footer={this.footer()}
        onCancel={this.handleImportClose}
        destroyOnClose
      >
        <Content
          style={{
            padding: 1,
          }}
          title={<FormattedMessage id="upload_side_content_title" values={{ name: getProjectName() }} />}
          description={<FormattedMessage id="upload_side_content_description" />}
          link="http://v0-16.choerodon.io/zh/docs/user-guide/test-management"
        >
          {this.renderForm()}
          <Modal
            title="导入用例"
            visible={visible}
            okText={<FormattedMessage id="upload" />}
            confirmLoading={uploading}
            cancelText={<FormattedMessage id="close" />}
            onOk={this.upload}
            onCancel={this.handleClose}
          >
            <div className="c7ntest-center" style={{ marginBottom: 20 }}>
              <SelectVersion
                value={version}
                onChange={(versionId, option) => {
                  this.setState({ version: versionId, versionName: option.props.children });
                }}
                style={{ width: 120 }}
              />
              <Input
                style={{ width: 340, margin: '17px 0 0 18px' }}
                value={file && file.name}
                prefix={<Icon type="attach_file" style={{ color: 'black', fontSize: '14px' }} />}
                suffix={<Tooltip title="选择文件"><Icon type="create_new_folder" style={{ color: 'black', cursor: 'pointer' }} onClick={() => { this.uploadInput.click(); }} /></Tooltip>}

              />
            </div>
            <input
              ref={
                (uploadInput) => { this.uploadInput = uploadInput; }
              }
              type="file"
              onChange={this.beforeUpload}
              style={{ display: 'none' }}
              accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            />
          </Modal>
        </Content>
      </Sidebar>
    );
  }
}


export default ImportIssue;
