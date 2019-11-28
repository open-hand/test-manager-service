import React, {
  Component, useEffect, useContext, useRef,
} from 'react';
import {
  Icon, Card, Spin, Tooltip,
} from 'choerodon-ui';
import {
  Page, Header, Content, Breadcrumb,
} from '@choerodon/boot';
import { Choerodon } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import { withRouter } from 'react-router-dom';
import { FormattedMessage } from 'react-intl';
import _ from 'lodash';
import { Modal, Button } from 'choerodon-ui/pro/lib';
import { StatusTags } from '../../../components';
import {
  executeDetailLink, executeDetailShowLink, beforeTextUpload, getParams, TestExecuteLink, TestPlanLink,
} from '../../../common/utils';
import {
  editCycle, removeDefect,
} from '../../../api/ExecuteDetailApi';
import { uploadFile, deleteAttachment } from '../../../api/FileApi';
import './ExecuteDetail.less';
import {
  StepTable as OldStepTable, ExecuteDetailSide, CreateBug,
} from '../components';
import { StepTable } from './components';
import { QuickOperate, ExecuteHistoryTable } from './components';
import Store from '../stores';
import EditExecuteIssue from './components/EditExecuteIssue';

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
function ExecuteDetail(props) {
  const context = useContext(Store);
  const { ExecuteDetailStore, stepTableDataSet } = context;
  const ExecuteDetailSideRef = useRef(null);
  useEffect(() => {
    const { id } = context.match.params;
    ExecuteDetailStore.clearPagination();
    ExecuteDetailStore.getInfo(id);
  }, [ExecuteDetailStore, context.match.params]);

  const goExecute = (mode) => {
    const cycleData = ExecuteDetailStore.getCycleData;
    const { nextExecuteId, lastExecuteId } = cycleData;
    const { disabled, history } = props;
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
  };

  const handleToggleExecuteDetailSide = () => {
    const visible = ExecuteDetailStore.ExecuteDetailSideVisible;
    ExecuteDetailStore.setExecuteDetailSideVisible(!visible);
  };

  // 用于文件移除。 传入ExcuteDeailSide组件内， 在UploadButtonExcuteDetail组件内进行调用
  const handleFileRemove = (file) => {
    if (file.url) {
      ExecuteDetailStore.enterloading();
      deleteAttachment(file.uid).then((data) => {
        ExecuteDetailStore.getInfo();
        Choerodon.prompt('删除成功');
      }).catch((error) => {
        Choerodon.prompt(`删除失败 ${error}`);
      });
    }
  };

  const handleUpload = (files) => {
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
  };

  const handleSubmit = (updateData) => {
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
      if (ExecuteDetailSideRef) {
        ExecuteDetailSideRef.HideFullEditor();
      }
      ExecuteDetailStore.getInfo();
    }).catch((error) => {
      // console.log(error);
      Choerodon.prompt('网络异常');
    });
  };

  const handleCommentSave = (value) => {
    beforeTextUpload(value, {}, handleSubmit, 'comment');
  };

  const quickPassOrFail = (text) => {
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
  };

  const quickPass = (e) => {
    e.stopPropagation();
    quickPassOrFail('通过');
  };

  const quickFail = (e) => {
    e.stopPropagation();
    quickPassOrFail('失败');
  };

  const handleRemoveDefect = (issueId) => {
    ExecuteDetailStore.enterloading();
    removeDefect(issueId).then((res) => {
      ExecuteDetailStore.getInfo();
    }).catch((error) => {
      ExecuteDetailStore.unloading();
    });
  };

  const handleHiddenCreateBug = () => {
    ExecuteDetailStore.setCreateBugShow(false);
  };

  const handleBugCreate = () => {
    ExecuteDetailStore.setCreateBugShow(false);
    ExecuteDetailStore.getInfo();
  };

  const handleCreateBugShow = () => {
    ExecuteDetailStore.setCreateBugShow(true);
    ExecuteDetailStore.setDefectType('CYCLE_CASE');
    ExecuteDetailStore.setCreateDectTypeId(ExecuteDetailStore.id);
  };

  const handleOpenEdit = () => {
    const { intl } = context;
    Modal.open({
      key: 'editExecuteIssue',
      title: '修改执行',
      drawer: true,
      style: {
        width: 740,
      },
      children: (
        <EditExecuteIssue
          // onOk={this.handleCreateIssue.bind(this)}
          intl={intl}

        />
      ),
      footer: (okBtn, cancelBtn) => (
        <div>
          {okBtn}
          <Button funcType="raised" color="primary">保存并同步到用例库</Button>
          {cancelBtn}
        </div>
      ),
      okText: '保存',
    });
  };

  // 默认只显示15个字其余用... 进行省略
  const renderBreadcrumbTitle = (text) => {
    const ellipsis = '...';
    const textArr = [...text];
    return textArr.length > 15 ? textArr.slice(0, 15).join('') + ellipsis : text;
  };

  function render() {
    // disabled 用于禁止action列
    const { disabled } = props;
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
            <Button funcType="flat" type="primary" onClick={handleToggleExecuteDetailSide}>
              {/* <Icon type={visible ? 'format_indent_decrease' : 'format_indent_increase'} /> */}
              <Icon type="find_in_page" />
              {visible ? '隐藏详情' : '查看详情'}
            </Button>
            // {/* </div> */}
          )}
          <Button icon="mode_edit" funcType="flat" type="primary" onClick={handleOpenEdit}>修改用例</Button>
          <Button
            disabled={lastExecuteId === null}
            onClick={() => {
              goExecute('pre');
            }}
          >
            <Icon type="navigate_before" />
            <span><FormattedMessage id="execute_pre" /></span>
          </Button>
          <Button
            disabled={nextExecuteId === null}
            onClick={() => {
              goExecute('next');
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

        <Breadcrumb title={issueInfosVO ? renderBreadcrumbTitle(issueInfosVO.summary) : null} />
        <Content style={{ padding: visible ? '0 437px 0 0' : 0 }}>
          <Spin spinning={loading} style={{ display: 'flex' }}>
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

                    </div>
                  )}
                </div>
                {!disabled
                  && (
                    <QuickOperate
                      statusList={statusList}
                      quickPass={quickPass}
                      quickFail={quickFail}
                      onSubmit={handleSubmit}
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
                {/* <StepTable
                  dataSet={stepTableDataSet}
                /> */}
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
                  ref={ExecuteDetailSideRef}
                  issueInfosVO={issueInfosVO}
                  cycleData={cycleData}
                  fileList={fileList}
                  onFileRemove={handleFileRemove}
                  status={{ statusColor, statusName }}
                  onClose={handleToggleExecuteDetailSide}
                  onUpload={handleUpload}
                  onSubmit={handleSubmit}
                  onCommentSave={handleCommentSave}
                  onRemoveDefect={handleRemoveDefect}
                  onCreateBugShow={handleCreateBugShow}
                />
              )}
              {
                createBugShow && (
                  <CreateBug
                    visible={createBugShow}
                    defectType={defectType}
                    id={createDectTypeId}
                    onCancel={handleHiddenCreateBug}
                    onOk={handleBugCreate}
                  />
                )
              }
            </div>
          </Spin>
        </Content>

      </Page>
    );
  }
  return render();
}


export default withRouter(observer(ExecuteDetail));
