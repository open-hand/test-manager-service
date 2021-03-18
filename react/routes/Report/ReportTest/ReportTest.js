import React, { Component } from 'react';
import {
  Page, Header, Content, stores, Breadcrumb,
  Choerodon,
} from '@choerodon/boot';

import { Link } from 'react-router-dom';
import {
  Table, Button, Icon, Collapse, Tooltip,
} from 'choerodon-ui';
import _ from 'lodash';
import { FormattedMessage } from 'react-intl';
import ReporterSwitcher from '../components';
import { getReportsFromDefect, getReportsFromDefectByIssueIds } from '../../../api/reportApi';
import { getStatusList } from '../../../api/TestStatusApi';
import { getIssueTypes, getIssueStatus } from '../../../api/agileApi';
import {
  issueLink, executeDetailLink, getProjectName,
} from '../../../common/utils';
import './ReportTest.less';

const { AppState } = stores;
const { Panel } = Collapse;

export const STATUS = {
  todo: '#ffb100',
  doing: '#4d90fe',
  done: '#00bfa5',
};
class ReportTest extends Component {
  // eslint-disable-next-line react/state-in-constructor
  state = {
    selectVisible: false,
    loading: false,
    reportList: [],
    statusList: [],
    stepStatusList: [],
    issueTypes: [],
    issueStatusList: [],
    pagination: {
      current: 1,
      total: 0,
      pageSize: 10,
    },
    openId: [],
    issueIds: [],
    search: {
      advancedSearchArgs: {},
      searchArgs: {},
    },
  }

  componentDidMount() {
    this.getInfo();
  }

  getInfo = () => {
    this.setState({
      loading: true,
    });
    Promise.all([
      getStatusList('CYCLE_CASE'),
      getStatusList('CASE_STEP'),
      this.getReportsFromDefect(),
      // getIssueTypes(),
      // getIssueTypes('agile'),
      getIssueStatus('agile'),
    ]).then(([
      statusList,
      stepStatusList,
      any,
      // issueTypes,
      // agileTypeList,
      issueStatusList,
    ]) => {
      this.setState({
        statusList,
        stepStatusList,
        // issueTypes: issueTypes.concat(agileTypeList),
        issueStatusList,
        openId: [],
      });
    });
  }

  sliceIssueIds = (arr, pagination) => {
    const { current, pageSize } = pagination;
    return arr.slice(pageSize * (current - 1), pageSize * current);
  }

  /**
   *根据搜索条件获取报表，取得数据以及所有issueid，在筛选条件和刷新时调用
   *
   * @memberof ReportTest
   */
  getReportsFromDefect = (pagination, search) => {
    const Pagination = pagination || this.state.pagination;
    const Search = search || this.state.search;

    getReportsFromDefect({
      page: Pagination.current,
      size: Pagination.pageSize,
    }, Search).then((reportData) => {
      if (!reportData.failed) {
        this.setState({
          loading: false,
          reportList: reportData.list,
          issueIds: reportData.allIdValues || [],
          pagination: {
            current: Pagination.current,
            pageSize: Pagination.pageSize,
            total: reportData.allIdValues ? reportData.allIdValues.length : 0,
          },
        });
      } else {
        this.setState({ loading: false });
        Choerodon.prompt(reportData.message);
      }
    }).catch((error) => {
      window.console.log(error);
      this.setState({
        loading: false,
      });
      Choerodon.prompt('网络异常');
    });
  }

  /**
   *通过issueid取报表，当分页改变时调用
   *
   * @memberof ReportTest
   */
  getReportsFromDefectByIssueIds = (pagination) => {
    const Pagination = pagination || this.state.pagination;
    const { issueIds } = this.state;
    this.setState({ loading: true });
    getReportsFromDefectByIssueIds(this.sliceIssueIds(issueIds, Pagination)).then((reportData) => {
      if (!reportData.failed) {
        this.setState({
          loading: false,
          reportList: reportData,
          pagination: {
            current: Pagination.current,
            pageSize: Pagination.pageSize,
            total: issueIds.length,
          },
        });
      } else {
        this.setState({ loading: false });
        Choerodon.prompt(reportData.message);
      }
    }).catch((error) => {
      window.console.log(error);
      this.setState({
        loading: false,
      });
      Choerodon.prompt('网络异常');
    });
  }

  handleTableChange = (pagination, filters, sorter) => {
    this.getReportsFromDefectByIssueIds(pagination);
  }

