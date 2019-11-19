import React, { Component, useState, useEffect } from 'react';
import { Choerodon } from '@choerodon/boot';
import { stores, Content } from '@choerodon/boot';
import { withRouter } from 'react-router-dom';
import { find, debounce, map } from 'lodash';
import {
  Input, Modal, Collapse,
} from 'choerodon-ui';
import {
  Form, TextField, Select, TextArea, DataSet, Icon, Tree,
} from 'choerodon-ui/pro';
import { FormattedMessage } from 'react-intl';
import { UploadButton } from '../CommonComponent';
import {
  handleFileUpload, beforeTextUpload, validateFile, normFile,
} from '../../../../common/utils';
import { createIssue, getFoldersByVersion } from '../../../../api/IssueManageApi';
import IssueStore from '../../stores/IssueStore';
import {
  getLabels, getModules, getPrioritys, getProjectVersion,
} from '../../../../api/agileApi';
import { getUsers } from '../../../../api/IamApi';
import { WYSIWYGEditor } from '../../../../components';
import UserHead from '../UserHead';
import { getProjectName } from '../../../../common/utils';
import CreateIssueDataSet from './store/CreateIssueDataSet';
import TestStepTable from '../TestStepTable';
import SelectTree from '../CommonComponent/SelectTree';
import './CreateIssue.less';

const { AppState } = stores;
const { Option } = Select;
const { TreeNode } = Tree;

let sign = false;
function CreateIssue(props) {
  const [createLoading, setCreateLoading] = useState(false);
  const [selectLoading, setSelectLoading] = useState(false);
  const [originLabels, setOriginLabels] = useState([]);
  const [originComponents, setOriginComponents] = useState([]);
  const [originPriorities, setOriginPriorities] = useState([]);
  const [originFixVersions, setOriginFixVersions] = useState([]);
  const [originUsers, setOriginUsers] = useState([]);
  const [folders, setFolders] = useState([]);

  const [visibleDetail, setVisibleDetail] = useState(true);
  const [testStepData, setTestStepData] = useState([]);
  const { intl } = props;
  const createDataset = new DataSet(CreateIssueDataSet('issue', intl));
  const loadVersions = () => {
    const { setFieldsValue, defaultVersion } = props.form;
    getProjectVersion().then((res) => {
      setOriginFixVersions(res);
      setSelectLoading(false);
      if (find(res, { versionId: defaultVersion })) {
        setFieldsValue({ versionId: defaultVersion });
      }
    });
  };

  const debounceFilterIssues = debounce((input) => {
    setSelectLoading(true);
    getUsers(input).then((res) => {
      setOriginUsers(res.list);
      setSelectLoading(false);
    });
  }, 500);

  function onFilterChange(input) {
    if (!sign) {
      setSelectLoading(true);
      getUsers(input).then((res) => {
        setOriginUsers(res.list);
        setSelectLoading(false);
      });
      sign = true;
    } else {
      debounceFilterIssues(input);
    }
  }


  function loadPrioritys() {
    getPrioritys().then((priorities) => {
      const defaultPriority = find(priorities, { default: true });
      if (defaultPriority) {
        props.form.setFieldsValue({ priorityId: defaultPriority.id });
      }
      setOriginPriorities(priorities);
    });
  }

  const loadFolders = () => {
    const { getFieldValue } = props.form;
    if (getFieldValue('versionId')) {
      setSelectLoading(true);
      getFoldersByVersion(getFieldValue('versionId')).then((res) => {
        setFolders(res);
        setSelectLoading(false);
      });
    }
  };


  const handleSave = (data, fileList, folderId) => {
    createIssue(data, folderId)
      .then((res) => {
        if (fileList.length > 0) {
          const config = {
            issueType: res.statusId,
            issueId: res.issueId,
            fileName: fileList[0].name,
            projectId: AppState.currentMenuType.id,
          };
          if (fileList.some(one => !one.url)) {
            handleFileUpload(fileList, () => { }, config);
          }
        }
        props.onOk(data, folderId);
      });
  };

  const handleCreateIssue = () => {
    props.form.validateFields((err, values) => {
      if (!err) {
        const { description, fileList } = values;
        const exitComponents = originComponents;
        const componentIssueRelVOList = map(values.componentIssueRel, (component) => {
          const target = find(exitComponents, { name: component });
          if (target) {
            return target;
          } else {
            return ({
              name: component,
              projectId: AppState.currentMenuType.id,
            });
          }
        });
        const exitLabels = originLabels;
        const labelIssueRelVOList = map(values.issueLink, (label) => {
          const target = find(exitLabels, { labelName: label });
          if (target) {
            return target;
          } else {
            return ({
              labelName: label,
              projectId: AppState.currentMenuType.id,
            });
          }
        });
        const exitFixVersions = originFixVersions;
        const version = values.versionId;
        const target = find(exitFixVersions, { versionId: version });
        let fixVersionIssueRelVOList = [];
        if (target) {
          fixVersionIssueRelVOList = [{
            ...target,
            relationType: 'fix',
          }];
        } else {
          Choerodon.prompt('版本错误');
          return null;
        }
        const testType = IssueStore.getTestType;
        const extra = {
          typeCode: 'issue_test',
          issueTypeId: testType,
          summary: values.summary,
          priorityCode: `priority-${values.priorityId}`,
          priorityId: values.priorityId,
          sprintId: values.sprintId || 0,
          epicId: values.epicId || 0,
          epicName: values.epicName,
          parentIssueId: 0,
          assigneeId: values.assigneedId,
          labelIssueRelVOList,
          versionIssueRelVOList: fixVersionIssueRelVOList,
          componentIssueRelVOList,
        };
        setCreateLoading(true);
        const deltaOps = description;
        if (deltaOps) {
          beforeTextUpload(deltaOps, extra, handleSave.bind(this, extra, fileList, values.folderId));
        } else {
          extra.description = '';
          handleSave(extra, [], values.folderId);
        }
      }
      return null;
    });
  };
  // const renderOptions = ({ record, text, value })=>{

  //   console.log('renderOptions',record,value);
  //   return <span>009</span>
  // }
  useEffect(() => {
    // 初始化属性
    loadPrioritys();
    props.modal.handleOk(handleCreateIssue);
  }, []);

  function render() {
    return (
      <Form dataSet={createDataset} className="test-create-issue-form">
        <TextField name="summary" />
        <div role="none" style={{ cursor: 'pointer' }} onClick={() => setVisibleDetail(!visibleDetail)}>
          <div className="test-create-issue-line" />
          <span className="test-create-issue-head">
            {
              visibleDetail ? <Icon type="expand_less" /> : <Icon type="expand_more" />
            }

            用例详细信息
          </span>

        </div>
        {/** 这里逻辑待处理， DataSet提交 */}

        {visibleDetail && [
          <TextArea name="description" />,
          <div className="test-create-issue-form-file">
            <span className="test-create-issue-head">附件</span>
            <UploadButton />
          </div>,
          <SelectTree name="folderId" pDataSet={createDataset} />,
          <Select name="issueLink" />]
        }
        <div className="test-create-issue-form-step">
          <div className="test-create-issue-line" />
          <span className="test-create-issue-head">测试步骤</span>
          <TestStepTable
            disabled={false}
            data={testStepData}
            intl={intl}
          // enterLoad={() => console.log('enterLoad')}
          // leaveLoad={() => console.log('leaveLoad')}
          // onOk={() => console.log('onOk')}
          />
        </div>
      </Form>
    );
  }

  return render();
}
export default withRouter(CreateIssue);
