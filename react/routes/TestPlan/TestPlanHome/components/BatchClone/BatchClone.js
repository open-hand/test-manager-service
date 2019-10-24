import React, { Component, Fragment } from 'react';
import { Choerodon } from '@choerodon/boot';
import { Content, stores, WSHandler } from '@choerodon/boot';
import {
  Modal, Progress, Table, Button, Icon, Tooltip, Spin,
} from 'choerodon-ui';
import { pull, pullAll, intersection } from 'lodash';
import { getCycleTreeByVersionId, getLastCloneData, batchClone } from '../../../../../api/cycleApi';
import { SelectFocusLoad } from '../../../../../components';
import TestPlanStore from '../../../stores/TestPlanStore';

const { AppState } = stores;
const { Sidebar } = Modal;

class BatchClone extends Component {
  state = {
    visible: false,
    selectCycleKeys: [],
    selectFolderKeys: [],
    data: [],
    sourceVersionId: null,
    targetVersionId: null,
    cloningData: {
      rate: 0,
    },
    cloning: false,
    tableLoading: false,
    lastCloneData: { successfulCount: 0 },
  }


  open = () => {
    // this.loadLastCloneData();
    this.setState({
      data: [],
      visible: true,
      cloningData: {
        rate: 0,
      },
      sourceVersionId: null,
      targetVersionId: null,
      selectCycleKeys: [],
      selectFolderKeys: [],
    });
  }

  close = () => {
    this.setState({
      visible: false,
    });
  }

  loadLastCloneData = () => {
    getLastCloneData().then((res) => {
      this.setState({
        lastCloneData: res,
        cloning: false,
      });
    });
  }

  loadCycleTreeByVersionId = (versionId) => {
    getCycleTreeByVersionId(versionId).then((res) => {
      this.setState({
        data: res.cycle,
        tableLoading: false,
      });
    });
  }

  handleSourceVersionChange = (versionId) => {
    this.setState({
      selectCycleKeys: [],
      selectFolderKeys: [],
      tableLoading: true,
      sourceVersionId: versionId,
    });
    this.loadCycleTreeByVersionId(versionId);
  }

  handleTargetVersionChange = (targetVersionId) => {
    this.setState({
      targetVersionId,
    });
  }

  handleRowSelect = (record, selected, selectedRows, nativeEvent) => {
    const { data } = this.state;
    let selectFolderKeys = selectedRows.filter(row => row.type === 'folder').map(row => row.cycleId);
    const selectCycleKeys = selectedRows.filter(row => row.type === 'cycle').map(row => row.cycleId);
    const { type } = record;
    // 循环
    if (type === 'cycle') {
      const targetCycle = data.find(item => item.cycleId === record.cycleId);
      const folderKeys = targetCycle.children.map(folder => folder.cycleId);
      if (selected) {
        selectFolderKeys = [...selectFolderKeys, ...folderKeys];
        selectCycleKeys.push(record.cycleId);
      } else {
        pull(selectCycleKeys, record.cycleId);
        // 取消子元素
        pullAll(selectFolderKeys, folderKeys);
      }
      // 阶段
    } else {
      const { parentCycleId } = record;
      const parentCycle = data.find(item => item.cycleId === parentCycleId);
      const folderKeys = parentCycle.children.map(folder => folder.cycleId);
      if (selected) {
        // 如果没有选择父cycle，则自动选上
        if (!selectCycleKeys.includes(parentCycleId)) {
          selectCycleKeys.push(parentCycleId);
        }
      } else {
        // 取消时，如果同级只剩自己，则取消父的选择
        // eslint-disable-next-line no-lonely-if
        if (intersection(selectFolderKeys, folderKeys).length === 0) {
          pull(selectCycleKeys, parentCycleId);
        }
      }
    }
    // console.log(selectedRowKeys);

    this.setState({
      selectCycleKeys: [...new Set(selectCycleKeys)],
      selectFolderKeys: [...new Set(selectFolderKeys)],
    });
  }

  handleSelectAll = (selected, selectedRows, changeRows) => {
    const selectFolderKeys = selectedRows.filter(row => row.type === 'folder').map(row => row.cycleId);
    const selectCycleKeys = selectedRows.filter(row => row.type === 'cycle').map(row => row.cycleId);
    this.setState({
      selectCycleKeys,
      selectFolderKeys,
    });
  }

