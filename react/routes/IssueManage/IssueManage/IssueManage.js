import React, { Component, Fragment } from 'react';
import { observer } from 'mobx-react';
import {
  Page, Header, Content, Breadcrumb,
} from '@choerodon/boot';
import { Button, Icon } from 'choerodon-ui';
import { Modal } from 'choerodon-ui/pro';
import { HeaderButtons } from '@choerodon/master';
import { FormattedMessage, injectIntl } from 'react-intl';
import { localPageCacheStore } from '@choerodon/agile/lib/stores/common/LocalPageCacheStore';
import Loading, { LoadingHiddenWrap } from '@choerodon/agile/lib/components/Loading';
import Empty from '@/components/Empty';
import empty from '@/assets/empty.png';
import priorityApi from '@/api/priority';
import ResizeContainer from '@/components/ResizeDivider/ResizeContainer';
import CreateIssue from '@/components/create-test-case';
import IssueStore from '../stores/IssueStore';
import { getParams } from '../../../common/utils';
import RunWhenProjectChange from '../../../common/RunWhenProjectChange';
import IssueTree from '../components/IssueTree';
import IssueTable from '../components/IssueTable';
import ExportSide from '../components/ExportSide';
import ImportSide from '../components/ImportIssue';
import TestCaseDetail from '../components/TestCaseDetail';
import openCreateFolder from '../components/CreateFolder';
import './IssueManage.less';
import IssueTreeStore from '../stores/IssueTreeStore';
import openBatchDeleteModal from '../components/batch-delete-confirm';

const { Section, Divider } = ResizeContainer;

@injectIntl
@observer
class IssueManage extends Component {
  componentDidMount() {
    RunWhenProjectChange(IssueStore.clearStore);
    RunWhenProjectChange(IssueTreeStore.clearStore);
    this.getInit();
  }

  componentWillUnmount() {
    // IssueTreeStore.clearStore();
    // IssueStore.clearStore();
    this.clearCheckIdMap();
  }

  clearCheckIdMap = () => {
    IssueStore.checkIdMap.clear();
  };

  getInit = () => {
    const Request = getParams(this.props.location.search);
    const { paramName, paramIssueId, folderId } = Request;
    IssueStore.setParamName(paramName);
    IssueStore.setParamIssueId(paramIssueId);
    if (paramName && paramIssueId) {
      IssueStore.setClickIssue({
        caseId: paramIssueId,
      });
    }
    priorityApi.load().then((res) => {
      IssueStore.setPriorityList(res);
    });
    // 当参数中有用例名时，在table的筛选框中加入
    const barFilters = paramName ? [paramName] : undefined;
    IssueStore.setBarFilters(barFilters);
    // 加载缓存
    const { id: defaultTreeIdValue } = localPageCacheStore.getItem('issueMange.tree') || {};
    const { page = {}, filter = {} } = localPageCacheStore.getItem('issueManage.table') || {};
    const { current, pageSize } = page;
    const { contents, searchArgs } = filter;
    IssueStore.setBarFilters(barFilters || contents || []);
    IssueStore.setFilter(searchArgs ? { searchArgs } : { searchArgs: {} });
    this.getTestCase(folderId || defaultTreeIdValue, current, pageSize);
  }

  getTestCase = async (defaultSelectId, defaultPage, defaultPageSize) => {
    await IssueTreeStore.loadIssueTree(defaultSelectId);
    const { currentFolder } = IssueTreeStore;
    const { id } = currentFolder;
    if (id) {
      IssueStore.loadIssues(defaultPage, defaultPageSize);
    }
  }

  /**
   *
   * 用例创建后，默认选到目标目录
   * @param {*} issue
   * @param {*} folderId
   * @memberof IssueManage
   */
  handleCreateIssue(issue, folderId) {
    if (folderId) {
      IssueTreeStore.setCurrentFolderById(folderId);
      IssueTreeStore.updateHasCase(folderId, true);// 设置含有用例
    }
    IssueStore.setPagination({
      current: 1,
      pageSize: 10,
      total: IssueStore.pagination.total,
    });
    IssueStore.loadIssues(1, 10);
  }

  handleTableRowClick = (record) => {
    const { clickIssue, descriptionChanged } = IssueStore;
    if (!clickIssue.caseId || !descriptionChanged) {
      IssueStore.setClickIssue(record);
    } else {
      Modal.confirm({
        title: '提示',
        children: (
          <div>
            前置条件信息尚未保存，是否放弃保存？
          </div>
        ),
        onOk: () => {
          IssueStore.setClickIssue(record);
          IssueStore.setDescriptionChanged(false);
          return true;
        },
      });
    }
  }

  saveRef = (name) => (ref) => {
    this[name] = ref;
  }

  handleRefresh = () => {
    this.props.change('IssueManage', true);
    this.getTestCase();
    // IssueTreeStore.loadIssueTree();
  }

  handleClose = (issueInfo) => {
    const { issues } = IssueStore;
    const index = issues.findIndex((item) => item.caseId === issueInfo.caseId);
    if (index > -1) {
      issues[index] = { ...issues[index], ...issueInfo };
    }
    IssueStore.setClickIssue({});
    IssueStore.setIssues(issues); // 每次关闭详情时应该设置issues
  }

  handleOpenCreateIssue = () => {
    const { intl } = this.props;
    const { clickIssue } = IssueStore;
    Modal.open({
      key: 'createIssue',
      // title:<FormattedMessage id='issue_create_name'  />,
      title: intl.formatMessage({ id: 'test.caseLibrary.create' }),
      drawer: true,
      style: {
        width: 740,
      },
      children: (
        <CreateIssue
          // eslint-disable-next-line react/jsx-no-bind
          onOk={this.handleCreateIssue.bind(this)}
          intl={intl}
          caseId={clickIssue && clickIssue.caseId}
          defaultFolderValue={IssueTreeStore.getCurrentFolder}
        />
      ),
      okText: '创建',
    });
  }

