import React, {
  useState, useEffect, useMemo, useCallback,
} from 'react';
import { withRouter } from 'react-router-dom';
import {
  Form, TextField, Select, DataSet, Icon,
} from 'choerodon-ui/pro';
import UploadButton from './UploadButton';
import { WYSIWYGEditor } from '@/components';
import EditIssueDataSet from './store/EditIssueDataSet';
import EditTestStepTable from './EditTestStepTable';
import { beforeTextUpload } from '@/common/utils';
import './EditExecuteIssue.less';

function EditExecuteIssue(props) {
  const [visibleDetail, setVisibleDetail] = useState(true);
  const {
    intl, executeId, onOk, modal,
  } = props;

  const editDataset = useMemo(() => new DataSet(EditIssueDataSet(executeId, 'issue', intl)), [executeId, intl]);


  const handleUpdateIssue = useCallback(async () => {
    try {
      // 描述富文本转换为字符串
      const oldDes = editDataset.current.get('description');
      beforeTextUpload(oldDes, {}, des => editDataset.current.set('description', des.description));
      if (await editDataset.submit().then((res) => {
        const fileList = editDataset.current.get('fileList');
        const formData = new FormData();
        fileList.forEach((file) => {
          formData.append('file', file);
        });
        // uploadFile(res[0].caseId, formData);
        onOk(res[0], editDataset.current.get('folderId'));
        return true;
      })) {
        return true;
      } else {
        // error 时 重新将描述恢复富文本格式
        editDataset.current.set('description', oldDes);
        return false;
      }
    } catch (e) {
      return false;
    }
  }, [editDataset, onOk]);
  const handleChangeDes = (value) => {
    editDataset.current.set('description', value);
  };
  const onUploadFile = ({ file, fileList, event }) => {
    editDataset.current.set('fileList', fileList);
  };
  useEffect(() => {
    // 初始化属性
    modal.handleOk(handleUpdateIssue);
  }, [handleUpdateIssue, modal]);
  useEffect(() => {
  
  }, []);
  return (
    <Form dataSet={editDataset} className={`test-edit-execute-issue-form ${visibleDetail ? '' : 'test-edit-execute-issue-form-hidden'}`}>
      <TextField name="summary" />
      <div role="none" style={{ cursor: 'pointer' }} onClick={() => setVisibleDetail(!visibleDetail)}>
        <div className="test-edit-execute-issue-line" />
        <span className="test-edit-execute-issue-head">
          <Icon type={`${visibleDetail ? 'expand_less' : 'expand_more'}`} />
          用例详细信息
        </span>

      </div>
      <WYSIWYGEditor
        style={{ height: 200, width: '100%' }}
        onChange={handleChangeDes}
      />
      {/* //  这里逻辑待处理， DataSet提交  */}
      <div className="test-edit-execute-issue-form-file">
        <span className="test-edit-execute-issue-head">附件</span>
        <UploadButton
          fileList={editDataset.current && editDataset.current.get('cycleCaseAttachmentRelVOList')}
          onChange={onUploadFile}
        />
      </div>
      <div className="test-edit-execute-issue-form-step">
        <div className="test-edit-execute-issue-line" />
        <span className="test-edit-execute-issue-head">测试步骤</span>
        <EditTestStepTable name="caseStepVOS" parentDataSet={editDataset} executeId={executeId} />
      </div>
    </Form>
  );
}
export default withRouter(EditExecuteIssue);
