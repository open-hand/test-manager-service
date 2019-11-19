/* eslint-disable no-console */
/* eslint-disable react/no-unused-state */
import React, { Component, Fragment } from 'react';
import { Choerodon } from '@choerodon/boot';
import { withRouter } from 'react-router-dom';
import { stores, Permission } from '@choerodon/boot';
import _ from 'lodash';
import { FormattedMessage } from 'react-intl';
import { throttle } from 'lodash';
import {
  Input, Button, Modal, Tooltip, Dropdown, Menu, Spin, Icon, Tabs,
} from 'choerodon-ui';

import './EditIssue.less';
import { TextEditToggle, User, ResizeAble } from '../../../../components';
import {
  delta2Html, handleFileUpload, text2Delta, beforeTextUpload, formatDate,
  returnBeforeTextUpload, color2rgba, testCaseTableLink, commonLink, testCaseDetailLink,
} from '../../../../common/utils';
import Timeago from '../../../../components/DateTimeAgo/DateTimeAgo';
import {
  loadDatalogs, loadLinkIssues, loadIssue, updateStatus, updateIssue,
  createCommit, deleteIssue, loadStatus, cloneIssue, getIssueSteps, getIssueExecutes,
} from '../../../../api/IssueManageApi';
import { getUsers, getUpdateProjectInfoPermission } from '../../../../api/IamApi';
import { FullEditor, WYSIWYGEditor } from '../../../../components';
import CreateLinkTask from '../CreateLinkTask';
import Comment from './Component/Comment';
import DataLogs from './Component/DataLogs';
import TypeTag from '../TypeTag';
import TestStepTable from '../TestStepTable';
import EditTestStepTable from './Component/EditTestStepTable';
import EditDetail from './EditDetail';

const { AppState, HeaderStore } = stores;
const { TextArea } = Input;
const { TabPane } = Tabs;
let sign = true;
let filterSign = false;
const { Text, Edit } = TextEditToggle;
const navs = [
  { code: 'detail', tooltip: '详情', icon: 'error_outline' },
  { code: 'des', tooltip: '描述', icon: 'subject' },
  // { code: 'test_step', tooltip: '测试详细信息', icon: 'compass' },
  // { code: 'test_execute', tooltip: '测试执行', icon: 'explicit2' },
  { code: 'attachment', tooltip: '附件', icon: 'attach_file' },
  { code: 'commit', tooltip: '评论', icon: 'sms_outline' },
  { code: 'data_log', tooltip: '活动日志', icon: 'insert_invitation' },
  { code: 'link_task', tooltip: '关联问题', icon: 'link' },
];
const STATUS_ICON = {
  done: {
    icon: 'check_circle',
    color: '#1bb06e',
    bgColor: '',
  },
  todo: {
    icon: 'watch_later',
    color: '#4a93fc',
    bgColor: '',
  },
  doing: {
    icon: 'timelapse',
    color: '#ffae02',
    bgColor: '',
  },
};
const ICON_COLOR = {
  todo: 'rgba(255, 177, 0, 0.2)',
  doing: 'rgba(77,144,254,0.2)',
  done: 'rgba(0,191,165,0.2)',
};
const STATUS = {
  todo: '#ffb100',
  doing: '#4d90fe',
  done: '#00bfa5',
};
class EditIssueNarrow extends Component {
  constructor() {
    super();
    this.container = React.createRef();
  }

  state = {
    selectLoading: true,
    FullEditorShow: false,
    createLinkTaskShow: false,
    editDescriptionShow: false,
    showMore: false,
    addingComment: false,
    currentNav: 'detail',
    StatusList: [],
    priorityList: [],
    componentList: [],
    labelList: [],
    userList: [],
    hasDeletePermission: false,
  }


  componentDidMount() {
    const { loading } = this.props;
    if (this.props.onRef) {
      this.props.onRef(this);
    }

    getUpdateProjectInfoPermission().then((res) => {
      this.setState({
        hasDeletePermission: res[0].approve,
      });
    });

    // document.getElementById('scroll-area').addEventListener('scroll', (e) => {
    //   if (sign) {
    //     const currentNav = this.getCurrentNav(e);
    //     if (this.state.currentNav !== currentNav && currentNav) {
    //       this.setState({
    //         currentNav,
    //       });
    //     }
    //   }
    // });
    this.setQuery();
  }

