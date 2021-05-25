/* eslint-disable react/jsx-no-bind */
import React, {
  useEffect, useContext, useState,
} from 'react';
import {
  Icon, Card, Spin, Tooltip,
} from 'choerodon-ui';
import {
  Page, Header, Content, Breadcrumb,
  Choerodon,
} from '@choerodon/boot';
import JsonBig from 'json-bigint';
import DetailContainer, { useDetail } from '@choerodon/agile/lib/components/detail-container';
import { HeaderButtons } from '@choerodon/master';
import { observer } from 'mobx-react-lite';
import { withRouter } from 'react-router-dom';
import { FormattedMessage, useIntl } from 'react-intl';
import _ from 'lodash';
import { Modal, Button, message } from 'choerodon-ui/pro';
import queryString from 'query-string';
import { uploadFile, deleteFile } from '@/api/FileApi';
import { StatusTags } from '../../../../components';
import {
  executeDetailLink,
} from '../../../../common/utils';
import { updateDetail, updateSidebarDetail } from '../../../../api/ExecuteDetailApi';
import './TestPlanExecuteDetail.less';
import {
  ExecuteDetailSide, CreateBug, StepTable, QuickOperate, ExecuteHistoryTable,
} from './components';
import Store from './stores';
import EditExecuteIssue from './components/EditExecuteIssue';
import AutoHeightPrecondition from './components/auto-height-precondition';

