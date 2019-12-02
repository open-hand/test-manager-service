import React, {
  useState, useEffect, useMemo, useCallback, 
} from 'react';
import { withRouter } from 'react-router-dom';
import {
  Form, TextField, Select, DataSet, Icon,
} from 'choerodon-ui/pro';
import UploadButton from './UploadButton';
import { WYSIWYGEditor } from '@/components';
import CreateIssueDataSet from './store/UpdateIssueDataSet';
import UpdateTestStepTable from './UpdateTestStepTable';
import { beforeTextUpload } from '@/common/utils';
import './EditExecuteIssue.less';

function EditExecuteIssue(props) {
  const [visibleDetail, setVisibleDetail] = useState(true);
  const {
    intl, caseId, defaultFolderValue, onOk, modal,
  } = props;

  const createDataset = useMemo(() => new DataSet(CreateIssueDataSet('issue', intl)), [intl]);


  const handleCreateIssue = useCallback(async () => {
    try {
      // 描述富文本转换为字符串
      const oldDes = createDataset.current.get('description');
      beforeTextUpload(oldDes, {}, des => createDataset.current.set('description', des.description));
      if (await createDataset.submit().then((res) => {
        const fileList = createDataset.current.get('fileList');
        const formData = new FormData();
        fileList.forEach((file) => {
          formData.append('file', file);
        });
        // uploadFile(res[0].caseId, formData);
        onOk(res[0], createDataset.current.get('folderId'));
        return true;
      })) {
        return true;
      } else {
        // error 时 重新将描述恢复富文本格式
        createDataset.current.set('description', oldDes);
        return false;
      }
    } catch (e) {
      return false;
    }
  }, [createDataset, onOk]);
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
    <Form dataSet={createDataset} className={`test-create-issue-form ${visibleDetail ? '' : 'test-create-issue-form-hidden'}`}>
      <TextField name="summary" />
      <div role="none" style={{ cursor: 'pointer' }} onClick={() => setVisibleDetail(!visibleDetail)}>
        <div className="test-create-issue-line" />
        <span className="test-create-issue-head">
          <Icon type={`${visibleDetail ? 'expand_less' : 'expand_more'}`} />
          用例详细信息
        </span>

      </div>
      <WYSIWYGEditor
        style={{ height: 200, width: '100%' }}
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
        <UpdateTestStepTable name="caseStepVOS" parentDataSet={createDataset} caseId={caseId} />
      </div>
    </Form>
  );
}
export default withRouter(EditExecuteIssue);