  handleOpen = (issueId) => {
    const { openId } = this.state;
    if (!openId.includes(issueId.toString())) {
      this.setState({
        openId: openId.concat([issueId.toString()]),
      });
    } else {
      const index = openId.indexOf(issueId.toString());
      openId.splice(index, 1);
      this.setState({
        openId: [...openId],
      });
    }
  }

  handleFilterChange = (pagination, filters, sorter, barFilters) => {
    const { statusId, priorityCode, issueTypeId } = filters;
    const {
      issueNum, summary, assignee, sprint, version, component, epic,
    } = filters;
    const search = {
      contents: barFilters,
      advancedSearchArgs: {
        statusId: statusId || [],
        // priorityCode: priorityCode || [],
        issueTypeId: issueTypeId || [],
      },
      otherArgs: {
        issueNum: issueNum ? issueNum[0] : '',
        summary: summary ? summary[0] : '',
        // assignee: assignee ? assignee[0] : '',
        // sprint: sprint ? sprint[0] : '',
        // version: version ? version[0] : '',
        // component: component ? component[0] : '',
        // epic: epic ? epic[0] : '',
      },
    };
    const Pagination = this.state.pagination;
    Pagination.current = 1;
    this.setState({
      search,
    });
    this.getReportsFromDefect(Pagination, search);
  }

