import React, { Component, Fragment } from 'react';
import { Choerodon } from '@choerodon/boot';
import Animate from 'choerodon-ui/lib/animate';
import _ from 'lodash';
import {
  loadDatalogs, loadLinkIssues, loadIssue, getIssueSteps, getIssueExecutes,
} from '../../../api/IssueManageApi';
import './TestCaseDetail.scss';
import IssueStore from '../stores/IssueStore';
import EditIssue from '../components/EditIssue/EditIssue';


class TestCaseDetail extends Component {
  state = {
    testCaseId: undefined,
    issueInfo: {},
    disabled: false,
    fileList: [],
    linkIssues: [],
    datalogs: [],
    testStepData: [],
    testExecuteData: [],
    loading: true,
    lasttestCaseId: null,
    nexttestCaseId: null,
    isExpand: false,
  }

  componentDidMount() {
    const { clickIssue } = this.props;
    const { caseId } = clickIssue;
    this.reloadIssue(caseId);
  }

  componentWillReceiveProps(nextProps) {
    const { clickIssue } = this.props;
    const { caseId } = clickIssue;
    if (nextProps.clickIssue.caseId && nextProps.clickIssue.caseId !== caseId) {
      this.reloadIssue(nextProps.clickIssue.caseId);
    }
  }

  /**
   *加载issue以及相关信息
   *
   * @param {*}
   * @memberof EditIssueNarrow
   */
  reloadIssue = (caseId) => {
    this.setState({
      loading: true,
    });
    const { onClose } = this.props;
    Promise.all([
      loadIssue(caseId),
      loadLinkIssues(caseId),
      loadDatalogs(caseId),
      getIssueSteps(caseId),
      getIssueExecutes(caseId),
    ]).then(([issue, linkIssues, datalogs, testStepData, testExecuteData]) => {
      const {
        issueAttachmentVOList,
      } = issue;
      const fileList = _.map(issueAttachmentVOList, issueAttachment => ({
        uid: issueAttachment.attachmentId,
        name: issueAttachment.fileName,
        url: issueAttachment.url,
      }));
      this.setState({
        issueInfo: issue,
        disabled: issue.typeCode === 'issue_auto_test',
        fileList,
        linkIssues,
        datalogs,
        testStepData: testStepData.map(step => ({
          ...step,
          stepIsCreating: false,
        })),
        testExecuteData,
        loading: false,
      });
    }).catch((error) => {
      Choerodon.prompt('加载用例错误');
      onClose();
    });
  }

  setFileList = (fileList) => {
    this.setState({
      fileList,
    });
  }

  handleUpdate=() => {
    IssueStore.loadIssues();
  }

  render() {
    const {
      testCaseId,
      issueInfo,
      disabled,
      fileList,
      linkIssues,
      datalogs,
      testStepData,
      testExecuteData,
      loading,
      lasttestCaseId,
      nexttestCaseId,
      isExpand,
      // folderName,
    } = this.state;
    const { clickIssue } = this.props;
    const { caseId, folderName } = clickIssue;
    const { onClose } = this.props;
    return (    
      <EditIssue
        loading={loading}
        issueId={caseId}
        folderName={folderName}
        issueInfo={issueInfo}
        testStepData={testStepData}
        testExecuteData={testExecuteData}
        enterLoad={() => {
          this.setState({
            loading: true,
          });
        }}
        leaveLoad={() => {
          this.setState({
            loading: false,
          });
        }}
        fileList={fileList}
        setFileList={this.setFileList}
        linkIssues={linkIssues}
        datalogs={datalogs}
        disabled={disabled}
        reloadIssue={this.reloadIssue.bind(this, caseId)}
        onClose={onClose}
        onUpdate={this.handleUpdate}
        mode="wide"
      />    
    );
  }
}
export default ({ visible, ...restProps }) => (
  <Animate
// key={props.key}
    component="div"
    transitionAppear
    transitionName="slide-right"
    hiddenProp="hidden"
  >
    {visible && <TestCaseDetail {...restProps} />}
  </Animate>
);
