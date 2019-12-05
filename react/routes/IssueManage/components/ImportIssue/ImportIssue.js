import React, {
  useState, useRef, useEffect, useMemo, useCallback, useReducer,
} from 'react';
import {
  WSHandler, stores,
} from '@choerodon/boot';
import { Choerodon } from '@choerodon/boot';
import { Progress, Divider } from 'choerodon-ui';
import { DataSet, Form, Button } from 'choerodon-ui/pro';
import moment from 'moment';
import _ from 'lodash';
import FileSaver from 'file-saver';
import { FormattedMessage } from 'react-intl';
import { importIssue } from '@/api/FileApi';
import { humanizeDuration } from '@/common/utils';
import { getImportHistory, cancelImport, downloadTemplate } from '@/api/IssueManageApi';
import './ImportIssue.less';
import SelectTree from '../SelectTree';

const { AppState } = stores;
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

function ImportIssue(props) {
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
        type: 'number',
        bind: 'folder.folderId',
      },
    ],

    transport: {
      read: () => ({
        url: `/test/v1/projects/${AppState.currentMenuType.id}/issueFolder/query`,
        method: 'get',
        transformResponse: (res) => {
          const resObj = JSON.parse(res);
          const newArr = resObj.treeFolder.map(item => ({
            folder: {
              fileName: item.name,
              folderId: item.folderId,
            },
          }));
          // console.log('read', newArr);
          return newArr;
        },
      }),
      submit: ({ data }) => ({
        url: `/test/v1/projects/${AppState.currentMenuType.id}/case/download/excel/folder?organizationId=${AppState.currentMenuType.organizationId}&userId=${AppState.userInfo.id}`,
        method: 'get',
        data: {
          folder_id: data[0].folderId,
        },
      }),
    },
  }), []);
  const [lastRecord, setLastRecord] = useState({});
  const [importRecord, setImportRecord] = useState({});
  const [folder, setFolder] = useState(null);
  const uploadInput = useRef(null);
  const { modal } = props;
  const [importBtn, dispatch] = useReducer((state, action) => {
    const { props: modalProps } = modal;
    switch (action.type) {
      case 'import':
        return {
          ...state,
          visibleImportBtn: false,
        };
      case 'process':
        if (state.visibleCancelBtm !== true) {
          modalProps.okProps.hidden = false;
          modal.update(modalProps);
          return {
            ...state,
            visibleCancelBtm: true,
          };
        }
        return { ...state };
      case 'finish':
        if (state.visibleCancelBtm !== false) {
          modalProps.okProps.hidden = true;
          modal.update(modalProps);
        }
        return {
          visibleImportBtn: true,
          visibleCancelBtm: false,
        };
      case 'cancel':
        if (state.visibleCancelBtm !== false) {
          modalProps.okProps.hidden = true;
          modal.update(modalProps);
        }
        return {
          visibleImportBtn: true,
          visibleCancelBtm: false,
        };
      default:
        return {
          ...state,
        };
    }
  }, {
    visibleImportBtn: true,
    visibleCancelBtm: false,
  });
  const { visibleImportBtn, visibleCancelBtm } = importBtn;
  const loadImportHistory = () => {
    getImportHistory().then((data) => {
      setLastRecord(data);
    });
  };

  const upload = (file) => {
    if (!folder) {
      Choerodon.prompt('请选择目录');
      return;
    }
    const formData = new FormData();
    formData.append('file', file);
    dispatch({ type: 'import' });
    importIssue(formData, dataSet.current.get('folderId')).then(() => {
      dispatch({ type: 'process' });
      uploadInput.current.value = '';
      setImportRecord({
        ...importRecord,
        status: 1,
      });
    }).catch((e) => {
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
    if (!lastRecord) {
      return '';
    }
    const {
      failedCount, fileUrl, successfulCount, lastUpdateDate,
    } = lastRecord;
    if (lastUpdateDate) {
      return (
        <div className="c7ntest-ImportIssue-record-normal-text">
          <span className="c7ntest-ImportIssue-text">
            上次导入完成时间
            {<span>{lastUpdateDate}</span>}
            {' '}
            (耗时
            {onHumanizeDuration(lastRecord)}
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
  const debounceSetImportRecord = _.debounce(setImportRecord, 250, { maxWait: 1300 });
  const handleMessage = (res) => {
    if (res !== 'ok') {
      const data = JSON.parse(res);
      const {
        id, status, fileUrl,
      } = data;
      if (importRecord.status === 4 && id === importRecord.id && status !== 4) {
        return;
      }
      if (fileUrl) {
        window.location.href = fileUrl;
      }
      debounceSetImportRecord(data);
    }
  };


  const handleCancelImport = useCallback(() => {
    cancelImport(importRecord.id).then((res) => {
      dispatch({ type: 'cancel' });
      return true;
    }).catch((error) => {
      Choerodon.prompt(error);
      return false;
    });
    return false;
  }, [importRecord.id]);


  const exportExcel = () => {
    downloadTemplate().then((excel) => {
      const blob = new Blob([excel], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      const oneFileName = '导入测试用例模板.xlsx';
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
    } = importRecord;
    if (status === 1) {
      return (
        <WSHandler
          messageKey={`choerodon:msg:test-issue-import:${AppState.userInfo.id}`}
          onMessage={handleMessage}
        >
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
    } else if (status === 2) {
      dispatch({ type: 'finish' });
    }
    return '';
  };

  useEffect(() => {
    modal.handleOk(handleCancelImport);
    if (visibleCancelBtm !== true) {
      loadImportHistory();
    }
  }, [handleCancelImport, modal, visibleCancelBtm]);


  return (
    <div className="c7ntest-ImportIssue-form">
      {/* {renderOneForm('下载模板', ,
      )} */}
      <ImportIssueForm
        title="下载模板"
        bottom={(
          <Button icon="get_app icon" funcType="flat" color="primary" onClick={() => exportExcel()}>
            <FormattedMessage id="issue_download_tpl" />
          </Button>
        )}
      >
        您必须使用模版文件，录入用例信息
      </ImportIssueForm>
      <Divider />
      <ImportIssueForm
        title="导入测试用例"
        bottom={visibleImportBtn && (
          <Button icon="file_upload" funcType="flat" color="primary" onClick={() => importExcel()}>
            <FormattedMessage id="issue_import" />
          </Button>
        )}
      >
        <Form dataSet={dataSet}>
          <SelectTree
            name="folder"
            parentDataSet={dataSet}
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
        {renderRecord()}
        {renderProgress()}
      </ImportIssueForm>
    </div>
  );
}


export default ImportIssue;
