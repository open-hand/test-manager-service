import React, {
  Component, useState, useRef, useEffect,
} from 'react';
import {
  Page, Header, Content, WSHandler, stores,
} from '@choerodon/boot';
import { Choerodon } from '@choerodon/boot';
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

function ImportIssue(props) {
  const [visible, setVisible] = useState(false);
  const [importVisible, setImportVisible] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [importRecord, setImportRecord] = useState(null);
  const [file, setFile] = useState(null);
  const [version, setVersion] = useState(null);
  const [step, setStep] = useState(1);

  const [fileName, setFileName] = useState(false);
  const [versionName, setVersionName] = useState(false);

  const uploadInput = useRef(null);

  const loadImportHistory = () => {
    getImportHistory().then((data) => {
      setImportRecord(data);
      setStep(data.status === 1 ? 3 : 1);
    });
  };

  const handleClose = () => {
    setVisible(false);
  };

  const changeStep = (value) => {
    setStep(step + value);
  };

  const upload = () => {
    if (!file || !version) {
      Choerodon.prompt('请选择文件和目标版本');
      return;
    }
    const formData = new FormData();
    formData.append('file', file);
    setUploading(true);
    importIssue(formData, version).then(() => {
      uploadInput.current.value = '';
      changeStep(1);

      setFile(null);
      setUploading(false);
      setVisible(false);
      setImportRecord({
        ...importRecord,
        status: 1,
      });
    }).catch((e) => {
      setUploading(false);
      Choerodon.prompt('网络错误');
    });
  };


  const onHumanizeDuration = (record) => {
    const { creationDate, lastUpdateDate } = record;
    const startTime = moment(creationDate);
    const lastTime = moment(lastUpdateDate);

    const diff = lastTime.diff(startTime);
    return creationDate && lastUpdateDate
      ? humanizeDuration(diff / 1000)
      : null;
  };

  const renderRecord = (tag) => {
    if (!importRecord) {
      return '';
    }
    const {
      versionName: newVersion, failedCount, fileUrl, successfulCount,
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
            {!newVersion && !versionName
              ? <React.Fragment>导入</React.Fragment>
              : (
                <React.Fragment>
                  导入到
                  <span className="c7ntest-ImportIssue-version">{newVersion || versionName}</span>
                  版本
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
                  点击下载失败详情
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
            导入到
            <span className="c7ntest-ImportIssue-version">{newVersion || versionName}</span>
            版本成功
            <span style={{ color: '#0000FF' }}>
              {successfulCount}
            </span>
            条用例
          </span>
        </div>
      );
    }
    return '';
  };

  const handleOk = () => {
    setVisible(false);
  };

  const beforeUpload = (e) => {
    if (e.target.files[0]) {
      setFile(e.target.files[0]);
      setFileName(e.target.files[0]);
    }
  };

  const handleMessage = (res) => {
    if (res !== 'ok') {
      const data = JSON.parse(res);
      const {
        rate, id, status, fileUrl,
      } = data;
      if (importRecord.status === 4 && id === importRecord.id && status !== 4) {
        return;
      }
      if (fileUrl) {
        window.location.href = fileUrl;
      }
      setImportRecord(data);
    }
  };


  const open = () => {
    setImportVisible(true);
    loadImportHistory();
  };

  const handleImportClose = () => {
    setVisible(false);
    setImportRecord(null);
    setImportVisible(false);
    setFile(null);
    setVersionName(false);
    setVersion(null);
    setStep(1);
    setFileName(false);
  };

  const handleCancelImport = () => {
    cancelImport(importRecord.id).then((res) => {
      handleImportClose();
    });
  };


  const exportExcel = () => {
    downloadTemplate().then((excel) => {
      const blob = new Blob([excel], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      const oneFileName = '导入模板.xlsx';
      FileSaver.saveAs(blob, oneFileName);
    });
  };

  const importExcel = () => {
    setVisible(true);
  };


  const footer = () => {
    const { status } = importRecord || {};
    if (step === 1) {
      return [
        <Button type="primary" funcType="raised" onClick={() => changeStep(1)}>
          <FormattedMessage id="next" />
        </Button>,
        <Button funcType="raised" onClick={handleImportClose}>
          <FormattedMessage id="cancel" />
        </Button>,
      ];
    } else if (step === 2) {
      return [
        <Button type="primary" funcType="raised" onClick={() => changeStep(-1)}>
          <FormattedMessage id="previous" />
        </Button>,
        <Button funcType="raised" onClick={handleImportClose}>
          <FormattedMessage id="cancel" />
        </Button>,
      ];
    } else {
      return [
        <Button type="primary" funcType="raised" onClick={handleImportClose}>
          <FormattedMessage id="finish" />
        </Button>,
        <Button funcType="raised" disabled={status && status !== 1} onClick={handleCancelImport}>
          <FormattedMessage id="issue_import_cancel" />
        </Button>,
      ];
    }
  };

  const renderProgress = () => {
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
      return renderRecord(true);
    } else {
      return (
        <div>
          正在查询导入信息，请稍后
        </div>
      );
    }
  };
  // import Divider from './Component/Divider';
  const renderOneForm = (title, content, button) => (
    <div className="c7ntest-ImportIssue-form-one">
      <span className="c7ntest-ImportIssue-form-one-title">{title}</span>
      <span className="c7ntest-ImportIssue-form-one-content">{content}</span>
      {button}
    </div>
  );
  const renderForm = () => (
    <div className="c7ntest-ImportIssue-form">
      {renderOneForm('下载模板', '您必须使用模版文件，录入用例信息',
        <Button type="primary" funcType="flat" onClick={() => exportExcel()}>
          <Icon type="get_app icon" />
          <FormattedMessage id="issue_download_tpl" />
        </Button>)}

      <Divider />
      {renderOneForm('导入测试用例', renderRecord(),
        <Button loading={uploading} type="primary" funcType="flat" onClick={() => importExcel()}>
          <Icon type="archive icon" />
          <FormattedMessage id="issue_import" />
        </Button>)}
      {/* <WSHandler
          messageKey={`choerodon:msg:test-issue-import:${AppState.userInfo.id}`}
          onMessage={handleMessage}
        >
          {renderProgress()}
        </WSHandler> */}
    </div>
  );

  useEffect(() => {
    loadImportHistory();
  }, []);

  function render() {
    return (
      <React.Fragment>
        {renderForm()}
        <Modal
          title="导入用例"
          visible={visible}
          okText={<FormattedMessage id="upload" />}
          confirmLoading={uploading}
          cancelText={<FormattedMessage id="close" />}
          onOk={upload}
          onCancel={handleClose}
        >
          <div style={{ margin: '20px 0', display: 'flex', alignItems: 'center' }}>
            <SelectVersion
              value={version}
              onChange={(versionId, option) => {
                setVersion(versionId);
                setVersionName(option.props.children);
              }}
              style={{ width: 120 }}
            />
            <Input
              style={{ width: 340, marginLeft: '18px' }}
              value={file && file.name}
              prefix={<Icon type="attach_file" style={{ color: 'black', fontSize: '14px' }} />}
              suffix={<Tooltip title="选择文件"><Icon type="create_new_folder" style={{ color: 'black', cursor: 'pointer' }} onClick={() => { uploadInput.current.click(); }} /></Tooltip>}

            />
          </div>
          <input
            ref={uploadInput}
            type="file"
            onChange={beforeUpload}
            style={{ display: 'none' }}
            accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
          />
        </Modal>
      </React.Fragment>
    );
  }
  return render();
}


export default ImportIssue;
