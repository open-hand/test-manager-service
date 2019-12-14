import React, { Component } from 'react';
import {
  Page, Header, Content, stores, Breadcrumb,
} from '@choerodon/boot';
import {
  Tooltip, Button, Icon, Select, Spin,
} from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import ReactEcharts from 'echarts-for-react';
import { getPlanList, getStatusByFolder } from '../../../api/TestPlanApi';
import ReporterSwitcher from '../components';
import { getProjectName } from '../../../common/utils';
import EmptyPng from '../../../assets/empty.png';
import Empty from '../../../components/Empty';
import './ReportProgress.scss';

const { AppState } = stores;
const { Option } = Select;
class ReportProgress extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: true,
      currentPlanId: undefined,
      planList: [],
      versionProgress: {},
    };
  }

  componentDidMount() {
    this.loadData();
  }

  loadData = () => {
    this.setState({
      loading: true,
      currentPlanId: undefined,
    });
    getPlanList().then((res) => {
      this.setState({
        loading: false,
      });
      if (res && res.length > 0) {
        const latestPlanId = Math.max.apply(null, res.map(item => item.planId));
        if (latestPlanId !== -Infinity) {
          this.loadProgressByPlan(latestPlanId, latestPlanId);
        }
        this.setState({
          planList: res.reverse(),
        });
      }
    }).catch((e) => {
      this.setState({
        loading: false,
      });
    });
  }

  loadProgressByPlan = (planId, folderId) => {
    this.setState({
      loading: true,
    });
    getStatusByFolder({ planId, folderId }).then((res) => {
      this.setState({
        currentPlanId: planId,
        versionProgress: res,
        loading: false,
      });
    }).catch((e) => {
      /* console.log(e); */
      this.setState({
        loading: false,
      });
    });
  }

  handlePlanChange = (value) => {
    this.loadProgressByPlan(value, value);
  }

  getOption() {
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c}',
      },
      series: [
        {
          name: '执行进度',
          type: 'treemap',
          roam: false,
          nodeClick: false,
          breadcrumb: { // 显示当前路径，面包屑
            show: false,
          },
          itemStyle: {
            normal: {
              label: {
                show: true,
                formatter: '{b}: {c}',
                textStyle: {
                  color: 'white',
                  fontSize: 14,
                },
              },
            },
          },
          data: this.state.versionProgress.statusVOList.map(item => ({
            name: item.statusName,
            value: item.count,
            itemStyle: {
              color: item.statusColor,
            },
          })),
        },
      ],
    };

    return option;
  }

  renderContent = () => {
    const {
      loading, versionProgress, planList,
    } = this.state;
    if (loading) {
      return (
        <div className="c7ntest-spinning-wrapper">
          <Spin spinning={loading} />
        </div>
      );
    }
    if (versionProgress.total > 0) {
      return (
        <div className="c7ntest-chartAndTable">
          <ReactEcharts
            style={{ width: '60%', height: 350 }}
            option={this.getOption()}
          />
          <div className="c7ntest-tableContainer">
            <p className="c7ntest-table-title"><FormattedMessage id="report_progress_table_title" /></p>
            <div style={{ overflowY: 'scroll', maxHeight: 300 }}>
              <table>
                <tr>
                  <td style={{ width: '158px', paddingBottom: 15 }}><FormattedMessage id="report_progress_table_statusTd" /></td>
                  <td style={{ width: '62px', paddingBottom: 15 }}><FormattedMessage id="report_progress_table_countTd" /></td>
                </tr>
                {
                versionProgress.statusVOList.map((item, index) => (
                  <tr>
                    <td style={{ display: 'flex', paddingBottom: 8 }}>
                      <div className="c7ntest-table-icon" style={{ background: item.statusColor }} />
                      <Tooltip title={item.statusName}>
                        <div className="c7ntest-table-name">{item.statusName}</div>
                      </Tooltip>
                    </td>
                    <td style={{ width: '62px', paddingRight: 15, paddingBottom: 8 }}>{item.count}</td>
                  </tr>
                ))
              }
              </table>
            </div>
          </div>
        </div>
      );
    } else {
      return (
        <Empty
          loading={loading}
          pic={EmptyPng}
          title={`${planList && planList.length > 0 ? '暂无测试用例' : '暂无计划'}`} 
          description={`${planList && planList.length > 0 ? '当前计划下暂无测试用例' : '当前项目下无计划'}`}
        />
      );
    }
  }

  render() {
    const urlParams = AppState.currentMenuType;
    const { organizationId } = AppState.currentMenuType;
    const {
      planList, currentPlanId,
    } = this.state;
    return (
      <Page className="c7ntest-report-progress">
        <Header
          title={<FormattedMessage id="report_defectToProgress" />}
          backPath={`/charts?type=${urlParams.type}&id=${urlParams.id}&name=${urlParams.name}&organizationId=${organizationId}&orgId=${organizationId}`}
        >
          <ReporterSwitcher />
          <Button onClick={this.loadData} style={{ marginLeft: 30 }}>
            <Icon type="autorenew icon" />
            <span>
              <FormattedMessage id="refresh" />
            </span>
          </Button>
        </Header>
        <Breadcrumb title={<FormattedMessage id="report_progress_content_title" values={{ name: getProjectName() }} />} />
        <Content>
          <div className="c7ntest-switch">
            <div className="c7ntest-switchVersion">
              <Select
                className="c7ntest-version-filter-item"
                getPopupContainer={triggerNode => triggerNode.parentNode}
                value={currentPlanId}
                label="计划"
                onChange={this.handlePlanChange}
              >
                {
                  planList.map(item => (
                    <Option value={item.planId} key={item.planId}>{item.name}</Option>
                  ))
                }
              </Select>
            </div>
          </div>
          {this.renderContent()}
        </Content>
      </Page>
    );
  }
}
export default ReportProgress;