  handleOk = () => {
    const {
      selectCycleKeys, selectFolderKeys, data, targetVersionId,
    } = this.state;
    if (!targetVersionId) {
      Choerodon.prompt('请选择目标版本');
      return;
    }
    const cloneDTO = [];
    selectCycleKeys.forEach((cycleKey) => {
      const cycle = { cycleId: cycleKey, folderIds: [] };
      const targetCycle = data.find(item => item.cycleId === cycleKey);
      targetCycle.children.forEach((folder) => {
        if (selectFolderKeys.includes(folder.cycleId)) {
          cycle.folderIds.push(folder.cycleId);
        }
      });
      cloneDTO.push(cycle);
    });
    if (cloneDTO.length > 0) {
      this.setState({
        cloningData: {
          rate: 0,
        },
        cloning: true,
      });
      batchClone(targetVersionId, cloneDTO).then((res) => {
        if (res.failed) {
          Choerodon.prompt('目标版本含有同名循环或阶段');
          this.setState({
            cloning: false,
          });
        }
      }).catch(() => {
        this.setState({
          cloning: false,
        });
      });
    } else {
      Choerodon.prompt('请选择循环或阶段');
    }
  }

  handleMessage = (res) => {
    /* console.log(data); */
    if (res !== 'ok') {
      const data = JSON.parse(res);
      const { failedCount, rate, status } = data;
      if (status === 3) {
        Choerodon.prompt('循环或阶段时间范围不可为空');
      }
      this.setState({
        cloningData: data,
        cloning: status !== 3,
      });
      if (rate === 1 && this.state.visible) {
        this.handleDone();
      }
    }
  }

  handleClose = () => {
    const { onOk } = this.props;
    this.setState({
      cloning: false,
      visible: false,
    });
    onOk();
  }

  handleDone = () => {
    const { targetVersionId } = this.state;
    setTimeout(() => {
      TestPlanStore.selectDefaultVersion(targetVersionId);
      TestPlanStore.getTree();
    }, 300);
  }

  render() {
    const {
      data, visible, targetVersionId, sourceVersionId, lastCloneData, cloning, cloningData, tableLoading,
    } = this.state;
    const { rate, status } = cloningData;
    const progress = (rate * 100).toFixed(2);
    const columns = [{
      title: '名称',
      render: record => record.cycleName,
    }];
    const { selectFolderKeys, selectCycleKeys } = this.state;
    const selectedRowKeys = [...new Set([...selectFolderKeys, ...selectCycleKeys])];
    return (
      <Sidebar
        destroyOnClose
        title="批量克隆"
        visible={visible}
        onCancel={this.close}
        onOk={this.handleOk}
        confirmLoading={cloning}
        footer={cloning ? (
          <Button
            type="primary"
            funcType="raised"
            onClick={this.handleClose}
          >
            完成
          </Button>
        ) : [
          <Button
            type="primary"
            funcType="raised"
            onClick={this.handleOk}
            disabled={cloning || !targetVersionId || !selectedRowKeys.length > 0}
          >
              确定
          </Button>,
          <Button type="primary" funcType="raised" onClick={this.close}>
              关闭
          </Button>]}
      >
        <Content
          style={{
            padding: '0 0 10px 0',
          }}
        >
          <div className="c7ntest-BatchClone" style={{ paddingTop: 10 }}>
            <div style={{ marginBottom: 24, display: 'flex', alignItems: 'center' }}>
              <SelectFocusLoad
                disabled={cloning}
                label="版本"
                filter={false}
                loadWhenMount
                optionDisabled={version => version.versionId === targetVersionId}
                type="version"
                style={{ width: 160 }}
                onChange={this.handleSourceVersionChange}
              />
              <SelectFocusLoad
                disabled={cloning || !sourceVersionId}
                label="克隆到"
                filter={false}
                loadWhenMount
                optionDisabled={version => version.versionId === sourceVersionId}
                type="version"
                style={{ marginLeft: 20, width: 160 }}
                onChange={this.handleTargetVersionChange}
              />
            </div>
            <WSHandler
              messageKey={`choerodon:msg:test-cycle-batch-clone:${AppState.userInfo.id}`}
              onMessage={this.handleMessage}
            >
              {
                cloning ? (
                  <div style={{ textAlign: 'center' }}>
                    <Tooltip title={`进度：${progress}%`}>
                      <Progress type="circle" status="active" percent={progress} />
                    </Tooltip>
                  </div>
                ) : (
                  <Table
                    filterBar={false}
                    pagination={false}
                    rowKey="cycleId"
                    columns={columns}
                    rowSelection={{
                      selectedRowKeys,
                      onSelectAll: this.handleSelectAll,
                      onSelect: this.handleRowSelect,
                      disabled: true,
                    }}
                    dataSource={data}
                  />
                )
              }
            </WSHandler>
          </div>
        </Content>
      </Sidebar>
    );
  }
}

BatchClone.propTypes = {

};

export default BatchClone;
