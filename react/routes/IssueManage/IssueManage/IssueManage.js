import React, { Component } from 'react';
import { observer } from 'mobx-react';
import _ from 'lodash';
import {
  Page, Header, Content, Breadcrumb,
} from '@choerodon/master';
import { Button, Icon } from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import IssueStore from '../IssueManagestore/IssueStore';
import { commonLink, getParams, testCaseDetailLink } from '../../../common/utils';
import RunWhenProjectChange from '../../../common/RunWhenProjectChange';
import CreateIssue from '../IssueManageComponent/CreateIssue';
import IssueTree from '../IssueManageComponent/IssueTree';
import IssueTable from '../IssueManageComponent/IssueTable';
import ExportSide from '../IssueManageComponent/ExportSide';
import ImportSide from '../ImportIssue';
import TestCaseDetail from '../TestCaseDetail';
import './IssueManage.less';
import IssueTreeStore from '../IssueManagestore/IssueTreeStore';

@observer
export default class IssueManage extends Component {
  constructor(props) {
    // 更正state 
    super(props);
    this.state = {
      createIssueShow: false,
      clickIssue: {},
    };
  }

  componentDidMount() {
    RunWhenProjectChange(IssueStore.clearStore);
    this.getInit();
  }

  getInit() {
    const Request = getParams(this.props.location.search);
    const { paramName, paramIssueId } = Request;
    IssueStore.setParamName(paramName);
    IssueStore.setParamIssueId(paramIssueId);
    if (paramName && paramIssueId) {
      this.setState({
        clickIssue: {
          issueId: paramIssueId,
        },
      });
    }
    // 当参数中有用例名时，在table的筛选框中加入
    const barFilters = paramName ? [paramName] : [];
    IssueStore.setBarFilters(barFilters);
    IssueStore.init();
    IssueStore.loadIssues();
  }

  /**
   *
   * 用例创建后，默认选到目标文件夹
   * @param {*} issue
   * @param {*} folderId
   * @memberof IssueManage
   */
  handleCreateIssue(issue, folderId) {
    this.setState({ createIssueShow: false });
    let targetCycle = null;
    // 如果指定了文件夹就设置文件夹，否则设置版本
    if (folderId) {
      targetCycle = _.find(IssueTreeStore.dataList, { cycleId: folderId });
    } else {
      const { versionId } = issue.versionIssueRelVOList[0];
      targetCycle = _.find(IssueTreeStore.dataList, { versionId });
    }
    if (targetCycle) {
      const expandKeys = IssueTreeStore.getExpandedKeys;
      // 设置当前选中项
      IssueTreeStore.setCurrentCycle(targetCycle);
      // 设置当前选中项
      IssueTreeStore.setSelectedKeys([targetCycle.key]);
      // 设置展开项，展开父元素
      IssueTreeStore.setExpandedKeys([...expandKeys, targetCycle.key.split('-').slice(0, -1).join('-')]);
    }
    IssueStore.loadIssues();
  }


  handleTableRowClick = (record) => {
    this.setState({
      clickIssue: record,
    });
    // const { history } = this.props;
    // history.push(testCaseDetailLink(record.issueId, record.folderName));
  }


  saveRef = name => (ref) => {
    this[name] = ref;
  }

  handleClose = () => {
    this.setState({
      clickIssue: {},
    });
  }

  render() {
    const { createIssueShow, clickIssue } = this.state;
    const { treeShow } = IssueStore;
    const currentCycle = IssueTreeStore.getCurrentCycle;
    return (
      <Page className="c7ntest-Issue c7ntest-region">
        <Header
          title={<FormattedMessage id="issue_name" />}
        >
          <Button className="leftBtn" onClick={() => this.setState({ createIssueShow: true })}>
            <Icon type="playlist_add icon" />
            <FormattedMessage id="issue_createTestIssue" />
          </Button>
          <Button className="leftBtn" onClick={() => this.ExportSide.open()}>
            <Icon type="unarchive" />
            <FormattedMessage id="issue_export" />
          </Button>
          <Button className="leftBtn" onClick={() => this.importSide.open()}>
            {/* <Icon type="file_upload icon" /> */}
            <Icon type="archive" />
            <FormattedMessage id="issue_import" />
          </Button>
          {/* <Button
            onClick={() => {
              const { current, pageSize } = IssueStore.pagination;
              if (this.tree) {
                this.tree.getTree();
              }
              IssueStore.loadIssues(current - 1, pageSize);
            }}
          >
            <Icon type="autorenew icon" />
            <FormattedMessage id="refresh" />
          </Button> */}
        </Header>
        <Breadcrumb title="" />
        <div className="breadcrumb-border" />
        <Content className="c7ntest-issue-content" style={{ display: 'flex', padding: '0' }}>
          {/* <div className="c7ntest-chs-bar">
            {!treeShow && (
              <p
                role="none"
                onClick={() => {
                  IssueStore.setTreeShow(true);
                }}
              >
                <FormattedMessage id="issue_repository" />
              </p>
            )}
          </div> */}
          <div className="c7ntest-issue-tree">
            {treeShow && (
              <IssueTree
                ref={(tree) => { this.tree = tree; }}
                onClose={() => {
                  IssueStore.setTreeShow(false);
                }}
              />
            )}
          </div>
          <div
            className="c7ntest-content-issue"
            style={{
              flex: 1,
              display: 'block',
              overflowY: 'auto',
              overflowX: 'hidden',
              padding: '0 20px',
            }}
          >
            <IssueTable
              clickIssue={clickIssue}
              onRow={record => ({
                onClick: (event) => { this.handleTableRowClick(record); },
              })}
            />
          </div>
          <ExportSide ref={this.saveRef('ExportSide')} />
          <ImportSide ref={this.saveRef('importSide')} />
          {clickIssue.issueId && <TestCaseDetail clickIssue={clickIssue} onClose={this.handleClose} />}
          {
            createIssueShow && (
              <CreateIssue
                visible={createIssueShow}
                onCancel={() => this.setState({ createIssueShow: false })}
                onOk={this.handleCreateIssue.bind(this)}
                defaultVersion={currentCycle.versionId}
              />
            )
          }
        </Content>
      </Page>
    );
  }
}
