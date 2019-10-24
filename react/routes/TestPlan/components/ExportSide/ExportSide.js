import React, { Component } from 'react';
import { FormattedMessage } from 'react-intl';
import { Content, stores, WSHandler } from '@choerodon/boot';
import { Choerodon } from '@choerodon/boot';
import { observer } from 'mobx-react';
import {
  Modal, Progress, Table, Button, Icon, Tooltip, Select,
} from 'choerodon-ui';
import _ from 'lodash';
import moment from 'moment';
import { getProjectVersion } from '../../../../api/agileApi';
import { SelectVersion, SelectFolder, SimpleSelect } from '../../../../components';
import {
  getCyclesByVersionId, getExportList, getFoldersByCycleId, exportCycle,
} from '../../../../api/cycleApi';
import { humanizeDuration } from '../../../../common/utils';
import './ExportSide.scss';
import TestPlanStore from '../../stores/TestPlanStore';


const { Sidebar } = Modal;
const { Option } = Select;
const { AppState } = stores;

@observer
class ExportSide extends Component {
  state = {
    loading: true,
    visible: false,
    exportList: [],
    versionList: [],
    cycleList: [],
    stageList: [],
  }


  handleClose = () => {
    this.setState({
      visible: false,
    });
    TestPlanStore.setExportVersionId(null);
    TestPlanStore.setExportCycleId(null);
    TestPlanStore.setExportStageId(null);
  }

  open = () => {
    const { exportCycleId, exportVersionId } = TestPlanStore;
    this.setState({
      visible: true,
      loading: true,
    });
    getExportList().then((exportList) => {
      // console.log(moment(moment().unix()).format('YYYY-MM-DD HH:mm:ss'));
      // console.log(JSON.stringify(_.map(_.map(_.orderBy(_.map(exportList, item => {
      //   item.creationDate = moment(item.creationDate).unix();
      //   return item;
      // }), ['creationDate'], ['desc']), 'creationDate'))));

      // console.log(JSON.stringify(_.map(_.map(_.orderBy(_.map(exportList, item => {
      //   item.creationDate = moment(item.creationDate).unix();
      //   return item;
      // }), ['creationDate'], ['desc']), 'creationDate').map(item => moment(item).format('YYYY-MM-DD HH:mm:ss')))));
      this.setState({
        // exportList: _.orderBy(_.map(exportList, item => {
        //   item.creationDate = moment(item.creationDate).unix();
        //   return item;
        // }), ['creationDate'], ['desc']),
        exportList,
        loading: false,
      });
    });

    getProjectVersion().then((versionList) => {
      this.setState({
        versionList,
      });
    });

    if (exportVersionId) {
      this.loadCycles();
    }

    if (exportCycleId) {
      this.loadStages();
    }
  }

  loadCycles=() => {
    const { exportVersionId } = TestPlanStore;
    getCyclesByVersionId(exportVersionId).then((cycleList) => {
      this.setState({
        cycleList,
      });
    });
  }
 
  loadStages=() => {
    const { exportCycleId } = TestPlanStore;
    getFoldersByCycleId(exportCycleId).then((stageList) => {
      this.setState({
        stageList,
      });
    });
  } 

  handleOk = () => {
    this.setState({
      visible: false,
    });
  }

  handleVersionChange = (exportVersionId) => {
    TestPlanStore.setExportVersionId(exportVersionId);
    TestPlanStore.setExportCycleId(null);
    TestPlanStore.setExportStageId(null);
  }

  handleCycleChange = (exportCycleId) => {
    TestPlanStore.setExportCycleId(exportCycleId);
    TestPlanStore.setExportStageId(null);
  }

  handleStageChange = (exportStageId) => {
    TestPlanStore.setExportStageId(exportStageId);
  }

  createExport = () => {
    const { exportVersionId, exportCycleId, exportStageId } = TestPlanStore;
    if (!exportCycleId) {
      Choerodon.prompt('测试循环为必选项');
      return;
    }
    exportCycle(exportStageId || exportCycleId);
  }

  handleDownload = (fileUrl) => {
    if (fileUrl) {
      const ele = document.createElement('a');
      ele.href = fileUrl;
      ele.target = '_blank';
      document.body.appendChild(ele);
      ele.click();
      document.body.removeChild(ele);
    }
  }

  handleMessage = (res) => {
    if (res === 'ok') {
      return;
    }
    const data = JSON.parse(res);
    // console.log(data);
    const theexportList = this.state.exportList;
    const exportList = [...theexportList];
    const { id, rate } = data;
    const index = _.findIndex(exportList, { id });
    // 存在记录就更新，不存在则新增记录
    if (index >= 0) {
      exportList[index] = { ...data, rate: data.rate.toFixed(1) };
    } else {
      exportList.unshift(data);
    }
    this.setState({
      exportList,
    });
  }

