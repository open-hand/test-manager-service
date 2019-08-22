/* eslint-disable react/state-in-constructor */
/* eslint-disable react/destructuring-assignment */
import React, { Component, Fragment } from 'react';
import { Page, Header } from '@choerodon/master';
import { withRouter } from 'react-router-dom';
import {
  Button, Card, Spin, Icon, Tooltip,
} from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import _ from 'lodash';
import { testCaseDetailLink, getParams, testCaseTableLink } from '../../../common/utils';
import {
  loadDatalogs, loadLinkIssues, loadIssue, getIssueSteps, getIssueExecutes,
} from '../../../api/IssueManageApi';
import './TestCaseDetail.scss';
import IssueStore from '../IssueManagestore/IssueStore';
import TestStepTable from '../IssueManageComponent/TestStepTable/TestStepTable';
import TestExecuteTable from '../IssueManageComponent/TestExecuteTable/TestExecuteTable';
import EditIssue from '../IssueManageComponent/EditIssue/EditIssue';

const styles = {
  cardTitle: {
    fontWeight: 500,
    display: 'flex',
  },
  cardTitleText: {
    lineHeight: '20px',
    marginLeft: '5px',
  },
  cardBodyStyle: {
    padding: 12,
  },
};

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
    folderName: '',
  }

  componentDidMount() {
    const { clickIssue } = this.props;
    const { issueId } = clickIssue;
    this.reloadIssue(issueId);
  }

  componentWillReceiveProps(nextProps) {
    const { clickIssue } = this.props;
    const { issueId } = clickIssue;
    if (nextProps.clickIssue.issueId && nextProps.clickIssue.issueId !== issueId) {
      this.reloadIssue(nextProps.clickIssue.issueId);
    }
  }

  /**
   *加载issue以及相关信息
   *
   * @param {*}
   * @memberof EditIssueNarrow
   */
  reloadIssue = (issueId) => {
    this.setState({
      loading: true,
    });
    const { onClose } = this.props;
    Promise.all([
      loadIssue(issueId),
      loadLinkIssues(issueId),
      loadDatalogs(issueId),
      getIssueSteps(issueId),
      getIssueExecutes(issueId),
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

  goTestCase = (mode) => {
    const { lasttestCaseId, nexttestCaseId } = this.state;
    const { disabled, history } = this.props;
    const toTestCaseId = mode === 'pre' ? lasttestCaseId : nexttestCaseId;
    const allIdValues = sessionStorage.allIdValues ? sessionStorage.allIdValues.split(',') : IssueStore.getIssueIds;
    let toTestCaseIdIndex;
    allIdValues.forEach((valueId, index) => {
      if (toTestCaseId === valueId) {
        toTestCaseIdIndex = index;
      }
    });

    if (toTestCaseId) {
      history.replace(testCaseDetailLink(toTestCaseId, IssueStore.getIssueFolderNames[toTestCaseIdIndex]));
    }
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
      folderName,
    } = this.state;
    const { clickIssue } = this.props;
    const { issueId } = clickIssue;
    const { onClose } = this.props;
    return (
      // eslint-disable-next-line react/jsx-fragments
      <Fragment>
        <div style={{ height: '100%' }}>
          <EditIssue
            loading={loading}
            issueId={issueId}
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
            reloadIssue={this.reloadIssue.bind(this, issueId)}
            onClose={onClose}
            onUpdate={this.handleUpdate}
            mode="wide"
          />
        </div>
      </Fragment>
    );
  }
}

export default withRouter(TestCaseDetail);
