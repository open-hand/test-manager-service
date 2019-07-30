import React, { Component } from 'react';
import { withRouter } from 'react-router-dom';
import { Link } from 'react-router-dom';
import {
  Table, Icon, Tooltip, Menu, Button,
} from 'choerodon-ui';
import { stores, axios } from '@choerodon/boot';
import _ from 'lodash';
import { FormattedMessage } from 'react-intl';
import TimeAgo from 'timeago-react';
import { TestExecuteLink, issueLink, executeDetailShowLink } from '../../../../common/utils';
import './TestExecuteTable.scss';
import { editCycle } from '../../../../api/ExecuteDetailApi';
import { StatusTags } from '../../../../components';

const { AppState } = stores;

class TestExecuteTable extends Component {
  constructor(props) {
    super(props);
    this.state = {
      data: [],
      expand: [],
      status: [],
    };
  }

  componentDidMount() {
    this.loadStatus();
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.data !== this.props.data && nextProps.data) {
      this.setState({ data: nextProps.data });
    }
  }

  loadStatus() {
    const obj = {
      projectId: AppState.currentMenuType.id,
      statusType: 'CYCLE_CASE',
    };
    axios.post(`/test/v1/projects/${AppState.currentMenuType.id}/status/query`, obj)
      .then((res) => {
        this.setState({
          status: res,
        });
      });
  }

  confirm(executeId, e) {
    this.handleDeleteCircle(executeId);
  }

  cancel(e) {
  }

  handleDeleteCircle(executeId) {
    axios.delete(`/test/v1/projects/${AppState.currentMenuType.id}/cycle/case?cycleCaseId=${executeId}`)
      .then((res) => {
        this.props.onOk();
      });
  }

  quickPass(execute) {
    const cycleData = { ...execute };
    if (_.find(this.state.status, { projectId: 0, statusName: '通过' })) {
      cycleData.executionStatus = _.find(this.state.status, { projectId: 0, statusName: '通过' }).statusId;
      delete cycleData.defects;
      delete cycleData.caseAttachment;
      delete cycleData.testCycleCaseStepES;
      delete cycleData.lastRank;
      delete cycleData.nextRank;
      this.props.enterLoad();
      editCycle(cycleData).then((Data) => {
        this.props.onOk();
      }).catch((error) => {
        this.props.leaveLoad();
        Choerodon.prompt('网络异常');
      });
    } else {
      Choerodon.prompt('未找到通过');
    }
  }


  getMenu = () => (
    <Menu onClick={this.handleClickMenu.bind(this)}>
      <Menu.Item key="edit">
        <FormattedMessage id="edit" />
      </Menu.Item>
      <Menu.Item key="clone">
        <FormattedMessage id="clone" />
      </Menu.Item>
      <Menu.Item key="delete">
        <FormattedMessage id="delete" />
      </Menu.Item>
      <Menu.Item key="add">
        <FormattedMessage id="upload_attachment" />
      </Menu.Item>
    </Menu>
  );

  handleClickMenu(e) {
    if (e.key === 'edit') {
      // window.console.log('edit');
    } else if (e.key === 'clone') {
      // window.console.log('clone');
    } else if (e.key === 'delete') {
      // window.console.log('delete');
    } else if (e.key === 'add') {
      // window.console.log('add');
    }
  }

  handleChangeExpand(id) {
    const theexpand = this.state.expand;
    let expand = theexpand.slice();
    if (_.find(expand, v => v === id)) {
      expand = _.remove(expand, id);
      document.getElementsByClassName(`${id}-list`)[0].style.height = '34px';
    } else {
      expand.push(id);
      document.getElementsByClassName(`${id}-list`)[0].style.height = 'auto';
    }
    this.setState({ expand });
  }

  render() {
    const { mode } = this.props;
    const columns = [{
      title: <FormattedMessage id="version" />,
      dataIndex: 'versionName',
      key: 'versionName',
    }, {
      title: <FormattedMessage id="cycle_name" />,
      dataIndex: 'cycleName',
      key: 'cycleName',
      render: (cycleName, item) => (
        <div className="c7ntest-text-dot">
          <Tooltip title={cycleName} placement="topLeft">
            <Link className="c7ntest-showId" to={TestExecuteLink(item.cycleId)} target="_blank">
              {cycleName || ''}
            </Link>
          </Tooltip>
        </div>
      ),
    }, {
      title: <FormattedMessage id="cycle_createExecute_folder" />,
      dataIndex: 'folderName',
      key: 'folderName',
      render: (folderName, item) => (
        <div className="c7ntest-text-dot">
          <Tooltip title={item.folderName} placement="topLeft">
            <Link className="c7ntest-showId" to={TestExecuteLink(item.cycleId)} target="_blank">
              {item.folderName || ''}
            </Link>
          </Tooltip>
        </div>
      ),
    }, {
      title: <FormattedMessage id="status" />,
      dataIndex: 'statusName',
      key: 'statusName',
      render: (statusName, item) => {
        const urlParams = AppState.currentMenuType;
        const status = _.find(this.state.status, { statusId: item.executionStatus }) || {};
        return (
          <StatusTags
            color={status.statusColor}
            name={status.statusName}
          />
          // <div style={{
          //   width: 60, height: 20, borderRadius: '2px', background: status.statusColor, display: 'inline-block', lineHeight: '20px', textAlign: 'center', color: '#fff',
          // }}
          // >
          //   {status && status.statusName}
          // </div>       
        );
      },
    }];
    const wideColumns = [{
      title: <FormattedMessage id="bug" />,
      dataIndex: 'defects',
      key: 'defects',
      render: (defects, item) => (
        <div>
          {
            item.defects.length ? (
              <Tooltip
                placement="topLeft"
                title={(
                  <div>
                    {item.defects.map((defect, i) => {
                      const { 
                        issueId, typeCode, issueName, summary, 
                      } = defect.issueInfosDTO || {};
                      return (
                        <div>
                          <Link
                            style={{
                              color: 'white',
                            }}
                            to={issueLink(issueId, typeCode)}
                            target="_blank"
                          >
                            {issueName}
                          </Link>
                          <div>{summary}</div>
                        </div>
                      );
                    })}
                  </div>
                )}
              >
                <div className="c7ntest-text-dot">
                  {item.defects.map((defect, i) => defect.issueInfosDTO && defect.issueInfosDTO.issueName).join(',')}
                </div>
              </Tooltip>

            ) : '-'
          }
        </div>
      ),
    }, {
      title: <FormattedMessage id="execute_executive" />,
      dataIndex: 'lastUpdateUser',
      key: 'lastUpdateUser',
    }, {
      title: <FormattedMessage id="execute_executeTime" />,
      dataIndex: 'executeTime',
      key: 'executeTime',
      render: (a, item) => (
        <div style={{ flex: 2, lineHeight: '34px' }} className="c7ntest-text-dot">
          <TimeAgo
            datetime={item.lastUpdateDate}
            locale={Choerodon.getMessage('zh_CN', 'en')}
          />
        </div>
      ),
    }, {
      title: null,
      dataIndex: 'action',
      key: 'action',
      render: (caction, item) => (
        <div style={{ lineHeight: '34px', textAlign: 'center' }}>
          {/* <Tooltip title={<FormattedMessage id="execute_quickPass" />}>
              <Icon type="pass mlr-3 pointer" onClick={this.quickPass.bind(this, item)} />
            </Tooltip> */}
          <Tooltip title={<FormattedMessage id="execute_detail" />}>
            <Button
              shape="circle"
              funcType="flat"
              icon="explicit2"
              style={{ color: 'black' }}
              onClick={() => {
                this.props.history.push(executeDetailShowLink(item.executeId));
              }}
            />
          </Tooltip>
          {/* <Popconfirm
              title={Choerodon.getMessage('确认删除吗?', 'Confirm delete')}
              placement="left"
              onConfirm={this.confirm.bind(this, item.executeId)}
              onCancel={this.cancel}
              okText={Choerodon.getMessage('删除', 'delete')}
              cancelText={Choerodon.getMessage('取消', 'Cancel')}
              okType="danger"
            >
              <Icon type="delete_forever mlr-3 pointer" />
            </Popconfirm> */}
        </div>
      ),
    }];
    return (
      <div className="c7ntest-TestExecuteTable">
        <Table
          pagination={false}
          filterBar={false}
          dataSource={this.state.data}
          columns={mode === 'narrow' ? columns : columns.concat(wideColumns)}
        />
      </div>
    );
  }
}

export default withRouter(TestExecuteTable);
