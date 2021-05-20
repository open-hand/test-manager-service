import React, {
  useState, useEffect, useMemo, useCallback,
} from 'react';
import { withRouter } from 'react-router-dom';
import {
  Form, DataSet, Icon, message, Select, Button,
} from 'choerodon-ui/pro';
import { PromptInput } from '@/components';
import { observer } from 'mobx-react-lite';
import { uploadFile, createLink } from '@/api/IssueManageApi';
import LinkList from '@/components/LinkList';
import CKEditor from '@/components/CKEditor';
import { remove } from 'lodash';
import UploadButton from './UploadButton';
import CreateIssueDataSet from './store/CreateIssueDataSet';
import CreateTestStepTable from './CreateTestStepTable';
import SelectTree from '../../routes/IssueManage/components/SelectTree';
import './CreateIssue.less';
import PriorityOptionDataSet from './store/PriorityOptionDataSet';

const openLinkIssueModal = require('./LinkIssue').default;

function CreateIssue(props) {
  const [visibleDetail, setVisibleDetail] = useState(true);
  const [linkIssues, setLinkIssues] = useState([]);
  const {
    intl, caseId, defaultFolderValue, onOk = (v1, v2) => v1, modal, request, noCreateLink,
  } = props;
  const priorityOptionsDataSet = useMemo(() => new DataSet(PriorityOptionDataSet()), []);
  const createDataset = useMemo(() => new DataSet(CreateIssueDataSet('issue', intl, priorityOptionsDataSet)), [intl]);

  useEffect(() => {
    // 设置优先级默认值
    const defaultRecord = priorityOptionsDataSet.find((item) => item.get('defaultFlag'));
    if (defaultRecord) {
      createDataset.current.set('priorityId', defaultRecord.get('id'));
    }
  }, [priorityOptionsDataSet.length]);
  const handleUploadFile = useCallback(async (currentCaseId) => {
    const fileList = createDataset.current.get('fileList');
    const formData = new FormData();

    if (fileList) {
      fileList.forEach((file) => {
        formData.append('file', file);
      });
      uploadFile(currentCaseId, formData);
    }
  }, [createDataset]);
  const handleCreateIssue = useCallback(async () => {
    try {
      if (!await createDataset.current.validate()) {
        return false;
      }
      // 描述富文本转换为字符串
      const oldDes = createDataset.current.get('description');
      if (request && typeof (request) === 'function') {
        const data = createDataset.current.toData();
        const newData = {
          ...data,
          caseStepVOS: data.caseStepVOS.filter((i) => i.stepIsCreating !== true).map((i) => ({
            testStep: i.testStep,
            testData: i.testData,
            expectedResult: i.expectedResult,
          })),
        };
        const reqResult = await request(newData);
        if (linkIssues.length > 0) {
          await createLink(reqResult.caseId, linkIssues.map((record) => record.get('issueId')));
        }
        // eslint-disable-next-line no-unused-expressions
        reqResult && handleUploadFile((reqResult || {}).caseId);
        onOk(reqResult, createDataset.current.get('folderId'));
        return typeof (reqResult) !== 'undefined' ? reqResult : true;
      }
      if (await createDataset.submit().then(async (res) => {
        if (!res) {
          throw new Error('create error');
        }
        if (linkIssues.length > 0) {
          await createLink(res[0].caseId, linkIssues.map((record) => record.get('issueId')));
        }
        handleUploadFile(res[0].caseId);
        onOk(res[0], createDataset.current.get('folderId'));
        return true;
      })) {
        return true;
      }
      // error 时 重新将描述恢复富文本格式

      createDataset.current.set('description', oldDes);
      return false;
    } catch (e) {
      message.error(e);
      return false;
    }
  }, [createDataset, handleUploadFile, linkIssues, onOk, request]);
  const handleChangeDes = (value) => {
    createDataset.current.set('description', value);
  };
  const onUploadFile = ({ file, fileList, event }) => {
    createDataset.current.set('fileList', fileList);
  };
  useEffect(() => {
    // 初始化属性
    modal.handleOk(handleCreateIssue);
  }, [handleCreateIssue, modal]);

  return (
    <>
      <Form dataSet={createDataset} className={`test-create-issue-form ${visibleDetail ? '' : 'test-create-issue-form-hidden'}`}>
        <PromptInput name="summary" maxLength={44} />
        <SelectTree name="folder" parentDataSet={createDataset} defaultValue={defaultFolderValue ? defaultFolderValue.id : undefined} />
        <Select name="priorityId" />
        <div role="none" style={{ cursor: 'pointer' }} onClick={() => setVisibleDetail(!visibleDetail)}>
          <div className="test-create-issue-line" />
          <span className="test-create-issue-head">
            <Icon type={`${visibleDetail ? 'expand_less' : 'expand_more'}`} />
            用例前置条件
          </span>

        </div>
        <CKEditor
          style={{ minHeight: 300, width: '100%' }}
          onChange={handleChangeDes}
        />
        {/* //  这里逻辑待处理， DataSet提交  */}
        <div className="test-create-issue-form-file">
          <span className="test-create-issue-head">附件</span>
          <UploadButton onChange={onUploadFile} />
        </div>

        <div className="test-create-issue-form-step">
          <div className="test-create-issue-line" />
          <span className="test-create-issue-head">测试步骤</span>
          <CreateTestStepTable name="caseStepVOS" parentDataSet={createDataset} caseId={caseId} />
        </div>

      </Form>
      <div className="test-create-issue-line" />
      {!noCreateLink && (
        <>
          <div role="none" style={{ display: 'flex', marginTop: '.195rem' }}>
            <span className="test-create-issue-head">
              问题链接
            </span>
            <Button
              style={{ marginLeft: 'auto' }}
              icon="playlist_add"
              color="primary"
              onClick={() => {
                openLinkIssueModal({
                  selected: linkIssues,
                  onSubmit: (records) => {
                    setLinkIssues(records);
                  },
                });
              }}
            />
          </div>
          <div>
            {linkIssues.map((record, i) => (
              <LinkList
                key={record.get('issueId')}
                issue={record.toData()}
                i={i}
                deleteLink={() => {
                  remove(linkIssues, record);
                  setLinkIssues([...linkIssues]);
                }}
              />
            ))}
          </div>
        </>
      )}

    </>
  );
}
export default withRouter(observer(CreateIssue));
