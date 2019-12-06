import React, {
  useState, useEffect, useMemo, useCallback, useContext,
} from 'react';
import { withRouter } from 'react-router-dom';
import {
  Form, TextField, Select, DataSet, Icon, Spin, message,
} from 'choerodon-ui/pro';
import { Choerodon } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import UploadButton from './UploadButton';
import { WYSIWYGEditor } from '@/components';
import EditIssueDataSet, { UpdateExecuteData } from './store/EditIssueDataSet';
import EditTestStepTable from './EditTestStepTable';
import { beforeTextUpload, text2Delta } from '@/common/utils';
// import { uploadFile } from '@/common/api/IssueManageApi';
import Store from '../../stores';
import './EditExecuteIssue.less';

function EditExecuteIssue(props) {
  const [visibleDetail, setVisibleDetail] = useState(true);
  const {
    intl, executeId, onOk, modal,
  } = props;
  const context = useContext(Store);
  const editDataset = useMemo(() => new DataSet(EditIssueDataSet(executeId, 'issue', intl)), [executeId, intl]);

  const handleUpdateIssue = useCallback(async () => {
    const { ExecuteDetailStore } = context;
    try {
      if (await editDataset.current.validate()) {
        await UpdateExecuteData(editDataset.current.toData());
      }
      message.success('修改成功');
      ExecuteDetailStore.getInfo();
      return true;
    } catch (e) {
      message.error(e);
      return false;
    }
  }, [context, editDataset]);
  const handleChangeDes = (value) => {
    editDataset.current.set('description', value);
  };
  const onUploadFile = ({ file, fileList, event }) => {
    // console.log('onUploadFile', file, fileList);
    const { status = 'ADD' } = file;
    // editDataset.current.get('cycleCaseAttachmentRelVOList').some(item=>{
    //   item.
    // });
    editDataset.current.set('fileList', fileList);
  };
  useEffect(() => {
    // 初始化属性
    modal.handleOk(handleUpdateIssue);
  }, [handleUpdateIssue, modal]);
  return (
    <Spin dataSet={editDataset}>
      <Form dataSet={editDataset} className={`test-edit-execute-issue-form ${visibleDetail ? '' : 'test-edit-execute-issue-form-hidden'}`}>
        <TextField name="summary" />
        <div role="none" style={{ cursor: 'pointer' }} onClick={() => setVisibleDetail(!visibleDetail)}>
          <div className="test-edit-execute-issue-line" />
          <span className="test-edit-execute-issue-head">
            <Icon type={`${visibleDetail ? 'expand_less' : 'expand_more'}`} />
            用例详细信息
          </span>

        </div>
        {(editDataset.current && (
          <WYSIWYGEditor
            style={{ height: 200, width: '100%' }}
            onChange={handleChangeDes}
            defaultValue={text2Delta(editDataset.current.get('description'))}
          />
        )
        )}
        {/* //  这里逻辑待处理， DataSet提交  */}
        <div className="test-edit-execute-issue-form-file">
          <span className="test-edit-execute-issue-head">附件</span>
          <UploadButton
            defaultFileList={editDataset.current ? [...editDataset.current.get('cycleCaseAttachmentRelVOList')] : []}
            onChange={onUploadFile}
          />
        </div>
        <div className="test-edit-execute-issue-form-step">
          <div className="test-edit-execute-issue-line" />
          <span className="test-edit-execute-issue-head">测试步骤</span>
          <EditTestStepTable name="testCycleCaseStepUpdateVOS" parentDataSet={editDataset} executeId={executeId} />
        </div>
      </Form>
    </Spin>
  );
}
export default withRouter(observer(EditExecuteIssue));