  handleOpenImportIssue = () => {
    const { intl } = this.props;

    Modal.open({
      key: 'importIssue',
      // title:<FormattedMessage id='issue_create_name'  />,
      title: intl.formatMessage({ id: 'test.caseLibrary.import' }),
      drawer: true,
      destroyOnClose: false,
      style: {
        width: 380,
      },
      children: (
        <ImportSide defaultFolderValue={IssueTreeStore.getCurrentFolder} onOk={IssueStore.loadIssues} />
      ),
      footer: () => '',
      className: 'c7ntest-Issue-import-modal',
    });
  }

  handleOpenExportIssue = () => {
    const { intl } = this.props;
    Modal.open({
      key: 'exportIssue',
      title: intl.formatMessage({ id: 'test.caseLibrary.export' }),
      drawer: true,
      style: {
        width: 1090,
      },
      okCancel: false,
      okText: '关闭',
      children: (
        <ExportSide
          folderId={IssueTreeStore.getCurrentFolder.id}
        />
      ),
    });
  }

  handleCreateFolder = () => {
    this.getTestCase();
  }

  handleAddFolderClick = () => {
    if (IssueTreeStore.treeRef && IssueTreeStore.treeRef.current) {
      IssueTreeStore.treeRef.current.addFirstLevelItem();
    } else {
      openCreateFolder({
        onCreate: this.handleCreateFolder,
      });
    }
  }

  afterBatchDeleteModal = () => {
    this.handleRefresh();
    this.clearCheckIdMap();
  };

  handleBatchDeleteCase = async () => {
    await IssueStore.batchRemove();
  };

  render() {
    const { clickIssue, checkIdMap } = IssueStore;
    const currentFolder = IssueTreeStore.getCurrentFolder;
    const { loading, rootIds } = IssueTreeStore;
    const noFolder = rootIds.length === 0;
    const { tab, hasExtraTab, intl } = this.props;
    return (
      <Page
        className="c7ntest-Issue c7ntest-region"
      >
        <Header
          title={<FormattedMessage id="issue_name" />}
        >
          <HeaderButtons items={[{
            name: intl.formatMessage({ id: 'test.caseLibrary.create' }),
            display: !noFolder,
            icon: 'playlist_add',
            handler: () => this.handleOpenCreateIssue(),
          }, {
            name: intl.formatMessage({ id: 'test.caseLibrary.create.root.dir' }),
            display: true,
            icon: 'playlist_add',
            handler: this.handleAddFolderClick,
          }, {
            name: intl.formatMessage({ id: 'test.caseLibrary.export' }),
            display: !noFolder,
            icon: 'unarchive-o',
            handler: this.handleOpenExportIssue,
          }, {
            name: intl.formatMessage({ id: 'test.caseLibrary.import' }),
            display: !noFolder,
            icon: 'archive-o',
            handler: this.handleOpenImportIssue,
          }, {
            name: intl.formatMessage({ id: 'test.caseLibrary.batch.remove' }),
            display: !noFolder,
            disabled: !checkIdMap.size,
            icon: 'delete_sweep-o',
            handler: () => openBatchDeleteModal({
              handleDelete: this.handleBatchDeleteCase,
              deleteCount: checkIdMap.size,
              refresh: this.afterBatchDeleteModal,
            }),
          }, {
            iconOnly: true,
            display: true,
            handler: this.handleRefresh,
            icon: 'refresh',
          }]}
          />
        </Header>
        <Breadcrumb />
        <Content style={{
          display: 'flex', padding: '0', borderTop: '0.01rem solid rgba(0,0,0,0.12)', overflow: 'hidden',
        }}
        >
          <ResizeContainer type="horizontal">
            <Section
              size={{
                width: 240,
                minWidth: 240,
                maxWidth: 600,
              }}
              style={{
                minWidth: 240,
                maxWidth: 600,
              }}
            >
              <div className="c7ntest-Issue-content-left">
                {hasExtraTab && tab}
                <IssueTree />
              </div>
            </Section>
            <Divider />
            <Section
              style={{ flex: 1 }}
              size={{
                width: 'auto',
              }}
            >
              <div style={{ height: '100%' }}>
                {
                  noFolder ? (
                    <LoadingHiddenWrap>
                      <Empty
                        // loading={loading}
                        pic={empty}
                        title={intl.formatMessage({ id: 'test.common.empty.data' })}
                        description={intl.formatMessage({ id: 'test.caseLibrary.empty.dir.description' })}
                        extra={<Button type="primary" funcType="raised" onClick={this.handleAddFolderClick}>{intl.formatMessage({ id: 'test.caseLibrary.create.root.dir' })}</Button>}
                      />
                    </LoadingHiddenWrap>
                  ) : currentFolder.id && (
                    <div
                      className="c7ntest-content-issue"
                      style={{
                        flex: 1,
                        overflow: 'hidden',
                        padding: '0 20px',
                        height: '100%',
                        display: 'flex',
                        flexDirection: 'column',
                      }}
                    >
                      <div className="c7ntest-content-issueFolderName">
                        {currentFolder.data.name}
                      </div>
                      <IssueTable
                        onClick={this.handleTableRowClick}
                      />
                    </div>
                  )
                }
              </div>
              <Loading loadId="tree" loading={loading} />
            </Section>
          </ResizeContainer>

          <TestCaseDetail visible={clickIssue && clickIssue.caseId} onClose={this.handleClose} />
        </Content>
      </Page>
    );
  }
}
export default IssueManage;
