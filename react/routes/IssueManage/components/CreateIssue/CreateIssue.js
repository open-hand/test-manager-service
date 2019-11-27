import React, {
  useState, useEffect, useMemo, useCallback,
} from 'react';
import { withRouter } from 'react-router-dom';
import {
  Form, TextField, Select, DataSet, Icon,
} from 'choerodon-ui/pro';
import UploadButton from './UploadButton';
import { WYSIWYGEditor } from '../../../../components';
import CreateIssueDataSet from './store/CreateIssueDataSet';
import CreateTestStepTable from './CreateTestStepTable';
import SelectTree from '../SelectTree';
import { beforeTextUpload } from '../../../../common/utils';
import './CreateIssue.less';


function CreateIssue(props) {
  const [visibleDetail, setVisibleDetail] = useState(true);
  const { intl, caseId, defaultFolderValue } = props;
  const createDataset = useMemo(() => new DataSet(CreateIssueDataSet('issue', intl)), [intl]);


  async function handleCreateIssue() {
    const { onOk } = props;
    try {
      // 描述富文本转换为字符串
      const oldDes = createDataset.current.get('description');
      beforeTextUpload(oldDes, {}, des => createDataset.current.set('description', des.description));
      if (await createDataset.submit().then((res) => {
        onOk(res, createDataset.current.get('folderId'));
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
  }
  const handleChangeDes = (value) => {
    createDataset.current.set('description', value);
  };
  console.log('parents');

  useEffect(() => {
    // 初始化属性
    props.modal.handleOk(handleCreateIssue);
  }, [handleCreateIssue, props.modal]);

  return (
    <Form dataSet={createDataset} className={`test-create-issue-form ${visibleDetail ? '' : 'test-create-issue-form-hidden'}`}>
      <TextField name="summary" />
      <SelectTree name="folder" parentDataSet={createDataset} defaultValue={defaultFolderValue.id} />
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
        <UploadButton />
      </div>
      <div className="test-create-issue-form-step">
        <div className="test-create-issue-line" />
        <span className="test-create-issue-head">测试步骤</span>
        <CreateTestStepTable name="caseStepVOS" parentDataSet={createDataset} caseId={caseId} />
      </div>
    </Form>
  );
}
export default withRouter(CreateIssue);