  getCurrentNav(e) {
    return _.find(navs.map(nav => nav.code), i => this.isInLook(document.getElementById(i)));
  }

  isInLook(ele) {
    const a = ele.offsetTop;
    const target = document.getElementById('scroll-area');
    // return a >= target.scrollTop && a < (target.scrollTop + target.offsetHeight);
    return a + ele.offsetHeight > target.scrollTop;
  }

  scrollToAnchor = (anchorName) => {
    if (anchorName) {
      const anchorElement = document.getElementById(anchorName);
      if (anchorElement) {
        sign = false;
        anchorElement.scrollIntoView({
          behavior: 'smooth',
          block: 'start',
          // inline: "nearest",
        });
        setTimeout(() => {
          sign = true;
        }, 2000);
      }
    }
  }

  onFilterChange(input) {
    if (!filterSign) {
      this.setState({
        selectLoading: true,
      });
      getUsers(input).then((res) => {
        this.setState({
          userList: res.list,
          selectLoading: false,
        });
      });
      filterSign = true;
    } else {
      this.debounceFilterIssues(input);
    }
  }


  debounceFilterIssues = _.debounce((input) => {
    this.setState({
      selectLoading: true,
    });
    getUsers(input).then((res) => {
      this.setState({
        userList: res.list,
        // eslint-disable-next-line react/no-unused-state
        selectLoading: false,
      });
    });
  }, 500);

  /**
   *多选提交前的准备，因为可以手动输入，所以会有原先不存在的值提交，后台会自动新建
   *
   * @memberof EditIssueNarrow
   */
  prepareMutilSelectValueBeforeSubmit = (selected, fromList, key) => selected.map((item) => {
    const exist = _.find(fromList, { name: item });
    // 如果已有则返回已有值，如果不存在，则返回name和projectId
    if (exist) {
      return exist;
    } else {
      const prepared = { projectId: AppState.currentMenuType.id };
      prepared[key] = item;
      return prepared;
    }
  })


  handleCreateLinkIssue() {
    this.props.reloadIssue();
    if (this.props.onUpdate) {
      this.props.onUpdate();
    }
  }

  handleCopyIssue() {
    this.props.reloadIssue();
    if (this.props.onUpdate) {
      this.props.onUpdate();
    }
    if (this.props.onCopyAndTransformToSubIssue) {
      this.props.onCopyAndTransformToSubIssue();
    }
  }

  handleLinkToTestCase = () => {
    const { history } = this.props;
    history.push(testCaseTableLink());
  }

  handleLinkToNewIssue = (issueId) => {
    const { history, folderName } = this.props;
    history.push(testCaseDetailLink(issueId, folderName));
  }

  /**
   * DataLog
   */
  renderDataLogs() {
    const { datalogs, issueInfo } = this.props;
    const {
      createdBy,
      createrImageUrl, createrEmail,
      createrName, createrRealName, creationDate, issueTypeVO = {},
    } = issueInfo;
    const createLog = {
      email: createrEmail,
      field: issueTypeVO.typeCode,
      imageUrl: createrImageUrl,
      name: createrName,
      realName: createrRealName,
      lastUpdateDate: creationDate,
      lastUpdatedBy: createdBy,
      newString: 'issueNum',
      newValue: 'issueNum',
    };

    return (
      <DataLogs
        datalogs={[...datalogs, createLog]}
      />
    );
  }

  checkDisabledModifyOrDelete = () => {
    const loginUserId = AppState.userInfo.id;
    const { issueInfo } = this.props;
    const { hasDeletePermission } = this.state;
    return !(loginUserId === issueInfo.createdBy || hasDeletePermission);
  }

  handleResizeEnd = ({ width }) => {
    localStorage.setItem('agile.EditIssue.width', `${width}px`);
  }

  setQuery = (width = this.container.current.clientWidth) => {
    if (width <= 600) {
      this.container.current.setAttribute('max-width', '600px');
    } else {
      this.container.current.removeAttribute('max-width');
    }
  }

