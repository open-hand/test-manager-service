/* eslint-disable */

import React, { Component } from 'react';
import {
  Page, Header, Content, stores,
} from '@choerodon/boot';
import { Link } from 'react-router-dom';
import {
  Table, Button, Icon, Collapse, Tooltip,
} from 'choerodon-ui';
import _ from 'lodash';
import { FormattedMessage } from 'react-intl';
import { Tags } from '../../../components';
import  ReporterSwitcher from '../components';
import { getReportsFromStory } from '../../../api/reportApi';
import {
  getIssueTypes, getIssueStatus, getProjectVersion, getSprints,
} from '../../../api/agileApi';
import { getStatusList } from '../../../api/TestStatusApi';
import {
  issueLink, TestExecuteLink, executeDetailLink, getProjectName,
} from '../../../common/utils';
import './ReportStory.scss';

export const STATUS = {
  todo: '#ffb100',
  doing: '#4d90fe',
  done: '#00bfa5',
};
const { AppState } = stores;
const { Panel } = Collapse;

class ReportStory extends Component {
  state = {
    loading: false,
    reportList: [],
    statusList: [],
    issueTypes: [],
    versionList: [],
    sprintList: [],
    issueStatusList: [],
    pagination: {
      current: 1,
      total: 0,
      pageSize: 10,
    },
    openId: {},
    search: {
      advancedSearchArgs: {},
      searchArgs: {},
    },
  }

  componentDidMount() {
    this.getInfo();
  }

  getInfo = () => {
    this.getReportsFromStory();
  }


  getReportsFromStory = (pagination, search) => {
    const Pagination = pagination || this.state.pagination;
    const Search = search || this.state.search;
    this.setState({ loading: true });
    Promise.all([
      getReportsFromStory({
        page: Pagination.current,
        size: Pagination.pageSize,
      }, Search),
      getStatusList('CYCLE_CASE'),
      getIssueTypes(),
      getIssueTypes('agile'),
      getIssueStatus('agile'),
      getProjectVersion(),
      getSprints(),
    ])
      .then(([reportData, statusList, issueTypes, agileTypeList, issueStatusList, versionList, sprintList]) => {
        if (reportData.total !== undefined) {
          this.setState({
            loading: false,
            statusList,
            issueTypes: issueTypes.concat(agileTypeList),
            issueStatusList,
            versionList,
            sprintList,
            openId: {},
            reportList: reportData.list,
            pagination: {
              current: Pagination.current,
              pageSize: Pagination.pageSize,
              total: reportData.total,
            },
          });
        }
      }).catch((error) => {
        window.console.log(error);
        this.setState({
          loading: false,
        });
      });
  }

  handleTableChange = (pagination, filters, sorter) => {
    this.getReportsFromStory(pagination);
  }

  handleOpen = (issueId, keys) => {
    const { openId } = this.state;
    openId[issueId] = keys;
    this.setState({
      openId: { ...openId },
    });
  }

