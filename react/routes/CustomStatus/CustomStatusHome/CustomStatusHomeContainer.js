/*
 * @Author: LainCarl 
 * @Date: 2019-01-25 11:36:37 
 * @Last Modified by: LainCarl
 * @Last Modified time: 2019-01-25 13:54:51
 * @Feature: 用户自定状态容器组件
 */

import React, { Component } from 'react';
import { Choerodon } from '@choerodon/boot';
import _ from 'lodash';
import {
  getStatusList, createStatus, editStatus, deleteStatus,
} from '../../../api/TestStatusApi';
import CustomStatusHome from './CustomStatusHome';

class CustomStatusHomeContainer extends Component {
  state = {
    loading: false,
    statusType: 'CYCLE_CASE',
    createVisible: false,
    editVisible: false,
    statusList: [],
    CurrentEditStatus: {
      statusId: null,
      statusType: 'CYCLE_CASE',
      objectVersionNumber: null,
      statusName: null,
      description: null,
      statusColor: null,
    },
    EditStatusLoading: false,
    CreateStatusLoading: false,
  };

  componentDidMount() {
    this.loadStatusList(this.state.statusType);
  }

  loadStatusList = (statusType = this.state.statusType) => {
    this.setState({ loading: true });    
    getStatusList(statusType).then((statusList) => {
      this.setState({
        loading: false,
        statusList,
      });
    }).catch(() => {
      this.setState({
        loading: false,
      });
      Choerodon.prompt('网络异常');
    });
  };

  /**
   * 切换创建状态侧边栏的显示状态
   *
   * 
   */
  ToggleCreateStatusVisible = (visible) => {
    this.setState({
      createVisible: visible,
    });
  }

  /**
   * 切换编辑状态侧边栏的显示状态
   *
   * 
   */
  ToggleEditStatusVisible = (visible, data = {}) => {
    this.setState({
      editVisible: visible,
      CurrentEditStatus: data,
    });
  }

  /**
   * tab切换时更改当前状态类型
   *
   * 
   */
  handleTabChange = (key) => {
    this.setState({
      statusType: key,
    });
    this.loadStatusList(key);
  }

  /**
   * 刷新状态列表
   *
   * 
   */
  handleRefreshClick = () => {
    this.loadStatusList();
  }

  /**
   * 点击删除状态按钮的处理
   *
   * 
   */
  handleDeleteOk = (data) => {
    this.setState({
      loading: true,
    });
    deleteStatus(data.statusId).then((res) => {
      if (res.failed) {
        Choerodon.prompt('状态已被使用，不可删除');
      }
      this.setState({
        loading: false,
      });
      this.loadStatusList();
    }).catch(() => {
      this.setState({
        loading: false,
      });
    });
  };

  /**
   * 点击编辑状态按钮的处理
   * 1.显示编辑侧边栏
   * 2.设置当前编辑状态数据
   */
  handleEditStatusClick = (statusData) => {
    this.ToggleEditStatusVisible(true, statusData);
  }

  /**
   * 点击显示状态创建侧边
   *
   * 
   */
  handleShowCreateClick = () => {
    this.ToggleCreateStatusVisible(true);
  }

  /**
   * 创建状态侧边点击取消
   *
   * 
   */
  handleCreateStatusCancel = () => {
    this.ToggleCreateStatusVisible(false);
  }

  /**
   * 编辑状态侧边点击取消
   *
   * 
   */
  handleEditStatusCancel = () => {
    this.ToggleEditStatusVisible(false);
  }

  /**
   * 校验状态是否重复
   *
   * 
   */
  handleCheckStatusRepeat = status => (rule, value, callback) => {
    const {
      statusName, statusColor, statusType, statusId, 
    } = status;
    getStatusList(statusType).then((statusList) => {
      if (_.find(statusList, o => o.statusName === statusName.trim() && o.statusId !== statusId)) {
        callback('状态名称已存在');
      } else if (_.find(statusList, o => o.statusColor === statusColor && o.statusId !== statusId)) {
        callback('状态颜色已存在');
      } else {
        callback();
      }
    }).catch(() => {
      callback();      
    });
  };

  /**
   * 创建状态侧边点击确定
   *
   * 
   */
  handleCreateStatusSubmit = (newStatus) => {
    this.setState({ CreateStatusLoading: true });
    createStatus(newStatus).then((res) => {
      if (res.failed) {
        Choerodon.prompt('状态或颜色不能相同');
      } else {
        this.setState({
          statusType: newStatus.statusType,
        });
        this.ToggleCreateStatusVisible(false);
        this.loadStatusList();
      }
      this.setState({ CreateStatusLoading: false });
    }).catch(() => {
      Choerodon.prompt('网络异常');
      this.setState({ CreateStatusLoading: false });
    });
  }

  /**
   * 编辑状态侧边点击确定
   *
   * 
   */
  handleEditStatusSubmit = (modifyedStatus) => {
    this.setState({ EditStatusLoading: true });
    editStatus(modifyedStatus).then((res) => {
      if (res.failed) {
        Choerodon.prompt('状态或颜色不能相同');
      } else {
        this.ToggleEditStatusVisible(false);
        this.loadStatusList();
      }
      this.setState({ EditStatusLoading: false });
    }).catch(() => {
      Choerodon.prompt('网络异常');
      this.setState({ EditStatusLoading: false });
    });
  }

  render() {
    return (
      <CustomStatusHome
        {...this.state}
        onShowCreateClick={this.handleShowCreateClick}
        onRefreshClick={this.handleRefreshClick}
        onEditStatusClick={this.handleEditStatusClick}
        onDeleteOk={this.handleDeleteOk}
        onTabChange={this.handleTabChange}
        onCheckStatusRepeat={this.handleCheckStatusRepeat}
        onCreateStatusCancel={this.handleCreateStatusCancel}
        onEditStatusCancel={this.handleEditStatusCancel}
        onCreateStatusSubmit={this.handleCreateStatusSubmit}
        onEditStatusSubmit={this.handleEditStatusSubmit}
      />
    );
  }
}


export default CustomStatusHomeContainer;