  render() {
    const {
      selectVisible, reportList, loading, pagination,
      statusList, stepStatusList, issueTypes, issueStatusList, openId,
    } = this.state;
    const urlParams = AppState.currentMenuType;
    const { organizationId } = AppState.currentMenuType;
    const that = this;

    const columns = [{
      className: 'c7ntest-table-white',
      title: <FormattedMessage id="bug" />,
      dataIndex: 'a',
      key: 'a',
      width: '25%',
      render(test, record) {
        const { issueInfosVO } = record;
        const {
          issueId, statusVO,
          issueName, summary, typeCode,
        } = issueInfosVO;
        const { name: statusName, colour: statusColor, type: statusCode } = statusVO || {};
        return (
          <Collapse
            activeKey={openId}
            bordered={false}
            onChange={(keys) => { that.handleOpen(issueId, keys); }}
          >
            <Panel
              showArrow={false}
              header={(
                <div>
                  <div className="c7ntest-collapse-show-item">
                    <Icon type="navigate_next" className="c7ntest-collapse-icon" />
                    <Tooltip title={issueName}>
                      <Link className="c7ntest-showId" to={issueLink(issueId, typeCode, issueName)}>
                        {issueName}
                      </Link>
                    </Tooltip>
                    <div className="c7ntest-collapse-header-icon">
                      <span style={{ color: STATUS[statusCode], borderColor: STATUS[statusCode] }}>
                        {statusName}
                      </span>
                    </div>
                  </div>
                  <div style={{ fontSize: '13px' }}>{summary}</div>
                </div>
              )}
              key={issueId}
            />
          </Collapse>
        );
      },
    }, {
      className: 'c7ntest-table-white',
      title: <FormattedMessage id="execute" />,
      dataIndex: 'execute',
      key: 'execute',
      width: '25%',
      render(a, record) {
        const { testCycleCaseES, testCycleCaseStepES, issueInfosVO } = record;
        const { issueId } = issueInfosVO;
        const executeStatus = {};
        const totalExecute = testCycleCaseES.length + testCycleCaseStepES.length;
        const caseShow = testCycleCaseES.concat(testCycleCaseStepES).map((execute, i) => {
          // 执行的颜色
          const { executionStatus, stepStatus } = execute;
          let statusColor = '';
          let statusName = '';
          if (executionStatus) {
            statusColor = _.find(statusList, { statusId: executionStatus })
              ? _.find(statusList, { statusId: executionStatus }).statusColor : '';
            statusName = _.find(statusList, { statusId: executionStatus })
              && _.find(statusList, { statusId: executionStatus }).statusName;
          } else {
            statusColor = _.find(stepStatusList, { statusId: stepStatus })
              ? _.find(stepStatusList, { statusId: stepStatus }).statusColor : '';
            statusName = _.find(stepStatusList, { statusId: stepStatus })
              ? _.find(stepStatusList, { statusId: stepStatus }).statusName : '';
          }

          if (!executeStatus[statusName]) {
            executeStatus[statusName] = 1;
          } else {
            executeStatus[statusName] += 1;
          }
          // 跳转到执行详情时所需要的参数
          const filters = {
            cycle_id: execute.cycleId || '', //  测试步骤关联的缺陷 无cycle_id，当无cycle_id将其置空，防止后端查询时出错.
            plan_id: 999999999, // 错误的Plan id 导致无上下执行
          };
          return (
            <div className="c7ntest-cycle-show-container">
              <div>
                <Tooltip title={`${execute.cycleName}${execute.folderName ? `/${execute.folderName}` : ''}`}>
                  <Link className="c7ntest-showId" style={{ display: 'inline-block' }} to={executeDetailLink(execute.executeId, filters)}>
                    {execute.cycleName}
                    {execute.folderName ? `/${execute.folderName}` : ''}
                  </Link>
                </Tooltip>
              </div>
              <div
                className="c7ntest-collapse-text-icon c7ntest-text-dot"
                style={{ color: statusColor, borderColor: statusColor }}
              >
                {statusName}
              </div>
              <Link
                style={{ lineHeight: '13px' }}
                to={executeDetailLink(execute.executeId, filters)}
              >
                <Icon type="explicit" style={{ marginLeft: 10, color: 'black' }} />
              </Link>
              {
                i >= testCycleCaseES.length
                  ? (
                    <div
                      style={{
                        height: 20,
                        width: 43,
                        marginLeft: 30,
                        color: 'white',
                        padding: '0 8px',
                        background: 'rgba(0,0,0,0.20)',
                        borderRadius: '100px',
                      }}
                      className="c7ntest-text-dot"
                    >
                      <FormattedMessage id="step" />
                    </div>
                  ) : null

              }

            </div>
          );
        });
        return openId.includes(issueId.toString())
          ? (
            <div style={{ minHeight: 30 }}>
              {' '}
              {caseShow}
              {' '}
            </div>
          )
          : (
            <div>
              <div>
                <FormattedMessage id="report_total" />
                {'：'}
                {totalExecute}
              </div>
              <div style={{ display: 'flex' }}>
                {
                  Object.keys(executeStatus).map((key) => (
                    <div>
                      <span>
                        {key}
                        {'：'}
                      </span>
                      <span>{executeStatus[key]}</span>
                    </div>
                  ))
                }
              </div>
            </div>
          );
      },
    }, {
      className: 'c7ntest-table-white',
      title: <FormattedMessage id="test" />,
      dataIndex: 'cycleId',
      key: 'cycleId',
      width: '25%',
      render(cycleId, record) {
        // const { linkedTestIssues } = record;
        const { testCycleCaseES, testCycleCaseStepES } = record;
        const { issueId } = record.issueInfosVO;
        const caseShow = testCycleCaseES.concat(testCycleCaseStepES).map((execute) => {
          const { issueInfosVO } = execute;
          const {
            issueName, summary, typeCode, statusVO,
          } = issueInfosVO || {};
          const { name: statusName, colour: statusColor, type: statusCode } = statusVO || {};
          return (
            <div className="c7ntest-issue-show-container">
              <div className="c7ntest-collapse-show-item">
                <Tooltip title={issueName}>
                  <Link className="c7ntest-showId" to={issueLink(issueInfosVO && issueInfosVO.issueId, typeCode, issueName)}>
                    {issueName}
                  </Link>
                </Tooltip>
                <div className="c7ntest-collapse-header-icon">
                  <span style={{ color: STATUS[statusCode], borderColor: STATUS[statusCode] }}>
                    {statusName}
                  </span>
                </div>
              </div>
              <div className="c7ntest-report-summary">{summary}</div>
            </div>
          );
        });
        return openId.includes(issueId.toString())
          ? (
            <div style={{ minHeight: 50 }}>
              {' '}
              {caseShow}
              {' '}
            </div>
          )
          : (
            <div>
              {' '}
              <FormattedMessage id="report_total" />
              {'：'}
              {testCycleCaseES.concat(testCycleCaseStepES).length}
            </div>
          );
      },
    }, {
      className: 'c7ntest-table-white',
      title: <FormattedMessage id="demand" />,
      dataIndex: 'demand',
      key: 'demand',
      width: '25%',
      render(demand, record) {
        const { testCycleCaseES, testCycleCaseStepES } = record;
        const { issueId } = record.issueInfosVO;
        const caseShow = testCycleCaseES.concat(testCycleCaseStepES).map((execute, i) => {
          const { issueLinkDTOS, issueInfosVO } = execute;
          const { issueName } = issueInfosVO || {};
          // window.console.log(issueLinkDTOS.length);
          const issueLinks = issueLinkDTOS && issueLinkDTOS.map((link) => {
            const { issueNum, summary, statusVO } = link;
            const { name: statusName, colour: statusColor, type: statusCode } = statusVO || {};
            return (
              <div className="c7ntest-issue-show-container">
                <div className="c7ntest-collapse-show-item">
                  <Tooltip title={issueNum}>
                    <Link className="c7ntest-showId" to={issueLink(link.linkedIssueId, link.typeCode, issueName)}>
                      {issueNum}
                    </Link>
                  </Tooltip>
                  <div className="c7ntest-collapse-header-icon">
                    <span style={{ color: STATUS[statusCode], borderColor: STATUS[statusCode] }}>
                      {statusName}
                    </span>
                  </div>
                </div>
                <div className="c7ntest-report-summary">{summary}</div>
              </div>
            );
          });
          return (
            <div style={{
              minHeight: 50,
            }}
            >
              {issueLinks}

            </div>
          );
        });

        return openId.includes(issueId.toString()) ? caseShow : '-';
      },
    }];
    const filterColumns = [
      {
        title: '类型',
        dataIndex: 'issueTypeId',
        key: 'issueTypeId',
        // filters: issueTypes.map(type => ({ text: type.name, value: type.id.toString() })),
        // filterMultiple: true,
      },
      // {
      //   title: '经办人',
      //   dataIndex: 'assignee',
      //   key: 'assignee',
      //   filters: [],
      // },
      {
        title: '编号',
        dataIndex: 'issueNum',
        key: 'issueNum',
        filters: [],
      },
      {
        title: '概要',
        dataIndex: 'summary',
        key: 'summary',
        filters: [],
      },
      // {
      //   title: '优先级',
      //   dataIndex: 'priorityCode',
      //   key: 'priorityCode',
      //   filters: [
      //     {
      //       text: '高',
      //       value: 'high',
      //     },
      //     {
      //       text: '中',
      //       value: 'medium',
      //     },
      //     {
      //       text: '低',
      //       value: 'low',
      //     },
      //   ],
      //   filterMultiple: true,
      // },
      {
        title: '状态',
        dataIndex: 'statusId',
        key: 'statusId',
        filters: issueStatusList.map((status) => ({ text: status.name, value: status.id.toString() })),
        filterMultiple: true,
        // filteredValue: IssueStore.filteredInfo.statusId || null,
      },
      // {
      //   title: '冲刺',
      //   dataIndex: 'sprint',
      //   key: 'sprint',
      //   filters: [],
      // },
      // {
      //   title: '模块',
      //   dataIndex: 'component',
      //   key: 'component',
      //   filters: [],
      // },
      // {
      //   title: '版本',
      //   dataIndex: 'version',
      //   key: 'version',
      //   filters: [],
      // },
      // {
      //   title: '史诗',
      //   dataIndex: 'epic',
      //   key: 'epic',
      //   filters: [],
      // },
    ];
    return (
      <Page
        className="c7ntest-report-test"
      >
        <Header
          title={<FormattedMessage id="report_defectToDemand" />}
          backPath={`/charts?type=${urlParams.type}&id=${urlParams.id}&name=${urlParams.name}&organizationId=${organizationId}&orgId=${organizationId}`}
        >
          <ReporterSwitcher />
          <Button onClick={this.getInfo} style={{ marginLeft: 30 }}>
            <Icon type="autorenew icon" />
            <span>
              <FormattedMessage id="refresh" />
            </span>
          </Button>
        </Header>
        <Breadcrumb title="可跟踪性报告：缺陷 -> 执行 -> 测试 -> 要求" />
        <Content style={{ paddingTop: 0 }}>
          <div style={{ display: 'flex' }} />
          <div className="c7ntest-report-test-filter-table">
            <Table
              rowKey={(record) => record.id}
              columns={filterColumns}
              dataSource={[]}
              filterBar
              showHeader={false}
              onChange={this.handleFilterChange}
              pagination={false}
              // 设置筛选input内默认文本
              // filters={IssueStore.barFilters || []}
              filterBarPlaceholder="过滤表"
            />
          </div>
          <Table
            filterBar={false}
            loading={loading}
            pagination={pagination}
            columns={columns}
            dataSource={reportList}
            onChange={this.handleTableChange}
          />
        </Content>
      </Page>
    );
  }
}

export default ReportTest;
