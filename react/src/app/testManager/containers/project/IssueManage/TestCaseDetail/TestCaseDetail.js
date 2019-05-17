/* eslint-disable react/destructuring-assignment */
import React, { Component } from 'react';
import { Page, Header } from '@choerodon/boot';
import {
  Button, Card, Spin, Icon, Tooltip,
} from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import _ from 'lodash';
import { testCaseDetailLink, getParams, testCaseTableLink } from '../../../../common/utils';
import {
  loadDatalogs, loadLinkIssues, loadIssue, getIssueSteps, getIssueExecutes,
} from '../../../../api/IssueManageApi';
import './TestCaseDetail.scss';
import IssueStore from '../../../../store/project/IssueManage/IssueStore';
import TestStepTable from '../../../../components/IssueManageComponent/TestStepTable/TestStepTable';
import TestExecuteTable from '../../../../components/IssueManageComponent/TestExecuteTable/TestExecuteTable';
import EditIssue from '../../../../components/IssueManageComponent/EditIssue/EditIssue';

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
    issueInfo: undefined,
    disabled: false,
    fileList: [],
    linkIssues: [],
    datalogs: [],
    testStepData: [],
    testExecuteData: [],
    loading: true,
    lasttestCaseId: null,
    nexttestCaseId: null,
    isExpand: true,
    folderName: '',
  }

  componentDidMount() {
    const { id } = this.props.match.params;
    const Request = getParams(this.props.location.search);
    const { folderName } = Request;
    this.setState({
      testCaseId: id,
      folderName,
    });
    const allIdValues = sessionStorage.allIdValues ? sessionStorage.allIdValues.split(',') : IssueStore.getIssueIds;
    let testCaseIdIndex;
    allIdValues.forEach((valueId, index) => {
      if (id === valueId) {
        testCaseIdIndex = index;
      }
    });
    this.setState({
      lasttestCaseId: testCaseIdIndex >= 1 ? allIdValues[testCaseIdIndex - 1] : null,
      nexttestCaseId: testCaseIdIndex <= allIdValues.length - 2 ? allIdValues[testCaseIdIndex + 1] : null,
    });

    this.reloadIssue(id);
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
    Promise.all([
      loadIssue(issueId),
      loadLinkIssues(issueId),
      loadDatalogs(issueId),
      getIssueSteps(issueId),
      getIssueExecutes(issueId),
    ]).then(([issue, linkIssues, datalogs, testStepData, testExecuteData]) => {
      const {
        issueAttachmentDTOList,
      } = issue;
      const fileList = _.map(issueAttachmentDTOList, issueAttachment => ({
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
    });
  }

  setFileList=(fileList) => {
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

    return (
      <Page className="c7ntest-testCaseDetail">
        <Header
          title={<FormattedMessage id="testCase_detail" />}
          backPath={testCaseTableLink()}
        >
          <Button
            disabled={lasttestCaseId === null}
            onClick={() => {
              this.goTestCase('pre');
            }}
          >
            <Icon type="navigate_before" />
            <span><FormattedMessage id="testCase_pre" /></span>
          </Button>
          <Button
            disabled={nexttestCaseId === null}
            onClick={() => {
              this.goTestCase('next');
            }}
          >
            <span><FormattedMessage id="testCase_next" /></span>
            <Icon type="navigate_next" />
          </Button>
          <Button onClick={() => {
            // this.props.history.replace('55');
            this.reloadIssue(testCaseId);
          }}
          >
            <Icon type="refresh" />
            <span><FormattedMessage id="refresh" /></span>
          </Button>
        </Header>

        <Spin spinning={loading}>
          <div style={{ display: 'flex', height: '100%' }}>
            <div style={{ overflowY: 'auto' }}>
              {
                <div style={{
                  display: 'flex', margin: '24px', fontSize: 20, height: '30px',
                }}
                >
                  <span>{issueInfo && issueInfo.summary}</span>
                  <div
                    role="none"
                    style={{
                      display: 'flex', alignItems: 'center', marginLeft: 20, color: '#3F51B5', fontSize: 14, cursor: 'pointer',
                    }}
                    onClick={() => {
                      this.setState({
                        isExpand: !isExpand,
                      });
                    }}
                  >
                    <Icon type={isExpand ? 'format_indent_increase' : 'format_indent_decrease'} style={{ verticalAlign: -2, fontSize: 15 }} />
                    <span style={{ display: 'inline-block', marginLeft: 3 }}>{isExpand ? '隐藏详情' : '显示详情'}</span>
                  </div>
                </div>
              }
              <Card
                title={null}
                style={{ marginBottom: 24, marginLeft: 24 }}
                bodyStyle={styles.cardBodyStyle}
              >
                <div style={{ ...styles.cardTitle, marginBottom: 10 }}>
                  {/* <Icon type="expand_more" /> */}
                  <span style={styles.cardTitleText}><FormattedMessage id="testCase_testDetail" /></span>
                  <span style={{ marginLeft: 5 }}>{`（${testStepData.length}）`}</span>
                </div>
                <TestStepTable
                  disabled={disabled}
                  issueId={testCaseId}
                  data={testStepData}
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
                  onOk={() => {
                    this.reloadIssue(testCaseId);
                  }}
                />
              </Card>
              <Card
                title={null}
                style={{ margin: 24, marginRight: 0 }}
                bodyStyle={styles.cardBodyStyle}
              >
                <div style={{ ...styles.cardTitle, marginBottom: 10 }}>
                  {/* <Icon type="expand_more" /> */}
                  <span style={styles.cardTitleText}><FormattedMessage id="testCase_testexecute" /></span>
                </div>
                <div>
                  <TestExecuteTable
                    issueId={testCaseId}
                    data={testExecuteData}
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
                    onOk={() => {
                      this.reloadIssue(testCaseId);
                    }}
                  />
                </div>
              </Card>
            </div>
            {
              isExpand && issueInfo && (
                <div style={{ height: '100%' }}>
                  <EditIssue
                    loading={loading}
                    issueId={testCaseId}
                    folderName={folderName}
                    issueInfo={issueInfo}
                    fileList={fileList}
                    setFileList={this.setFileList}
                    linkIssues={linkIssues}
                    datalogs={datalogs}
                    disabled={disabled}
                    reloadIssue={this.reloadIssue.bind(this, testCaseId)}
                    onClose={() => {
                      this.setState({
                        isExpand: false,
                      });
                    }}
                    mode="wide"
                  />
                </div>
              )
            }
          </div>
        </Spin>
      </Page>
    );
  }
}

export default TestCaseDetail;