  handleResize = throttle(({ width }) => {
    this.setQuery(width);
    // console.log(width, parseInt(width / 100) * 100);
  }, 150)

  /**
*更新用例信息
* @param newValue 例 { statusId: 1 }
* @memberof EditIssueNarrow
*/
  editIssue = (newValue, done) => {
    console.log('editIssue', newValue);
    const key = Object.keys(newValue)[0];
    const value = newValue[key];
    const {
      StatusList, componentList, labelList,
    } = this.state;
    const { issueInfo } = this.props;
    const { issueId, objectVersionNumber } = issueInfo;

    let issue = {
      issueId,
      objectVersionNumber,
    };
    switch (key) {
      case 'statusId': {
        const targetStatus = _.find(StatusList, { endStatusId: value });
        if (targetStatus) {
          updateStatus(targetStatus.id, issue.issueId, issue.objectVersionNumber)
            .then((res) => {
              this.props.reloadIssue();
              if (this.props.onUpdate) {
                this.props.onUpdate();
              }
            }).catch(() => {
              done();
            });
        }
        break;
      }
      case 'componentIssueRelVOList': {
        issue.componentIssueRelVOList = this.prepareMutilSelectValueBeforeSubmit(value, componentList, 'name');
        updateIssue(issue)
          .then((res) => {
            this.props.reloadIssue();
            if (this.props.onUpdate) {
              this.props.onUpdate();
            }
          }).catch(() => {
            done();
          });
        break;
      }
      case 'labelIssueRelVOList': {
        issue.labelIssueRelVOList = this.prepareMutilSelectValueBeforeSubmit(value, labelList, 'labelName');
        updateIssue(issue)
          .then((res) => {
            this.props.reloadIssue();
            if (this.props.onUpdate) {
              this.props.onUpdate();
            }
          }).catch(() => {
            done();
          });
        break;
      }
      case 'description': {
        if (value) {
          returnBeforeTextUpload(value, issue, updateIssue, 'description')
            .then((res) => {
              this.props.reloadIssue();
            }).catch(() => {
              done();
            });
        }
        break;
      }
      default: {
        if (key === 'summary' && value === '') {
          Choerodon.prompt('用例名不可为空！');
          done();
          break;
        }
        issue = { ...issue, ...newValue };
        updateIssue(issue)
          .then((res) => {
            this.props.reloadIssue();
            if (this.props.onUpdate) {
              this.props.onUpdate();
            }
          }).catch(() => {
            done();
          });
        break;
      }
    }
  }