  humanizeDuration = (record) => {
    const { creationDate, lastUpdateDate } = record;
    const startTime = moment(creationDate);
    const lastTime = moment(lastUpdateDate);
    const diff = lastTime.diff(startTime);
    return creationDate && lastUpdateDate
      ? humanizeDuration(diff)
      : null;
  }

  render() {
    const {
      visible, exportList, loading, versionList, cycleList, stageList,
    } = this.state;
    const { exportVersionId, exportCycleId, exportStageId } = TestPlanStore;
    const columns = [{
      title: '导出来源',
      dataIndex: 'sourceType',
      key: 'sourceType',
      // width: 100,
      render: (sourceType, record) => {
        const ICONS = {
          1: 'project',
          2: 'version',
          4: 'folder',
        };
        return (
          <div className="c7ntest-center">
            <Icon type={ICONS[sourceType]} />
            <span className="c7ntest-text-dot" style={{ marginLeft: 10 }}>{record.name}</span>
          </div>
        );
      },
    },
    {
      title: '执行个数',
      dataIndex: 'successfulCount',
      key: 'successfulCount',
      // width: 100,
    },
    {
      title: '导出时间',
      dataIndex: 'creationDate',
      key: 'creationDate',
      width: 160,
      render: creationDate => moment(creationDate).format('YYYY-MM-DD HH:mm:ss'),
    }, {
      title: '耗时',
      dataIndex: 'during',
      key: 'during',
      // width: 100,
      render: (during, record) => <div>{this.humanizeDuration(record)}</div>,
    }, {
      title: '进度',
      dataIndex: 'rate',
      key: 'rate',
      render: (rate, record) => (record.status === 2
        ? <div>已完成</div>
        : (
          <Tooltip title={`进度：${rate}%`}>
            <Progress percent={rate} showInfo={false} />
          </Tooltip>
        )),
    }, {
      title: '',
      dataIndex: 'fileUrl',
      key: 'fileUrl',
      render: fileUrl => (
        <div style={{ textAlign: 'right' }}>
          <Tooltip title="下载文件">
            <Button style={{ marginRight: -3 }} shape="circle" funcType="flat" icon="get_app" disabled={!fileUrl} onClick={this.handleDownload.bind(this, fileUrl)} />
          </Tooltip>
        </div>
      ),
    }];
    return (
      <Sidebar
        title="导出测试执行"
        visible={visible}
        footer={<Button onClick={this.handleClose} type="primary" funcType="raised"><FormattedMessage id="close" /></Button>}
      >
        <Content
          style={{
            padding: '0 0 10px 0',
          }}
        >
          <div className="c7ntest-ExportSide">
            <div style={{ marginBottom: 24 }}>
              <Select 
                style={{ width: 100 }} 
                value={versionList && versionList.length > 0 && exportVersionId} 
                label="版本" 
                onChange={this.handleVersionChange}
              >
                {
                 versionList && versionList.length > 0 && (
                   versionList.map(item => (
                     <Option key={item.versionId} value={item.versionId}>{item.name}</Option>
                   ))
                 )
               }
              </Select>
              <Select 
                style={{ width: 200, margin: '0 20px' }} 
                label="测试循环" 
                disabled={!exportVersionId} 
                value={cycleList && cycleList.length > 0 && exportCycleId} 
                onChange={this.handleCycleChange}
                allowClear={!!exportCycleId}
                onFocus={this.loadCycles}
              >
                {
                 cycleList && cycleList.length > 0 && (
                   cycleList.map(item => (
                     <Option key={item.cycleId} value={item.cycleId}>{item.cycleName}</Option>
                   ))
                 )
               }
              </Select>
              <Select 
                style={{ minWidth: 150 }} 
                label="测试阶段" 
                disabled={!exportCycleId} 
                value={stageList && stageList.length > 0 && exportStageId} 
                onChange={this.handleStageChange}
                onFocus={this.loadStages}
                allowClear={!!exportStageId}
              >
                {
                 stageList && stageList.length > 0 && (
                   stageList.map(item => (
                     <Option key={item.cycleId} value={item.cycleId}>{item.cycleName}</Option>
                   ))
                 )
               }
              </Select>

              <Button type="primary" icon="playlist_add" onClick={this.createExport}>新建导出</Button>
            </div>
            <WSHandler
              messageKey={`choerodon:msg:test-cycle-export:${AppState.userInfo.id}`}
              onMessage={this.handleMessage}
            >
              <Table columns={columns} dataSource={exportList} loading={loading} />
            </WSHandler>
          </div>
        </Content>
      </Sidebar>
    );
  }
}


export default ExportSide;
