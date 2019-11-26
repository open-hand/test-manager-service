import React, {
  useState, useRef, useEffect, useMemo,
} from 'react';
import {
  WSHandler, stores,
} from '@choerodon/boot';
import { Choerodon } from '@choerodon/boot';
import {
  Button, Input, Modal, Progress,
  Icon, Divider, Tooltip,
} from 'choerodon-ui';
import { DataSet, Form } from 'choerodon-ui/pro';
import moment from 'moment';
import FileSaver from 'file-saver';
import { FormattedMessage } from 'react-intl';
import { importIssue } from '@/api/FileApi';
import { humanizeDuration } from '@/common/utils';
import { getImportHistory, cancelImport, downloadTemplate } from '@/api/IssueManageApi';
import './ImportIssue.less';
import SelectTree from '../SelectTree';

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
  const [importRecord, setImportRecord] = useState({});
  const [folder, setFolder] = useState(null);
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


  const upload = (file) => {
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

  const renderRecord = () => {
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
            上次导入完成时间
            {<span>lastUpdateDate</span>}
            {' '}
            (耗时
            {onHumanizeDuration(importRecord)}
            )
          </span>
          <span className="c7ntest-ImportIssue-text">
            共导入
            <span style={{ color: '#00bfa5', fontSize: 20, margin: '0 .04rem' }}>{successfulCount}</span>
            条数据成功,
            <span style={{ color: '#f76e64', fontSize: 20, margin: '0 .04rem' }}>{failedCount}</span>
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


  const beforeUpload = (e) => {
    if (e.target.files[0]) {
      upload(e.target.files[0]);
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
    dataSet.validate().then((res) => {
      if (res) {
        uploadInput.current.click();
      }
    });
  };

  const renderProgress = () => {
    const {
      rate = 0,
      status,
      lastUpdateDate,
    } = importRecord;
    if (status === 1) {
      return (
        <WSHandler
          messageKey={`choerodon:msg:test-issue-import:${AppState.userInfo.id}`}
          onMessage={handleMessage}
        >
          {lastUpdateDate && renderRecord()}
          <div className="c7ntest-ImportIssue-progress-area">
            <Progress
              className="c7ntest-ImportIssue-progress"
              status="active"
              type="circle"
              width={50}
              percent={rate}
              strokeWidth={16}
              showInfo={false}
            />
            <span className="c7ntest-ImportIssue-progress-area-text">正在导入中</span>
            <span className="c7ntest-ImportIssue-progress-area-prompt">( 本次导入耗时较长，您可先返回进行其他操作）</span>

          </div>
        </WSHandler>
      );
    } else if (lastUpdateDate || status === 2) {
      return renderRecord();
    }
    return '';
  };
  // import Divider from './Component/Divider';
  const ImportIssueForm = (formProps) => {
    const { title, children, bottom } = formProps;
    return (
      <div className="c7ntest-ImportIssue-form-one">
        <span className="c7ntest-ImportIssue-form-one-title">{title}</span>
        <span className="c7ntest-ImportIssue-form-one-content">{children}</span>
        {bottom}
      </div>
    );
  };

  useEffect(() => {
    loadImportHistory();
  }, []);


  return (
    <div className="c7ntest-ImportIssue-form">
      {/* {renderOneForm('下载模板', ,
      )} */}
      <ImportIssueForm
        title="下载模板"
        bottom={(
          <Button type="primary" funcType="flat" onClick={() => exportExcel()}>
            <Icon type="get_app icon" />
            <FormattedMessage id="issue_download_tpl" />
          </Button>
        )}
      >
        您必须使用模版文件，录入用例信息
      </ImportIssueForm>
      <Divider />
      <ImportIssueForm
        title="导入测试用例"
        bottom={isImport || (
          <Button loading={uploading} type="primary" funcType="flat" onClick={() => importExcel()}>
            <Icon type="file_upload" />
            <FormattedMessage id="issue_import" />
          </Button>
        )}
      >
        <Form dataSet={dataSet}>
          <SelectTree
            name="folder"
            pDataSet={dataSet}
            onChange={setFolder}
          />
        </Form>
        <input
          ref={uploadInput}
          type="file"
          onChange={beforeUpload}
          style={{ display: 'none' }}
          accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        />
        {renderProgress()}
      </ImportIssueForm>
    </div>
  );
}


export default ImportIssue;
