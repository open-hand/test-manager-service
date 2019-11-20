import React, { Component } from 'react';
import { observer } from 'mobx-react';
import _ from 'lodash';
import {
  Page, Header, Content, Breadcrumb, Choerodon,
} from '@choerodon/boot';
import { Button, Icon } from 'choerodon-ui';
import { Modal } from 'choerodon-ui/pro/lib';
import { FormattedMessage, injectIntl } from 'react-intl';
import IssueStore from '../stores/IssueStore';
import { getParams } from '../../../common/utils';
import { getIssueTree } from '../../../api/IssueManageApi';
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
      clickIssue: {},
    };
  }

  componentDidMount() {
    RunWhenProjectChange(IssueStore.clearStore);    
    this.getInit();
  }

  getInit = () => {
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
    this.getTestCase();
  }

  getTestCase = () => {
    IssueTreeStore.setLoading(true);
    getIssueTree().then((data) => {
      IssueTreeStore.setTreeData(data);
      IssueTreeStore.setLoading(false);

      const { currentCycle } = IssueTreeStore;
      const { id } = currentCycle;
      if (id) {
        IssueStore.loadIssues();
      }
    }).catch(() => {
      IssueTreeStore.setLoading(false);
      Choerodon.prompt('网络错误');
    });
  }

  /**
   *
   * 用例创建后，默认选到目标文件夹
   * @param {*} issue
   * @param {*} folderId
   * @memberof IssueManage
   */
  handleCreateIssue(issue, folderId) {
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

  handleAddFolderClick = () => {   
    IssueTreeStore.treeRef.current.addFirstLevelItem();
  }

  render() {
    const { clickIssue } = this.state;
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
          <Button icon="playlist_add" onClick={this.handleAddFolderClick}>
            创建一级目录
          </Button>
          <Button icon="unarchive" onClick={() => this.ExportSide.open()}>
            <FormattedMessage id="issue_export" />
          </Button>
          <Button className="leftBtn" onClick={this.handleOpenImportIssue}>
            {/* <Icon type="file_upload icon" /> */}
            <Icon type="archive" />
            <FormattedMessage id="issue_import" />
          </Button>
        </Header>
        <Breadcrumb />
        <Content style={{ display: 'flex', padding: '0', borderTop: '0.01rem solid rgba(0,0,0,0.12)' }}>          
          <IssueTree
            ref={(tree) => { this.tree = tree; }}
            onClose={() => {
              IssueStore.setTreeShow(false);
            }}
          />  
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
            <div className="c7ntest-content-issueFolderName">
              {
                currentCycle.id ? currentCycle.data.name : ''
              }
            </div>
            <IssueTable
              clickIssue={clickIssue}
              onClick={this.handleTableRowClick}
            />
          </div>
          <ExportSide ref={this.saveRef('ExportSide')} />
          <TestCaseDetail visible={clickIssue.caseId} clickIssue={clickIssue} onClose={this.handleClose} />
        </Content>
      </Page>
    );
  }
}
