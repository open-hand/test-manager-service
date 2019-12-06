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
import EditTestStepTable from './EditTestStepTable';
import { updateSidebarDetail } from '@/api/ExecuteDetailApi';
import { uploadFile } from '@/api/FileApi';
import { text2Delta, returnBeforeTextUpload } from '@/common/utils';
import './EditExecuteIssue.less';

function UpdateExecuteData(data) {
  const { executeId } = data;
  const testCycleCaseStepUpdateVOS = data.testCycleCaseStepUpdateVOS.map(
    (i) => {
      let { stepId } = i;
      let { executeStepId } = i;
      if (String(i.stepId).indexOf('.') !== -1) {
        stepId = 0;
        executeStepId = null;
      }
      return {
        ...i,
        stepId,
        executeId,
        executeStepId,
      };
    },
  );
  return new Promise((resolve) => {
    returnBeforeTextUpload(data.description, data, async (res) => {
      const newData = {
        ...res,
        fileList: [],
        caseStepVOS: [],
        testCycleCaseStepUpdateVOS,
      };
      const { fileList } = res;
      await updateSidebarDetail(newData);
      if (fileList) {
        const formDataAdd = new FormData();
        const formDataDel = new FormData();
        fileList.forEach((file) => {
          if (file.status && file.status === 'uploading') {
            formDataAdd.append('file', file);
          } else if (file.status && file.status === 'removed') {
            formDataDel.append('file', file);
          }
        });

        const config = {
          bucketName: 'test', attachmentLinkId: res.executeId, attachmentType: 'CYCLE_CASE',
        };
        await uploadFile(formDataAdd, config);
        // 缺少删除附件接口调用
      }
      resolve(true);
    });
  });
}

function EditExecuteIssue(props) {
  const [visibleDetail, setVisibleDetail] = useState(true);
  const {
    modal, ExecuteDetailStore, editDataset, executeId,
  } = props;

  const handleUpdateIssue = useCallback(async () => {
    try {
      if (editDataset.current && await editDataset.current.validate()) {
        await UpdateExecuteData(editDataset.current.toData());
        message.success('修改成功');
        ExecuteDetailStore.getInfo();
        return true;
      }
      return false;
    } catch (e) {
      message.error(e);
      return false;
    }
  }, [ExecuteDetailStore, editDataset]);
  const handleChangeDes = (value) => {
    editDataset.current.set('description', value);
  };
  const onUploadFile = ({ file, fileList, event }) => {
    // console.log('onUploadFile', file, fileList);
    const { status = 'ADD' } = file;
    // 缺少移除文件判断
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
