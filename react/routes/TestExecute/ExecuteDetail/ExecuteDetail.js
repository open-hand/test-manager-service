import React, { Component } from 'react';
import {
  Button, Icon, Card, Spin, Tooltip,
} from 'choerodon-ui';
import { TabPage as Page, Header, Breadcrumb } from '@choerodon/master';
import { observer } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { FormattedMessage } from 'react-intl';
import _ from 'lodash';
import { StatusTags } from '../../../components';
import {
  executeDetailLink, executeDetailShowLink, beforeTextUpload, getParams, TestExecuteLink, TestPlanLink,
} from '../../../common/utils';
import {
  editCycle, removeDefect,
} from '../../../api/ExecuteDetailApi';
import { uploadFile, deleteAttachment } from '../../../api/FileApi';
import './ExecuteDetail.scss';
import {
  StepTable, ExecuteDetailSide, CreateBug,
} from '../ExecuteComponent';
import { QuickOperate, ExecuteHistoryTable } from './components';
import ExecuteDetailStore from '../TestExecuteStore/ExecuteDetailStore';

function beforeUpload(file) {
  const isLt2M = file.size / 1024 / 1024 < 30;
  if (!isLt2M) {
    // console.log('不能超过30MB!');
  }
  return isLt2M;
}
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
const CardWrapper = ({ children, title, style }) => (
  <Card
    title={null}
    style={style}
    bodyStyle={styles.cardBodyStyle}
  >
    <div style={{ ...styles.cardTitle, marginBottom: 10 }}>
      <span style={styles.cardTitleText}>{title}</span>
    </div>
    {children}
  </Card>
);
@observer
class ExecuteDetail extends Component {
  componentDidMount() {
    // eslint-disable-next-line react/destructuring-assignment
    const { id } = this.props.match.params;
    ExecuteDetailStore.clearPagination();
    ExecuteDetailStore.getInfo(id);
  }

  saveRef = (name) => (ref) => {
    this[name] = ref;
  }

  goExecute = (mode) => {
    const cycleData = ExecuteDetailStore.getCycleData;
    const { nextExecuteId, lastExecuteId } = cycleData;
    const { disabled, history } = this.props;
    const toExecuteId = mode === 'pre' ? lastExecuteId : nextExecuteId;
    const { cycleId } = getParams(window.location.href);
    if (toExecuteId) {
      if (disabled) {
        history.replace(executeDetailShowLink(toExecuteId));
      } else {
        history.replace(executeDetailLink(toExecuteId, cycleId));
      }
      ExecuteDetailStore.clearPagination();
    }
  }

  handleToggleExecuteDetailSide = () => {
    const visible = ExecuteDetailStore.ExecuteDetailSideVisible;
    ExecuteDetailStore.setExecuteDetailSideVisible(!visible);
  }

  handleFileRemove = (file) => {
    if (file.url) {
      ExecuteDetailStore.enterloading();
      deleteAttachment(file.uid).then((data) => {
        ExecuteDetailStore.getInfo();
      });
    }
  }

  handleUpload = (files) => {
    if (beforeUpload(files[0])) {
      const formData = new FormData();
      [].forEach.call(files, (file) => {
        formData.append('file', file);
      });
      const config = {
        bucketName: 'test',
        comment: '',
        attachmentLinkId: ExecuteDetailStore.getCycleData.executeId,
        attachmentType: 'CYCLE_CASE',
      };
      ExecuteDetailStore.enterloading();
      uploadFile(formData, config).then(() => {
        ExecuteDetailStore.getInfo();
      }).catch(() => {
        Choerodon.prompt('网络异常');
      });
    }
  }

  handleCommentSave = (value) => {
    beforeTextUpload(value, {}, this.handleSubmit, 'comment');
  }

