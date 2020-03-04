import React, {
  useState, useRef, useEffect, useMemo, useReducer,
} from 'react';
import {
  WSHandler, stores,
} from '@choerodon/boot';
import { Choerodon } from '@choerodon/boot';
import { Progress, Divider } from 'choerodon-ui';
import {
  DataSet, Form, Button, message,
} from 'choerodon-ui/pro';
import moment from 'moment';
import _, { find } from 'lodash';
import FileSaver from 'file-saver';
import { FormattedMessage } from 'react-intl';
import { importIssue } from '@/api/FileApi';
import { humanizeDuration } from '@/common/utils';
import { getImportHistory, cancelImport, downloadTemplate } from '@/api/IssueManageApi';
import IssueStore from '../../stores/IssueStore';
import IssueTreeStore from '../../stores/IssueTreeStore';

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
  const wsRef = useRef();
  const autoDownRef = useRef();
  const { modal, defaultFolderValue } = props;
  /**
   * 加载导入历史
   * @param {boolean} isAutoDown 是否自动下载错误详情（前提 最近一次历史存在错误详情时）
   */
  const loadImportHistory = (isAutoDown = false) => getImportHistory().then((data) => {
    setLastRecord(data);
    const { fileUrl } = data;
    if (fileUrl && isAutoDown && autoDownRef.current) {
      autoDownRef.current.click();
      return true;
    } else {
      return false;
    }
  });
  const [importBtn, dispatch] = useReducer((state, action) => {
    switch (action.type) {
      case 'import':
        return {
          ...state,
          visibleImportBtn: false,
        };
      case 'process':
        if (state.visibleCancelBtn === true && state.visibleImportBtn === false) {
          return state;
        } else {
          return {
            visibleImportBtn: false,
            visibleCancelBtn: true,
          };
        }
      case 'finish':
        loadImportHistory(true);
        return {
          visibleImportBtn: true,
          visibleCancelBtn: false,
        };
      case 'cancel':
        return {
          visibleImportBtn: true,
          visibleCancelBtn: false,
        };
      default:
        return {
          ...state,
        };
    }
  }, {
    visibleImportBtn: true,
    visibleCancelBtn: false,
  });
  const { visibleImportBtn, visibleCancelBtn } = importBtn;


  const upload = (file) => {
    if (!folder) {
      Choerodon.prompt('请选择目录');
      return;
    }
    const formData = new FormData();
    formData.append('file', file);
    dispatch({ type: 'import' });
    importIssue(formData, dataSet.current.get('folderId')).then(() => {
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
    let diff = lastTime.diff(startTime);
    // console.log(diff);
    if (diff <= 0) {
      diff = moment().diff(startTime);
    }
    return creationDate && lastUpdateDate
      ? humanizeDuration(diff)
      : null;
  };

  const renderRecord = () => {
    if (!lastRecord) {
      return <span>暂无导入记录</span>;
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
              <a className="c7ntest-ImportIssue-text c7ntest-ImportIssue-text-down-load" href={fileUrl} ref={autoDownRef}>
                点击下载失败详情
              </a>
            ) : ''
          }
        </div>
      );
    }
    return <span>暂无导入记录</span>;
  };


  const beforeUpload = (e) => {
    if (e.target.files[0]) {
      upload(e.target.files[0]);
    }
  };

  const debounceSetImportRecord = _.debounce((e) => {
    setImportRecord(e);
    // wsRef.current.context.ws.destroySocketByPath(wsRef.current.props.path);
  }, 250, { maxWait: 1300 });
  const handleMessage = (res) => {
    if (res !== 'ok') {
      const data = JSON.parse(res);
      if (data.code === 'test-issue-import-error') {
        message.error(data.message);
        dispatch({ type: 'cancel' });
        setImportRecord({});
        return;
      }
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
    } else {
      dispatch({ type: 'process' });
    }
  };


  const handleCancelImport = () => {
    // debounceSetImportRecord.cancel();
    cancelImport(importRecord.id).then((res) => {
      debounceSetImportRecord.flush();
      setImportRecord({
        ...importRecord,
        status: undefined,
      });
      loadImportHistory();
      dispatch({ type: 'cancel' });
    }).catch((error) => {
      debounceSetImportRecord.flush();
      Choerodon.prompt(`${error || '网络异常'}`);
      setImportRecord({
        ...importRecord,
        status: undefined,
      });
      loadImportHistory();
      dispatch({ type: 'cancel' });
    });
  };


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
          ref={wsRef}
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
      // loadImportHistory();
      setImportRecord({});
      dispatch({ type: 'finish' });
    }
    return '';
  };

  useEffect(() => {
    loadImportHistory();
  }, []);

  const handleCloseModal = () => {
    // 根据取消按钮可见状态 判断是否销毁modal
    modal.close(!visibleCancelBtn);
    if (dataSet.current.get('folderId')) {
      IssueTreeStore.setCurrentFolder(find(IssueTreeStore.treeData.treeFolder, { id: dataSet.current.get('folderId') }) || {});
      props.onOk(1, 10, dataSet.current.get('folderId'));
    }
  };

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
        您必须使用模板文件，录入用例信息
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
            defaultValue={defaultFolderValue.id}
            disabled={visibleCancelBtn} // 导入过程中禁止操作文件树
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
      <div className="c7ntest-ImportIssue-form-modal-footer">
        <Button disabled={!visibleCancelBtn} hidden={!visibleCancelBtn} funcType="raised" color="primary" onClick={handleCancelImport}>取消导入</Button>
        <Button funcType="raised" onClick={handleCloseModal}>关闭</Button>
      </div>
    </div>
  );
}


export default ImportIssue;
