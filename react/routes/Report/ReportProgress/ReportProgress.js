import React, { Component } from 'react';
import {
  Page, Header, Content, stores,
} from '@choerodon/boot';
import {
  Tooltip, Button, Icon, Select, Spin,
} from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import ReactEcharts from 'echarts-for-react';
import _ from 'lodash';
import { getProjectVersion } from '../../../api/agileApi';
import loadProgressByVersion from '../../../api/DashBoardApi';
import { getCyclesByVersionId } from '../../../api/cycleApi';
import ReporterSwitcher from '../components';
import { getProjectName } from '../../../common/utils';
import EmptyCase from '../../../assets/emptyCase.svg';

import './ReportProgress.scss';

const { AppState } = stores;
const { Option } = Select;
class ReportProgress extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: true,
      currentVersion: null,
      currentCycle: null,
      versionList: [],
      cycleList: [],
      versionProgress: [],
    };
  }

  componentDidMount() {
    this.loadData();
  }

  loadData = () => {
    this.setState({
      loading: true,
      currentVersion: null,
      currentCycle: null,
    });
    getProjectVersion().then((res) => {
      this.setState({
        loading: false,
      });
      if (res && res.length > 0) {
        const latestVersionId = Math.max.apply(null, res.map(item => item.versionId));
        // console.log(latestVersionId);
        if (latestVersionId !== -Infinity) {
          this.loadProgressByVersion(latestVersionId);
          this.loadCyclesByVersionId(latestVersionId);
        }
        this.setState({
          versionList: res.reverse(),
        });
      }
    }).catch((e) => {
      /* console.log(e); */
      this.setState({
        loading: false,
      });
    });
  }

  loadProgressByVersion = (versionId, cycleId) => {
    // console.log('load', versionId);
    this.setState({
      loading: true,
    });
    loadProgressByVersion(versionId, cycleId).then((res) => {
      this.setState({
        currentVersion: versionId,
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

  loadCyclesByVersionId = (versionId) => {
    getCyclesByVersionId(versionId).then((res) => {
      this.setState({
        cycleList: res,
      });
    });
  }

  handleVersionChange = (value) => {
    this.loadProgressByVersion(value);
    this.setState({
      currentCycle: null,
    });
    this.loadCyclesByVersionId(value);
  }

  handleCycleChange = (value) => {
    const { currentVersion } = this.state;
    this.setState({
      currentCycle: value,
    });
    this.loadProgressByVersion(currentVersion, value);
  }

  getOption() {
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c}',
      },
      //   hoverable: true,
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
          data: this.state.versionProgress.map(item => ({
            name: item.name,
            value: item.counts,
            itemStyle: {
              color: item.color,
            },
          })),
          // [
          //   {
          //     name: '未执行',
          //     value: 6,
          //     itemStyle: {                
          //       color: 'rgba(0, 0, 0, 0.18)',              
          //     },  
          //   }]
        },
      ],
    };

    return option;
  }

  renderContent = () => {
    const {
      loading, currentVersion, currentCycle, versionProgress,
    } = this.state;
    if (loading) {
      return (
        <div className="c7ntest-spinning-wrapper">
          <Spin spinning={loading} />
        </div>
      );
    }
    if (versionProgress && versionProgress.length > 0) {
      return (
        <div className="c7ntest-chartAndTable">
          <ReactEcharts
            style={{ width: '60%', height: 350 }}
            option={this.getOption()}
          />
          <div className="c7ntest-tableContainer">
            <p className="c7ntest-table-title"><FormattedMessage id="report_progress_table_title" /></p>
            <table>
              <tr>
                <td style={{ width: '158px', paddingBottom: 15 }}><FormattedMessage id="report_progress_table_statusTd" /></td>
                <td style={{ width: '62px', paddingBottom: 15 }}><FormattedMessage id="report_progress_table_countTd" /></td>
              </tr>
              {
                versionProgress.map((item, index) => (
                  <tr>
                    <td style={{ display: 'flex', paddingBottom: 8 }}>
                      <div className="c7ntest-table-icon" style={{ background: item.color }} />
                      <Tooltip title={item.name}>
                        <div className="c7ntest-table-name">{item.name}</div>
                      </Tooltip>
                    </td>
                    <td style={{ width: '62px', paddingRight: 15, paddingBottom: 8 }}>{item.counts}</td>
                  </tr>
                ))
              }
            </table>
          </div>
        </div>
      );
    } else {
      return (
        <div className="c7ntest-emptyCase">
          <img src={EmptyCase} title="没有测试用例" alt="没有测试用例" />
          <div className="c7ntest-emptyCase-detail">{`${currentVersion ? '当前版本' : ''}${currentCycle ? '的当前循环' : ''}${currentVersion ? '下' : ''}没有测试用例`}</div>
        </div>
      );
    }
  }

  render() {
    const urlParams = AppState.currentMenuType;
    const { organizationId } = AppState.currentMenuType;
    const {
      loading, versionList, currentVersion, cycleList, currentCycle, versionProgress,
    } = this.state;
    return (
      <Page className="c7ntest-report-progress">
        <Header
          title={<FormattedMessage id="report_defectToProgress" />}
          backPath={`/charts?type=${urlParams.type}&id=${urlParams.id}&name=${urlParams.name}&organizationId=${organizationId}`}
        >
          <ReporterSwitcher />
          <Button onClick={this.loadData} style={{ marginLeft: 30 }}>
            <Icon type="autorenew icon" />
            <span>
              <FormattedMessage id="refresh" />
            </span>
          </Button>
        </Header>
        <Content>
          <div className="c7ntest-switch">
            <div className="c7ntest-switchVersion">
              <Select
                className="c7ntest-version-filter-item"
                getPopupContainer={triggerNode => triggerNode.parentNode}
                value={currentVersion}
                label={<FormattedMessage id="report_progress_versionLabel" />}
                onChange={this.handleVersionChange}
              >
                {
                  versionList.map(item => (
                    <Option value={item.versionId} key={item.versionId}>{item.name}</Option>
                  ))
                }
              </Select>
            </div>
            <div className="c7ntest-switchCycle">
              <Select
                className="c7ntest-cycle-filter-item"
                getPopupContainer={triggerNode => triggerNode.parentNode}
                value={currentCycle}
                allowClear={!!currentCycle}
                label={<FormattedMessage id="report_progress_cycleLabel" />}
                onChange={this.handleCycleChange}
              >
                {
                  cycleList.map(item => (
                    <Option value={item.cycleId} key={item.cycleName}>{item.cycleName}</Option>
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
