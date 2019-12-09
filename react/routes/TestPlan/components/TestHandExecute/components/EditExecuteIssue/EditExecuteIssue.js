import React, {
  useState, useEffect, useCallback,
} from 'react';
import { withRouter } from 'react-router-dom';
import {
  Form, TextField, Icon, Spin, message,
} from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import UploadButton from './UploadButton';
import { WYSIWYGEditor } from '@/components';
import EditTestStepTable from './EditTestStepTable';
import { updateSidebarDetail } from '@/api/ExecuteDetailApi';
import { uploadFile, deleteFile } from '@/api/FileApi';
import { text2Delta, returnBeforeTextUpload } from '@/common/utils';
import './EditExecuteIssue.less';

/**
 * 批量删除已上传文件（修改用例 保存）
 * @param {*} files 
 */
async function deleteFiles(files = []) {
  files.forEach((file) => {
    deleteFile(file);
  });
  return true;
}
/**
 * 更新执行用例数据
 * @param {*} data 
 */
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
        const formDataDel = [];
        fileList.forEach((file) => {
          if (!file.status) {
            formDataAdd.append('file', file);
          } else if (file.status && file.status === 'removed') {
            formDataDel.push(file);
          }
        });

        const config = {
          description: '', executeId: res.executeId, attachmentType: 'CYCLE_CASE',
        };
        if (formDataAdd.has('file')) {
          await uploadFile(formDataAdd, config);
        }
        // 删除文件 只能单个文件删除， 进行遍历删除
        await deleteFiles(formDataDel.map(i => i.id));
      }
      message.success('修改成功');
      resolve(true);
    });
  });
}

function EditExecuteIssue(props) {
  const [visibleDetail, setVisibleDetail] = useState(true);
  const {
    modal, editDataset, executeId,
  } = props;

  const handleUpdateIssue = useCallback(async () => {
    try {
      if (editDataset.current && await editDataset.current.validate()) {
        if (editDataset.current.status !== 'sync') {
          if (await UpdateExecuteData(editDataset.current.toData())) {
            return true;
          } else {
            message.info('修改失败');
          }
        }
        message.info('未做任何修改');
      }
      return false;
    } catch (e) {
      message.error(e);
      return false;
    }
  }, [editDataset]);

  const handleChangeDes = (value) => {
    editDataset.current.set('description', value);
  };

  const onUploadFile = ({ file }) => {
    // console.log('onUploadFile', file, fileList);
    const { status = 'add', size } = file;
    // remove操作的file是新文件 则进行文件列表直接赋值操作，否则 则进行标记 
    const oldFileList = editDataset.current.get('fileList') || [];
    if (status === 'removed' && !size) {
      editDataset.current.set('fileList', [...oldFileList, file]);
    } else if (size) {
      const newFileList = [...oldFileList];
      if (status === 'add') {
        newFileList.push(file);
        editDataset.current.set('fileList', newFileList);
      } else if (status === 'removed') {
        editDataset.current.set('fileList', newFileList.filter(item => item.uid !== file.uid));
      }
    }
  };
  useEffect(() => {
    // 初始化属性
    modal.handleOk(handleUpdateIssue);
  }, [handleUpdateIssue, modal]);
  useEffect(() => {
    if (!editDataset.current) {
      editDataset.query();
    }
  }, [editDataset]);
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
          {editDataset.current && (
            <UploadButton
              defaultFileList={[...editDataset.current.get('cycleCaseAttachmentRelVOList')]}
              onChange={onUploadFile}
            />
          )}
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
