import React, { Component } from 'react';
import { observer } from 'mobx-react';
import _ from 'lodash';
import {
  Page, Header, Content, Breadcrumb,
} from '@choerodon/boot';
import { Button, Icon } from 'choerodon-ui';
import { Modal } from 'choerodon-ui/pro/lib';
import { FormattedMessage, injectIntl } from 'react-intl';
import IssueStore from '../stores/IssueStore';
import { commonLink, getParams, testCaseDetailLink } from '../../../common/utils';
import RunWhenProjectChange from '../../../common/RunWhenProjectChange';
import CreateIssue from '../components/CreateIssue';
import IssueTree from '../components/IssueTree';
import IssueTable from '../components/IssueTable';
import ExportSide from '../components/ExportSide';
import ImportSide from '../ImportIssue';
import TestCaseDetail from '../TestCaseDetail';
import './IssueManage.less';
import IssueTreeStore from '../stores/IssueTreeStore';

@injectIntl
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

  handleOpenCreateIssue = () => {
    const { intl } = this.props;
    Modal.open({
      key: 'createIssue',
      // title:<FormattedMessage id='issue_create_name'  />,
      title: intl.formatMessage({ id: 'issue_create_name' }),
      drawer: true,
      style: {
        width: 740,
      },
      children: (
        <CreateIssue
          onOk={this.handleCreateIssue.bind(this)}
          intl={intl}
        />
      ),
      okText: '创建',
    });
  }

  handleOpenImportIssue = () => {
    const { intl } = this.props;
    Modal.open({
      key: 'createIssue',
      // title:<FormattedMessage id='issue_create_name'  />,
      title: '导入用例',
      drawer: true,
      style: {
        width: 380,
      },
      children: (
        <ImportSide />
      ),
      // onOk: this.handleCreateIssue.bind(this),
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
          <Button className="leftBtn" onClick={() => this.handleOpenCreateIssue()}>
            <Icon type="playlist_add icon" />
            <FormattedMessage id="issue_createTestIssue" />
          </Button>
          <Button className="leftBtn" onClick={() => this.ExportSide.open()}>
            <Icon type="unarchive" />
            <FormattedMessage id="issue_export" />
          </Button>
          <Button className="leftBtn" onClick={this.handleOpenImportIssue}>
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
              onClick={this.handleTableRowClick}
            />
          </div>
          <ExportSide ref={this.saveRef('ExportSide')} />
          <TestCaseDetail visible={clickIssue.issueId} clickIssue={clickIssue} onClose={this.handleClose} />
          {/* {
            createIssueShow && (
              <CreateIssue
                visible={createIssueShow}
                onCancel={() => this.setState({ createIssueShow: false })}
                onOk={this.handleCreateIssue.bind(this)}
                defaultVersion={currentCycle.versionId}
              />
            )
          } */}
        </Content>
      </Page>
    );
  }
}
