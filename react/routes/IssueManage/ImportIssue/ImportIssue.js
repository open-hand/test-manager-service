import React, {
  Component, useState, useRef, useEffect, useMemo,
} from 'react';
import {
  WSHandler, stores,
} from '@choerodon/boot';
import { Choerodon } from '@choerodon/boot';
import {
  Table, Button, Input, Dropdown, Menu, Pagination, Modal, Progress,
  Icon, Divider, Tooltip,
} from 'choerodon-ui';
import { DataSet, Form } from 'choerodon-ui/pro';
import moment from 'moment';
import FileSaver from 'file-saver';
import { FormattedMessage } from 'react-intl';
import { importIssue } from '../../../api/FileApi';
import { commonLink, humanizeDuration, getProjectName } from '../../../common/utils';
import { getImportHistory, cancelImport, downloadTemplate } from '../../../api/IssueManageApi';
import './ImportIssue.less';
import SelectTree from '../components/CommonComponent/SelectTree';

const { AppState } = stores;

function ImportIssue(props) {
  const dataSet = useMemo(() => new DataSet({
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
  }), []);
  const [visible, setVisible] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [importRecord, setImportRecord] = useState(null);
  const [file, setFile] = useState(null);
  const [folder, setFolder] = useState(null);
  const [fileName, setFileName] = useState(false);
  const [isImport, setIsImport] = useState(false);
  const uploadInput = useRef(null);

  const loadImportHistory = () => {
    getImportHistory().then((data) => {
      setImportRecord(data);
    });
  };

  const handleClose = () => {
    setVisible(false);
  };


  const upload = () => {
    if (!folder) {
      Choerodon.prompt('请选择文件夹');
      return;
    }
    const formData = new FormData();
    formData.append('file', file);
    setUploading(true);
    setIsImport(true);
    importIssue(formData, dataSet.current.get('folderId')).then(() => {
      uploadInput.current.value = '';
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
      failedCount, fileUrl, successfulCount, lastUpdateDate,
    } = importRecord;
    if (lastUpdateDate) {
      return (
        <div className="c7ntest-ImportIssue-record-normal-text">
          <span className="c7ntest-ImportIssue-text">
            上次导入完成时间：
            {lastUpdateDate}
            {' '}
            (耗时
            {onHumanizeDuration(importRecord)}
            )
          </span>
          <span className="c7ntest-ImportIssue-text">
            共导入
            <span style={{ color: '#0000FF' }}>{successfulCount}</span>
            条数据成功,
            <span style={{ color: '#F44336' }}>{failedCount}</span>
            条数据失败
          </span>
          {fileUrl
            ? (
              <a href={fileUrl}>
                点击下载失败详情
              </a>
            ) : ''
          }
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

  const handleImportClose = () => {
    setVisible(false);
    setImportRecord(null);
    setFile(null);
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

  const renderProgress = () => {
    const {
      rate = 0,
      status,
    } = importRecord;
    if (status === 1) {
      return (
        <div className="c7ntest-ImportIssue-progress-area">
          <Progress
            className="c7ntest-ImportIssue-progress"
            status="active"
            type="circle"
            width={50}
            strokeWidth={12}
            showInfo={false}
          />
          <span className="c7ntest-ImportIssue-progress-area-text">正在导入中</span>
          <span className="c7ntest-ImportIssue-progress-area-prompt">( 本次导入耗时较长，您可先返回进行其他操作）</span>

        </div>
      );
    } else if (status === 2) {
      return renderRecord(true);
    } else {
      return (
        <div width={{ width: 300 }}>
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
      {renderOneForm('导入测试用例', renderRecord(), !isImport ? (
        <Button loading={uploading} type="primary" funcType="flat" onClick={() => importExcel()}>
          <Icon type="archive icon" />
          <FormattedMessage id="issue_import" />
        </Button>
      ) : (
        <WSHandler
          messageKey={`choerodon:msg:test-issue-import:${AppState.userInfo.id}`}
          onMessage={handleMessage}
        >
          {renderProgress()}
        </WSHandler>
      ))}


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
          <Form dataSet={dataSet} columns={2} style={{ width: 200, height: 60 }}>
            <SelectTree
              name="folder"
              pDataSet={dataSet}
              onChange={setFolder}
            />
            <Input
              style={{ width: 340, marginLeft: '18px' }}
              value={file && file.name}
              prefix={<Icon type="attach_file" style={{ color: 'black', fontSize: '14px' }} />}
              suffix={<Tooltip title="选择文件"><Icon type="create_new_folder" style={{ color: 'black', cursor: 'pointer' }} onClick={() => { uploadInput.current.click(); }} /></Tooltip>}
            />
          </Form>
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