const CardWrapper = ({
  children, title, style, titleClassName,
}) => (
  <Card
    title={null}
    style={style}
    bodyStyle={{ paddingBottom: 0, paddingTop: 20 }}
    bordered={false}
  >
    <span className={`c7n-test-execute-detail-card-title ${titleClassName || ''}`}>{title}</span>
    {children}
  </Card>
);
function TestPlanExecuteDetail(props) {
  const context = useContext(Store);
  const intl = useIntl();
  const {
    ExecuteDetailStore, stepTableDataSet, executeHistoryDataSet, testStatusDataSet,
  } = context;
  const [syncLoading, setSyncLoading] = useState(false);
  const [detailProps] = useDetail();
  useEffect(() => {
    const { executeId } = context;
    ExecuteDetailStore.setDetailParams(queryString.parse(context.location.search.replace(/%253D/g, '%3D'))); // 全局替换 id加密后 防止有% 被转义
    ExecuteDetailStore.getInfo(executeId);
    ExecuteDetailStore.setId(executeId);
  }, [ExecuteDetailStore, context, context.match.params]);

  const goExecute = (mode) => {
    const detailData = ExecuteDetailStore.getDetailData;
    const { nextExecuteId, previousExecuteId } = detailData;
    const { history } = context;
    const toExecuteId = mode === 'pre' ? previousExecuteId : nextExecuteId;
    if (toExecuteId) {
      const {
        contents, plan_id: planId, cycle_id: cycleId, assignerId, executionStatus, summary,
      } = queryString.parse(context.location.search.replace(/%253D/g, '%3D'));
      const filters = {
        cycle_id: cycleId,
        plan_id: planId,
        assignerId,
        contents,
        executionStatus,
        summary,
      };
      history.replace(executeDetailLink(toExecuteId, filters));
      // ExecuteDetailStore.getInfo(toExecuteId);
    }
  };

  const handleToggleExecuteDetailSide = () => {
    const visible = ExecuteDetailStore.ExecuteDetailSideVisible;
    ExecuteDetailStore.setExecuteDetailSideVisible(!visible);
  };

  const handleSubmit = (updateData) => {
    if (updateData.executionStatus) {
      const { statusList } = ExecuteDetailStore;
      const statusItem = _.find(statusList, { statusId: updateData.executionStatus }) || {};
      _.set(updateData, 'executionStatusName', statusItem.statusName);
    }
    const detailData = ExecuteDetailStore.getDetailData;
    const newData = { ...detailData, ...updateData };
    ExecuteDetailStore.enterLoading();
    updateDetail(newData).then(() => {
      ExecuteDetailStore.getInfo();
      executeHistoryDataSet.query();
    }).catch(() => {
      Choerodon.prompt('网络异常');
    });
  };

  const quickPassOrFail = (text) => {
    const detailData = { ...ExecuteDetailStore.getDetailData };
    const { statusList } = ExecuteDetailStore;
    if (_.find(statusList, { projectId: 0, statusName: text })) {
      const statusItem = _.find(statusList, { projectId: 0, statusName: text }) || {};
      detailData.executionStatus = statusItem.statusId;
      detailData.executionStatusName = statusItem.statusName;
      ExecuteDetailStore.enterLoading();
      updateDetail(detailData).then(() => {
        ExecuteDetailStore.getInfo();
        executeHistoryDataSet.query();
        if (text === '通过') {
          stepTableDataSet.query();
        }
      }).catch((error) => {
        Choerodon.prompt(`${error || '网络错误'}`);
      });
    } else {
      Choerodon.prompt('未找到对应状态');
    }
  };

  const quickHandle = (statusName) => {
    quickPassOrFail(statusName);
  };

  const handleHiddenCreateBug = () => {
    ExecuteDetailStore.setCreateBugShow(false);
  };

  const handleBugCreate = (res, requestData) => {
    const { assigneeId, projectId } = requestData;
    sessionStorage.setItem('test.plan.execute.detail.create.bug.default.value', JsonBig.stringify({
      assigneeId, projectId,
    }));
    ExecuteDetailStore.setCreateBugShow(false);
    ExecuteDetailStore.getInfo();
    stepTableDataSet.query();
  };

  /**
   * 批量删除已上传文件（修改用例 保存）
   *
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
  async function UpdateExecuteData(data) {
    const { executeId } = data;
    const testCycleCaseStepUpdateVOS = data.testCycleCaseStepUpdateVOS.map(
      (i) => {
        let {
          executeStepId, stepId,
        } = i;
        const { _status, ...otherI } = i;
        if (_status === 'add') {
          stepId = 0;
          executeStepId = null;
        }
        return {
          ...otherI,
          stepId,
          executeId,
          executeStepId,
        };
      },
    );
    const newData = {
      ...data,
      fileList: [],
      caseStepVOS: [],
      testCycleCaseStepUpdateVOS,
    };
    const { isAsync = false } = newData;
    const { fileList } = data;
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
        attachmentLinkId: data.executeId, attachmentType: 'CYCLE_CASE',
      };
      if (formDataAdd.has('file')) {
        await uploadFile(formDataAdd, config);
      }
      // 删除文件 只能单个文件删除， 进行遍历删除
      await deleteFiles(formDataDel.map((i) => i.id));
    }
    await updateSidebarDetail(newData);
    message.success(`${isAsync ? '同步修改成功' : '修改成功'}`);
    return true;
  }

  /**
   * 保存同步用例
   */
  const handleSaveSyncCase = async (modal) => {
    const { editExecuteCaseDataSet } = context;
    if (editExecuteCaseDataSet.current && editExecuteCaseDataSet.validate()) {
      // 进行提交数据
      setSyncLoading(true);
      const newData = {
        ...editExecuteCaseDataSet.current.toData(),
        isAsync: true,
      };
      if (editExecuteCaseDataSet.current.status !== 'sync') {
        if (!await UpdateExecuteData(newData)) {
          message.info('同步修改失败');
        }
      } else {
        message.info('未做任何修改');
      }
    }
    setSyncLoading(false);
    modal.close();
  };
  /**
   * 取消时数据清空
   */
  const handleCloseEdit = async () => {
    const { editExecuteCaseDataSet } = context;
    if (editExecuteCaseDataSet.current) {
      editExecuteCaseDataSet.splice(0, 1);
    }
    return true;
  };
  /**
   * 关闭回调，数据有更新则刷新页面
   * 无论数据是否有变化都删除数据
   */
  const onRefreshAfterClose = () => {
    const { editExecuteCaseDataSet } = context;

    if (editExecuteCaseDataSet.current) {
      if (editExecuteCaseDataSet.current.status === 'update') {
        ExecuteDetailStore.getInfo();
        stepTableDataSet.query();
        executeHistoryDataSet.query();
      }
      editExecuteCaseDataSet.splice(0, 1);
    }
  };
  const handleOpenEdit = () => {
    const { editExecuteCaseDataSet, executeId } = context;
    const detailData = ExecuteDetailStore.getDetailData;
    const editModal = Modal.open({
      key: 'editExecuteIssue',
      title: '修改用例',
      drawer: true,
      style: {
        width: 740,
      },
      afterClose: onRefreshAfterClose,
      onCancel: handleCloseEdit,
      children: (
        <EditExecuteIssue
          editDataset={editExecuteCaseDataSet}
          executeId={executeId}
          UpdateExecuteData={UpdateExecuteData}
        />
      ),
      footer: (okBtn, cancelBtn) => (
        <div>
          {okBtn}
          {detailData.caseHasExist ? <Button loading={syncLoading} funcType="raised" color="primary" onClick={handleSaveSyncCase.bind(this, editModal)}>保存并同步到用例库</Button>
            : (
              <Tooltip title="相关用例已删除">
                <Button funcType="raised" color="primary">保存并同步到用例库</Button>
              </Tooltip>
            )}
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
    return textArr.length > 15 ? <Tooltip title={text}>{`${textArr.slice(0, 15).join('') + ellipsis}`}</Tooltip> : text;
  };
  function handleOpenIssue(issueId) {
    const { open } = detailProps;
    open({
      path: 'issue',
      props: {
        issueId,
        // store: detailStore,
      },
      events: {
        update: () => {
          stepTableDataSet.query(stepTableDataSet.currentPage);
          // refresh();
        },
      },
    });
  }
  function render() {
    // disabled 用于禁止action列
    const { executeId } = context;
    const { disabled } = props;
    const { loading } = ExecuteDetailStore;
    const detailData = ExecuteDetailStore.getDetailData;
    const visible = ExecuteDetailStore.ExecuteDetailSideVisible;
    const statusList = ExecuteDetailStore.getStatusList;
    const createBugShow = ExecuteDetailStore.getCreateBugShow;
    const defectType = ExecuteDetailStore.getDefectType;
    const createDefectTypeId = ExecuteDetailStore.getCreateDefectTypeId;
    const { statusColor, statusName } = ExecuteDetailStore.getStatusById(detailData.executionStatus);
    const {
      summary, nextExecuteId, previousExecuteId, planStatus = 'done',
    } = detailData;
    return (
      <Page
        className="c7n-test-execute-detail"
      >
        <Header
          title={<FormattedMessage id="execute_detail" />}
        // backPath={disabled ? TestPlanLink() : TestExecuteLink()}
        >
          <HeaderButtons items={[{
            name: intl.formatMessage({ id: 'execute_next' }),
            display: true,
            icon: 'navigate_next',
            handler: () => {
              goExecute('next');
            },
            disabled: !nextExecuteId,
          }, {
            name: intl.formatMessage({ id: 'execute_pre' }),
            display: true,
            icon: 'navigate_before',
            handler: () => {
              goExecute('pre');
            },
            disabled: !previousExecuteId,
          }, {
            name: '修改用例',
            display: planStatus !== 'done',
            icon: 'mode_edit',
            handler: handleOpenEdit,
          }, {
            name: visible ? '隐藏详情' : '查看详情',
            display: true,
            icon: 'find_in_page',
            handler: handleToggleExecuteDetailSide,
          }]}
          />
        </Header>

        <Breadcrumb title={detailData ? renderBreadcrumbTitle(summary) : null} />
        <Content style={{ padding: visible ? '0 437px 0 0' : 0 }}>

          <Spin spinning={ExecuteDetailStore.loading} style={{ display: 'flex' }}>
            <div style={{ display: 'flex', width: '100%', height: '100%' }}>
              {/* 左边内容区域 */}
              <div
                style={{
                  flex: 1,
                  overflow: 'hidden',
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
                  {planStatus === 'doing'
                    && (
                      <QuickOperate
                        readOnly={planStatus !== 'doing'}
                        statusList={statusList}
                        quickHandle={quickHandle}
                        onSubmit={handleSubmit}
                      />
                    )}
                </div>

                <CardWrapper
                  title={(
                    <div className="c7n-test-execute-detail-card-title-description">
                      <AutoHeightPrecondition data={detailData.description} />
                      {[
                        <FormattedMessage id="execute_testDetail" />,
                        <span style={{ marginLeft: 5 }}>{`（${stepTableDataSet.totalCount}）`}</span>,
                      ]}
                    </div>
                  )}
                // titleClassName="c7n-test-execute-detail-card-title-description"
                >
                  <StepTable
                    dataSet={stepTableDataSet}
                    updateHistory={() => executeHistoryDataSet.query()} // 更新执行历史
                    testStatusDataSet={testStatusDataSet}
                    readOnly={planStatus === 'done'} // 数据是否只读
                    operateStatus={planStatus === 'doing'} // 数据是否可以进行状态更改/缺陷更改
                    ExecuteDetailStore={ExecuteDetailStore}
                    executeId={executeId}
                    openIssue={handleOpenIssue}
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
                  detailData={detailData}
                  fileList={detailData.attachment}
                  status={{ statusColor, statusName }}
                  onClose={handleToggleExecuteDetailSide}
                />
              )}
              {
                createBugShow && (
                  <CreateBug
                    visible={createBugShow}
                    defectType={defectType}
                    description={ExecuteDetailStore.getDefaultDefectDescription}
                    id={createDefectTypeId}
                    onCancel={handleHiddenCreateBug}
                    onOk={handleBugCreate}
                  />
                )
              }
            </div>
          </Spin>
          <DetailContainer {...detailProps} />
        </Content>

      </Page>
    );
  }
  return render();
}

export default withRouter(observer(TestPlanExecuteDetail));