  handleFilterChange = (pagination, filters, sorter, barFilters) => {
    const { statusId, priorityCode, typeId } = filters;
    const {
      issueNum, summary, assignee, sprint, version, component, epic,
    } = filters;
    // console.log(barFilters);
    const search = {
      contents: barFilters,
      advancedSearchArgs: {
        statusId: statusId || [],
        // priorityCode: priorityCode || [],
        issueTypeId: typeId || [],
      },
      searchArgs: {
        issueNum: issueNum ? issueNum[0] : '',
        summary: summary ? summary[0] : '',
      },
      otherArgs: {
        sprint: sprint || [],
        version: version || [],
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
    this.getReportsFromStory(Pagination, search);
  }

  render() {
    const {
      reportList, loading, pagination,
      statusList, openId, issueTypes, issueStatusList,
      versionList, sprintList,
    } = this.state;
    const urlParams = AppState.currentMenuType;
    const { organizationId } = AppState.currentMenuType;
    const that = this;
    const filterColumns = [
      {
        title: '类型',
        dataIndex: 'typeId',
        key: 'typeId',
        filters: issueTypes.map(type => ({ text: type.name, value: type.id.toString() })),
        filterMultiple: true,
      },
      {
        title: '版本',
        dataIndex: 'version',
        key: 'version',
        filters: versionList.map(version => ({ text: version.name, value: version.versionId.toString() })),
        filterMultiple: true,
      },
      {
        title: '冲刺',
        dataIndex: 'sprint',
        key: 'sprint',
        filters: sprintList.map(sprint => ({ text: sprint.sprintName, value: sprint.sprintId.toString() })),
        filterMultiple: true,
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
      //   title: '内容',
      //   dataIndex: 'content',
      //   key: 'content',
      //   filters: [],
      // },
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
        filters: issueStatusList.map(status => ({ text: status.name, value: status.id.toString() })),
        filterMultiple: true,
        // filteredValue: IssueStore.filteredInfo.statusCode || null,
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
    const columns = [{
      className: 'c7ntest-table-white',
      title: <FormattedMessage id="demand" />,
      dataIndex: 'issueId',
      key: 'issueId',
      render(issue, record) {
        const { defectInfo, defectCount } = record;
        const {
          statusVO, issueStatusName, issueName, issueId, typeCode, summary, sprintDTOList, versionIssueRelVOList,
        } = defectInfo;
        const { name: statusName, colour: statusColor, type: statusCode } = statusVO || {};
        return (
          <div>
            <div className="c7ntest-collapse-header-container">
              <Tooltip title={(
                <div>
                  <div>{issueName}</div>
                  <div>{summary}</div>
                </div>
              )}
              >
                <Link className="c7ntest-showId" to={issueLink(issueId, typeCode, issueName)}>
                  {issueName}
                </Link>
              </Tooltip>
              <div className="c7ntest-issue-status-icon">
                <span style={{ color: STATUS[statusCode], borderColor: STATUS[statusCode] }}>
                  {statusName}
                </span>
              </div>
              <div style={{ marginLeft: 10 }}>
                <Tags data={sprintDTOList} nameField="sprintName" />
              </div>
              <div>
                <Tags data={versionIssueRelVOList} nameField="name" />
              </div>
            </div>
            <div>
              <FormattedMessage id="report_defectCount" />
              {':'}
              {defectCount}
            </div>
          </div>
        );
      },
    }, {
      className: 'c7ntest-table-white',
      title: <FormattedMessage id="test" />,
      dataIndex: 'test',
      key: 'test',
      width: 200,
      render(test, record) {
        const { issueStatus, linkedTestIssues, defectInfo } = record;
        const { issueId } = defectInfo;
        return (
          <Collapse
            activeKey={openId[issueId]}
            bordered={false}
            onChange={(keys) => { that.handleOpen(issueId, keys); }}
          >
            {
              linkedTestIssues.map((issue, i) => (
                <Panel
                  showArrow={false}
                  header={
                    (
                      <div style={{
                        marginBottom: openId[issueId]
                          && openId[issueId].includes(`${issue.issueId}-${i}`)
                          && issue.testCycleCaseES.length > 1
                          ? (issue.testCycleCaseES.length * 30) - 48 : 0,
                      }}
                      >
                        <div style={{ display: 'flex', alignItems: 'center' }}>
                          <Icon type="navigate_next" className="c7ntest-collapse-icon" />
                          <Tooltip title={issue.issueName}>
                            <Link className="c7ntest-text-dot" to={issueLink(issue.issueId, 'issue_test',issue.issueName)}>
                              {issue.issueName}
                            </Link>
                          </Tooltip>
                        </div>
                        <div className="c7ntest-report-summary">{issue.summary}</div>
                      </div>
                    )}
                  // eslint-disable-next-line react/no-array-index-key
                  key={`${issue.issueId}-${i}`}
                />
              ))
            }
          </Collapse>
        );
      },
    }, {
      className: 'c7ntest-table-white',
      title: <FormattedMessage id="execute" />,
      dataIndex: 'cycleId',
      key: 'cycleId',
      render(cycleId, record) {
        const { linkedTestIssues, defectInfo } = record;
        return (
          <div>
            {linkedTestIssues.map((testIssue, i) => {
              const { testCycleCaseES, issueId } = testIssue;
              const totalExecute = testCycleCaseES.length;
              const executeStatus = {};
              const caseShow = testCycleCaseES.map((execute) => {
                // 执行的颜色
                const { executionStatus } = execute;
                const statusColor = _.find(statusList, { statusId: executionStatus })
                  ? _.find(statusList, { statusId: executionStatus }).statusColor : '';
                const statusName = _.find(statusList, { statusId: executionStatus })
                  && _.find(statusList, { statusId: executionStatus }).statusName;
                if (!executeStatus[statusName]) {
                  executeStatus[statusName] = 1;
                } else {
                  executeStatus[statusName] += 1;
                }
                const marginBottom = Math.max((execute.defects.length + execute.subStepDefects.length) - 1, 0) * 30;
                return (
                  <div className="c7ntest-cycle-show-container" style={{ marginBottom }}>
                    <div
                      style={{
                        width: 80, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis',
                      }}
                    >
                      <Tooltip title={`${execute.cycleName}${execute.folderName ? `/${execute.folderName}` : ''}`}>
                        <Link className="c7ntest-showId" to={TestExecuteLink(execute.cycleId)}>
                          {execute.cycleName}
                          {execute.folderName ? `/${execute.folderName}` : ''}
                        </Link>
                      </Tooltip>
                    </div>
                    <div
                      className="c7ntest-collapse-text-icon"
                      style={{ color: statusColor, borderColor: statusColor }}
                    >
                      {statusName}
                    </div>
                    <Link
                      style={{ lineHeight: '13px' }}
                      to={executeDetailLink(execute.executeId)}
                    >
                      <Icon type="explicit2" style={{ marginLeft: 10, color: 'black' }} />
                    </Link>
                  </div>
                );
              });
              return openId[record.defectInfo.issueId] && openId[record.defectInfo.issueId]
                .includes(`${issueId}-${i}`) ? (
                  <div
                    style={{ minHeight: totalExecute === 0 ? 50 : 30 }}
                  >
                    {caseShow}
                    {' '}

                  </div>
                )
                : (
                  <div style={{ height: 50 }}>
                    <div>
                      <FormattedMessage id="report_total" />
                      {'：'}
                      {totalExecute}
                    </div>
                    <div style={{ display: 'flex' }}>
                      {
                        Object.keys(executeStatus).map(key => (
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
            })}
          </div>
        );
      },
    }, {
      className: 'c7ntest-table-white',
      title: <FormattedMessage id="bug" />,
      dataIndex: 'demand',
      key: 'demand',
      render(demand, record) {
        const { linkedTestIssues, defectInfo } = record;
        return (
          <div>
            {linkedTestIssues.map((testIssue, i) => {
              const { testCycleCaseES, issueId } = testIssue;
              if (testCycleCaseES.length === 0) {
                return <div style={{ minHeight: 50 }} />;
              }
              return (openId[record.defectInfo.issueId] && openId[record.defectInfo.issueId]
                .includes(`${issueId}-${i}`) ? (
                  <div>
                    {
                      testCycleCaseES.map((item) => {
                        const { defects, subStepDefects } = item;
                        return (
                          <div>
                            {defects.concat(subStepDefects).length > 0
                              ? defects.concat(subStepDefects).map((defect) => {
                                const { issueInfosVO } = defect;
                                return (
                                  <div className="c7ntest-issue-show-container">
                                    <Tooltip title={issueInfosVO && issueInfosVO.issueName}>
                                      <Link
                                        className="c7ntest-showId"
                                        to={issueLink(issueInfosVO && issueInfosVO.issueId,
                                          issueInfosVO && issueInfosVO.typeCode, issueInfosVO && issueInfosVO.issueName)}
                                      >
                                        {issueInfosVO && issueInfosVO.issueName}
                                      </Link>
                                    </Tooltip>
                                    <div className="c7ntest-issue-status-icon">
                                      <span style={{
                                        color: issueInfosVO && STATUS[issueInfosVO.statusVO.type],
                                        borderColor: issueInfosVO && STATUS[issueInfosVO.statusVO.type],
                                      }}
                                      >
                                        {issueInfosVO && issueInfosVO.statusVO.name}
                                      </span>
                                    </div>
                                    {defect.defectType === 'CASE_STEP'
                                      && (
                                        <div style={{
                                          marginLeft: 20,
                                          color: 'white',
                                          padding: '0 8px',
                                          background: 'rgba(0,0,0,0.20)',
                                          borderRadius: '100px',
                                          whiteSpace: 'nowrap',
                                        }}
                                        >
                                          <FormattedMessage id="step" />

                                        </div>
                                      )}
                                  </div>
                                );
                              }) : <div className="c7ntest-issue-show-container">－</div>}
                          </div>
                        );
                      })
                    }
                  </div>
                )
                : (
                  <div style={{ minHeight: 50 }}>
                    {
                      testCycleCaseES.map((item) => {
                        const { defects, subStepDefects } = item;
                        return (
                          <div style={{ display: 'flex', flexWrap: 'wrap' }}>
                            {defects.concat(subStepDefects).map((defect, i) => {
                              const { issueInfosVO } = defect;
                              return (
                                <span
                                  className="primary"
                                  style={{
                                    fontSize: '13px',
                                  }}
                                >
                                  <Link className="c7ntest-showId" to={issueLink(issueInfosVO && issueInfosVO.issueId, issueInfosVO && issueInfosVO.typeCode, issueInfosVO && issueInfosVO.issueName)}>
                                    {issueInfosVO && issueInfosVO.issueName}
                                  </Link>
                                  {i === defects.concat(subStepDefects).length - 1 ? null : '，'}
                                </span>
                              );
                            })}
                          </div>
                        );
                      })}
                  </div>
                ));
            })}
          </div>
        );
      },
    }];

    return (
      <Page className="c7ntest-report-story">
        <Header
          title={<FormattedMessage id="report_demandToDefect" />}
          backPath={`/charts?type=${urlParams.type}&id=${urlParams.id}&name=${urlParams.name}&organizationId=${organizationId}`}
        >
          <ReporterSwitcher />
          <Button onClick={this.getInfo} style={{ marginLeft: 30 }}>
            <Icon type="autorenew icon" />
            <span>
              <FormattedMessage id="refresh" />
            </span>
          </Button>
        </Header>
        <Content>
          <div style={{ display: 'flex' }} />
          <div className="c7ntest-report-story-filter-table">
            <Table
              rowKey={record => record.id}
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


export default ReportStory;