  render() {
    const {
      FullEditorShow, createLinkTaskShow,
      currentNav, showMore, addingComment,
    } = this.state;
    const {
      loading, issueId, issueInfo, fileList, setFileList, disabled, linkIssues, folderName, testStepData, testExecuteData,
      leaveLoad, enterLoad, reloadIssue,
    } = this.props;
    const {
      issueNum, summary, creationDate, lastUpdateDate, description,
      priorityVO, issueTypeVO, statusVO, versionIssueRelVOList,
      issueAttachmentVOList,
    } = issueInfo || {};
    const {
      name: statusName, id: statusId, colour: statusColor, icon: statusIcon,
      type: statusCode,
    } = statusVO || {};
    const { colour: priorityColor } = priorityVO || {};


    const fixVersionsTotal = _.filter(versionIssueRelVOList, { relationType: 'fix' }) || [];
    const fixVersionsFixed = _.filter(fixVersionsTotal, { statusCode: 'archived' }) || [];
    const fixVersions = _.filter(fixVersionsTotal, v => v.statusCode !== 'archived') || [];
    const menu = AppState.currentMenuType;
    const {
      type, id: projectId, organizationId: orgId, name,
    } = menu;
    const { mode } = this.props;
    return (
      <div style={{
        position: 'fixed',
        right: 0,
        top: HeaderStore.announcementClosed ? 50 : 100,
        bottom: 0,
        zIndex: 101,
        overflowY: 'hidden',
        overflowX: 'visible',
      }}
      >
        <ResizeAble
          modes={['left']}
          size={{
            maxWidth: window.innerWidth * 0.6,
            minWidth: 440,
          }}
          defaultSize={{
            width: localStorage.getItem('agile.EditIssue.width') || 600,
            height: '100%',
          }}
          onResizeEnd={this.handleResizeEnd}
          onResize={this.handleResize}
        >
          <div className="c7ntest-editIssue" ref={this.container}>
            <div className="c7ntest-editIssue-divider" />
            {
              loading ? (
                <div
                  style={{
                    position: 'absolute',
                    top: 0,
                    bottom: 0,
                    left: 0,
                    right: 0,
                    background: 'rgba(255, 255, 255, 0.65)',
                    zIndex: 9999,
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                  }}
                >
                  <Spin />
                </div>
              ) : null
            }
            <div className="c7ntest-content">
              <div className="c7ntest-content-top">
                <div className="c7ntest-header-editIssue">
                  <div className="c7ntest-content-editIssue" style={{ overflowY: 'hidden' }}>
                    <div
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        paddingLeft: '20px',
                        paddingRight: '20px',
                        marginLeft: '-20px',
                        marginRight: '-20px',
                        height: 44,
                      }}
                    >
                      <div style={{
                        height: 44, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
                      }}
                      >
                        <TypeTag data={issueTypeVO} />
                      </div>
                      {/* issueNum 用例编号 */}
                      <div style={{
                        fontSize: 16, lineHeight: '28px', fontWeight: 500, marginLeft: 15,
                      }}
                      >
                        <span>{issueNum}</span>
                      </div>
                      <div
                        style={{
                          cursor: 'pointer', fontSize: '13px', lineHeight: '20px', display: 'flex', alignItems: 'center', marginLeft: 'auto',
                        }}
                        role="none"
                        onClick={() => this.props.onClose()}
                      >
                        <Icon type="last_page" style={{ fontSize: '18px', fontWeight: '500' }} />
                        <FormattedMessage id="issue_edit_hide" />
                      </div>
                    </div>
                    <div className="line-justify" style={{ marginBottom: 10, alignItems: 'center', marginTop: 10 }}>
                      <TextEditToggle
                        disabled={disabled}
                        style={{ width: '100%' }}
                        formKey="summary"
                        onSubmit={(value, done) => { this.editIssue({ summary: value }, done); }}
                        originData={summary}
                      >
                        <Text>
                          {data => (
                            <div className="c7ntest-summary">
                              {data}
                            </div>
                          )}
                        </Text>
                        <Edit>
                          <TextArea style={{ fontSize: '20px', fontWeight: 500, padding: '0.04rem' }} maxLength={44} autosize autoFocus />
                        </Edit>
                      </TextEditToggle>
                    </div>
                  </div>
                </div>
              </div>
              <div className="c7ntest-content-bottom" id="scroll-area" style={{ position: 'relative' }}>
                <section className="c7ntest-body-editIssue">
                  <div className="c7ntest-content-editIssue">
                    <Tabs>
                      <TabPane tab="步骤" key="test">
                        <EditTestStepTable
                          disabled={disabled}
                          issueId={issueId}
                          data={testStepData}
                          enterLoad={enterLoad}
                          leaveLoad={leaveLoad}
                          reloadIssue={reloadIssue}
                        />
                      </TabPane>
                      <TabPane tab="详情" key="detail">
                        <EditDetail
                          linkIssues={linkIssues}
                          reloadIssue={reloadIssue}
                          folderName={folderName}
                          issueInfo={issueInfo}
                          createLinkIssue={this.handleCreateLinkIssue.bind(this)}
                          checkDisabledModifyOrDelete={this.checkDisabledModifyOrDelete}
                          editIssue={this.editIssue}
                          setFileList={setFileList}
                          fileList={fileList}
                        />
                      </TabPane>
                      <TabPane tab="记录" key="log">
                        {this.renderDataLogs()}
                      </TabPane>
                    </Tabs>
                  </div>
                </section>
              </div>
            </div>
          </div>
        </ResizeAble>
      </div>
    );
  }
}
export default withRouter(EditIssueNarrow);
