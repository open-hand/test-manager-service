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
import queryString from 'query-string';
import { StatusTags } from '../../../../components';
import {
  executeDetailLink, executeDetailShowLink, beforeTextUpload, getParams, TestExecuteLink, TestPlanLink,
} from '../../../../common/utils';
import {
  editCycle, removeDefect,
} from '../../../../api/ExecuteDetailApi';
import { uploadFile } from '../../../../api/IssueManageApi';
import './TestHandExecute.less';
import {
  ExecuteDetailSide, CreateBug, StepTable, QuickOperate, ExecuteHistoryTable,
} from './components';
import Store from './stores';
import EditExecuteIssue from './components/EditExecuteIssue';

function beforeUpload(file) {
  const isLt2M = file.size / 1024 / 1024 < 30;
  if (!isLt2M) {
    // console.log('不能超过30MB!');
  }
  return isLt2M;
}

const CardWrapper = ({ children, title, style }) => (
  <Card
    title={null}
    style={style}
    bodyStyle={{ paddingBottom: 0 }}
    bordered={false}
  >
    <span className="c7n-test-execute-detail-card-title">{title}</span>
    {children}
  </Card>
);
function TestHandExecute(props) {
  const context = useContext(Store);
  const { ExecuteDetailStore, stepTableDataSet, executeHistoryDataSet } = context;
  const ExecuteDetailSideRef = useRef(null);
  useEffect(() => {
    const { id } = context.match.params;
    ExecuteDetailStore.setDetailParams(queryString.parse(context.location.search));
    ExecuteDetailStore.getInfo(id);
    ExecuteDetailStore.setId(id);
    // ExecuteDetailStore.loadDetailData(id);  
  }, [ExecuteDetailStore, context, context.match.params]);

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
      // deleteAttachment(file.uid).then((data) => {
      //   ExecuteDetailStore.getInfo();
      //   Choerodon.prompt('删除成功');
      // }).catch((error) => {
      //   Choerodon.prompt(`删除失败 ${error}`);
      // });
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
    const { id } = context.match.params;
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
          executeId={id}
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
    const detailData = ExecuteDetailStore.getDetailData;
    const visible = ExecuteDetailStore.ExecuteDetailSideVisible;
    const fileList = [];
    const statusList = ExecuteDetailStore.getStatusList;
    const createBugShow = ExecuteDetailStore.getCreateBugShow;
    const defectType = ExecuteDetailStore.getDefectType;
    const createDectTypeId = ExecuteDetailStore.getCreateDectTypeId;
    const { statusColor, statusName } = ExecuteDetailStore.getStatusById(detailData.executionStatus);
    const { summary } = detailData;
    return (
      <Page className="c7n-test-execute-detail">
        <Header
          title={<FormattedMessage id="execute_detail" />}
        // backPath={disabled ? TestPlanLink() : TestExecuteLink()}
        >
          {detailData && (
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
            disabled={false}
            onClick={() => {
              goExecute('pre');
            }}
          >
            <Icon type="navigate_before" />
            <span><FormattedMessage id="execute_pre" /></span>
          </Button>
          <Button
            disabled={false}
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

        <Breadcrumb title={detailData ? renderBreadcrumbTitle(summary) : null} />
        <Content style={{ padding: visible ? '0 437px 0 0' : 0 }}>

          <Spin spinning={false} style={{ display: 'flex' }}>
            <div style={{ display: 'flex', width: '100%', height: '100%' }}>
              {/* 左边内容区域 */}
              <div
                style={{
                  flex: 1,
                  overflowX: 'hidden',
                  overflowY: 'auto',
                }}
              >
                <div className="c7n-test-execute-detail-header">
                  {detailData && (
                    <div style={{ display: 'flex', alignItems: 'center' }}>
                      <span style={{ fontSize: '20px' }}>{renderBreadcrumbTitle(summary)}</span>
                      <StatusTags
                        style={{ height: 20, lineHeight: '20px', marginLeft: 10 }}
                        color={statusColor}
                        name={statusName}
                      />
                    </div>
                  )}
                  {!disabled
                    && (
                      <QuickOperate
                        statusList={statusList}
                        quickPass={quickPass}
                        quickFail={quickFail}
                        onSubmit={handleSubmit}
                      />
                    )}
                </div>

                <CardWrapper
                  title={[<FormattedMessage id="execute_testDetail" />, <span style={{ marginLeft: 5 }}>{`（${stepTableDataSet.length}）`}</span>]}
                >
                  <StepTable
                    dataSet={stepTableDataSet}
                    ExecuteDetailStore={ExecuteDetailStore}
                  />
                </CardWrapper>

                <CardWrapper title={<FormattedMessage id="execute_executeHistory" />}>
                  <ExecuteHistoryTable
                    dataSet={executeHistoryDataSet}
                  />
                </CardWrapper>
              </div>
              {/* 右侧侧边栏 */}
              {visible && (
                <ExecuteDetailSide
                  disabled={disabled}
                  ref={ExecuteDetailSideRef}
                  detailData={detailData}
                  fileList={fileList}
                  status={{ statusColor, statusName }}
                  onClose={handleToggleExecuteDetailSide}
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


export default withRouter(observer(TestHandExecute));