  handleSubmit = (updateData) => {
    const cycleData = ExecuteDetailStore.getCycleData;
    const newData = { ...cycleData, ...updateData };
    newData.assignedTo = newData.assignedTo || 0;
    // 删除一些不必要字段
    delete newData.defects;
    delete newData.caseAttachment;
    delete newData.testCycleCaseStepES;
    delete newData.lastRank;
    delete newData.nextRank;
    editCycle(newData).then((Data) => {
      if (this.ExecuteDetailSide) {
        this.ExecuteDetailSide.HideFullEditor();
      }
      ExecuteDetailStore.getInfo();
    }).catch((error) => {
      // console.log(error);
      Choerodon.prompt('网络异常');
    });
  }

  quickPass = (e) => {
    e.stopPropagation();
    this.quickPassOrFail('通过');
  }

  quickFail = (e) => {
    e.stopPropagation();
    this.quickPassOrFail('失败');
  }

  quickPassOrFail = (text) => {
    const cycleData = { ...ExecuteDetailStore.getCycleData };
    const { statusList } = ExecuteDetailStore;
    if (_.find(statusList, { projectId: 0, statusName: text })) {
      cycleData.executionStatus = _.find(statusList, { projectId: 0, statusName: text }).statusId;
      delete cycleData.defects;
      delete cycleData.caseAttachment;
      delete cycleData.testCycleCaseStepES;
      delete cycleData.lastRank;
      delete cycleData.nextRank;
      cycleData.assignedTo = cycleData.assignedTo || 0;
      ExecuteDetailStore.enterloading();
      editCycle(cycleData).then((Data) => {
        ExecuteDetailStore.getInfo();
      }).catch((error) => {
        ExecuteDetailStore.unloading();
        Choerodon.prompt('网络错误');
      });
    } else {
      Choerodon.prompt('未找到对应状态');
    }
  }

  handleRemoveDefect = (issueId) => {
    ExecuteDetailStore.enterloading();
    removeDefect(issueId).then((res) => {
      ExecuteDetailStore.getInfo();
    }).catch((error) => {
      ExecuteDetailStore.unloading();
    });
  }

  handleHiddenCreateBug = () => {
    ExecuteDetailStore.setCreateBugShow(false);
  }

  handleBugCreate = () => {
    ExecuteDetailStore.setCreateBugShow(false);
    ExecuteDetailStore.getInfo();
  }

  handleCreateBugShow = () => {
    ExecuteDetailStore.setCreateBugShow(true);
    ExecuteDetailStore.setDefectType('CYCLE_CASE');
    ExecuteDetailStore.setCreateDectTypeId(ExecuteDetailStore.id);
  }

  render() {
    const { disabled } = this.props;
    const { loading } = ExecuteDetailStore;
    const detailList = ExecuteDetailStore.getDetailList;
    const historyList = ExecuteDetailStore.getHistoryList;
    const historyPagination = ExecuteDetailStore.getHistoryPagination;
    const cycleData = ExecuteDetailStore.getCycleData;
    const visible = ExecuteDetailStore.ExecuteDetailSideVisible;
    const fileList = ExecuteDetailStore.getFileList;
    const statusList = ExecuteDetailStore.getStatusList;
    const createBugShow = ExecuteDetailStore.getCreateBugShow;
    const defectType = ExecuteDetailStore.getDefectType;
    const createDectTypeId = ExecuteDetailStore.getCreateDectTypeId;
    const {
      nextExecuteId, lastExecuteId, issueInfosVO, executionStatus,
    } = cycleData;
    const { statusColor, statusName } = ExecuteDetailStore.getStatusById(executionStatus);
    const stepStatusList = ExecuteDetailStore.getStepStatusList;
    return (
      <Page className="c7ntest-ExecuteDetail">
        <Header
          title={<FormattedMessage id="execute_detail" />}
        // backPath={disabled ? TestPlanLink() : TestExecuteLink()}
        >
          {issueInfosVO && (
            // <div style={{ display: 'flex', alignItems: 'center' }}>
            <Button funcType="flat" type="primary" onClick={this.handleToggleExecuteDetailSide}>
              {/* <Icon type={visible ? 'format_indent_decrease' : 'format_indent_increase'} /> */}
              <Icon type="find_in_page" />
              {visible ? '隐藏详情' : '查看详情'}
            </Button>
            // {/* </div> */}
          )}
          <Button
            disabled={lastExecuteId === null}
            onClick={() => {
              this.goExecute('pre');
            }}
          >
            <Icon type="navigate_before" />
            <span><FormattedMessage id="execute_pre" /></span>
          </Button>
          <Button
            disabled={nextExecuteId === null}
            onClick={() => {
              this.goExecute('next');
            }}
          >
            <span><FormattedMessage id="execute_next" /></span>
            <Icon type="navigate_next" />
          </Button>
          {/* <Button onClick={() => {
            ExecuteDetailStore.getInfo();
          }}
          >
            <Icon type="autorenew icon" />
            <span><FormattedMessage id="refresh" /></span>
          </Button> */}

        </Header>

        <Breadcrumb title={issueInfosVO ? issueInfosVO.summary : null} />
        <Spin spinning={loading}>
          <div style={{ display: 'flex', width: '100%', height: '100%' }}>
            {/* 左边内容区域 */}
            <div
              style={{
                flex: 1,
                overflowX: 'hidden',
                overflowY: 'auto',
                padding: 20,
              }}
            >
              <div style={{ marginBottom: 24 }}>
                {issueInfosVO && (
                  <div style={{ display: 'flex', alignItems: 'center' }}>
                    <StatusTags
                      style={{ height: 20, lineHeight: '20px', marginRight: 15 }}
                      color={statusColor}
                      name={statusName}
                    />
                    <span style={{ fontSize: '20px' }}>{issueInfosVO.summary}</span>
                    {/* <Button funcType="flat" type="primary" onClick={this.handleToggleExecuteDetailSide} style={{ marginLeft: 15 }}>
                      <Icon type={visible ? 'format_indent_decrease' : 'format_indent_increase'} />
                      {visible ? '隐藏详情' : '打开详情'}
                    </Button> */}
                  </div>
                )}
              </div>
              {!disabled
                && (
                  <QuickOperate
                    statusList={statusList}
                    quickPass={this.quickPass}
                    quickFail={this.quickFail}
                    onSubmit={this.handleSubmit}
                  />
                )}
              <CardWrapper
                style={{ margin: '24px 0' }}
                title={[<FormattedMessage id="execute_testDetail" />, <span style={{ marginLeft: 5 }}>{`（${detailList.length}）`}</span>]}
              >
                <StepTable
                  disabled={disabled}
                  dataSource={detailList}
                  stepStatusList={stepStatusList}
                />
              </CardWrapper>
              <CardWrapper title={<FormattedMessage id="execute_executeHistory" />}>
                <div style={{ padding: '0 20px' }}>
                  <ExecuteHistoryTable
                    dataSource={historyList}
                    pagination={historyPagination}
                    onChange={ExecuteDetailStore.loadHistoryList}
                  />
                </div>
              </CardWrapper>
            </div>
            {/* 右侧侧边栏 */}
            {visible && (
              <ExecuteDetailSide
                disabled={disabled}
                ref={this.saveRef('ExecuteDetailSide')}
                issueInfosVO={issueInfosVO}
                cycleData={cycleData}
                fileList={fileList}
                onFileRemove={this.handleFileRemove}
                status={{ statusColor, statusName }}
                onClose={this.handleToggleExecuteDetailSide}
                onUpload={this.handleUpload}
                onSubmit={this.handleSubmit}
                onCommentSave={this.handleCommentSave}
                onRemoveDefect={this.handleRemoveDefect}
                onCreateBugShow={this.handleCreateBugShow}
              />
            )}
            {
              createBugShow && (
                <CreateBug
                  visible={createBugShow}
                  defectType={defectType}
                  id={createDectTypeId}
                  onCancel={this.handleHiddenCreateBug}
                  onOk={this.handleBugCreate}
                />
              )
            }
          </div>
        </Spin>
      </Page>
    );
  }
}


export default withRouter(ExecuteDetail);
