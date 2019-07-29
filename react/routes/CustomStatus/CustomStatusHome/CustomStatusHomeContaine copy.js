/*
 * @Author: LainCarl 
 * @Date: 2019-01-25 11:36:37 
 * @Last Modified by: LainCarl
 * @Last Modified time: 2019-01-25 13:54:51
 * @Feature: 用户自定状态容器组件
 */

/* 将原文件class组件改为如下函数组件（未完成版） */

import React, { Component, useState, useEffect } from 'react';
import _ from 'lodash';
import {
  getStatusList, createStatus, editStatus, deleteStatus,
} from '../../../api/TestStatusApi';
import CustomStatusHome from './CustomStatusHome';


function CustomStatusHomeContainer() {
  const [loading, setLoading] = useState(false);
  const [statusType, setStatusType] = useState('CYCLE_CASE');
  const [createVisible, setCreateVisible] = useState(false);
  const [editVisible, setEditVisible] = useState(false);
  const [statusList, setStatusList] = useState([]);
  const [CurrentEditStatus, setCurrentEditStatus] = useState({
    statusId: null,
    statusType: 'CYCLE_CASE',
    objectVersionNumber: null,
    statusName: null,
    description: null,
    statusColor: null,
  });
  const [EditStatusLoading, setEditStatusLoading] = useState(false);
  const [CreateStatusLoading, setCreateStatusLoading] = useState(false);

  const loadStatusList = (thisstatusType = { statusType }) => {
    setLoading(true);
    getStatusList(thisstatusType).then((thisstatusList) => {
      setLoading(false);
      setStatusList(thisstatusList);
    }).catch(() => {
      setLoading(false);
      Choerodon.prompt('网络异常');
    });
  };
  
  useEffect(() => {
    loadStatusList({ statusType });
  }, []);
  

  /**
   * 切换创建状态侧边栏的显示状态
   *
   * 
   */
  const ToggleCreateStatusVisible = (visible) => {
    setCreateVisible(visible);
  };

  /**
   * 切换编辑状态侧边栏的显示状态
   *
   * 
   */
  const ToggleEditStatusVisible = (visible, data = {}) => {
    setEditVisible(visible);
    setCurrentEditStatus(data);
  };

  /**
   * tab切换时更改当前状态类型
   *
   * 
   */
  const handleTabChange = (key) => {
    setStatusType(key);
    loadStatusList(key);
  };

  /**
   * 刷新状态列表
   *
   * 
   */
  const handleRefreshClick = () => {
    loadStatusList();
  };

  /**
   * 点击删除状态按钮的处理
   *
   * 
   */
  const handleDeleteOk = (data) => {
    setLoading(true);
    deleteStatus(data.statusId).then((res) => {
      if (res.failed) {
        Choerodon.prompt('状态已被使用，不可删除');
      }
      setLoading(false);
      loadStatusList();
    }).catch(() => {
      setLoading(false);
    });
  };

  /**
   * 点击编辑状态按钮的处理
   * 1.显示编辑侧边栏
   * 2.设置当前编辑状态数据
   */
  const handleEditStatusClick = (statusData) => {
    ToggleEditStatusVisible(true, statusData);
  };

  /**
   * 点击显示状态创建侧边
   *
   * 
   */
  const handleShowCreateClick = () => {
    ToggleCreateStatusVisible(true);
  };

  /**
   * 创建状态侧边点击取消
   *
   * 
   */
  const handleCreateStatusCancel = () => {
    ToggleCreateStatusVisible(false);
  };

  /**
   * 编辑状态侧边点击取消
   *
   * 
   */
  const handleEditStatusCancel = () => {
    ToggleEditStatusVisible(false);
  };

  /**
   * 校验状态是否重复
   *
   * 
   */
  const handleCheckStatusRepeat = status => (rule, value, callback) => {
    const {
      statusName, statusColor, thisstatusType, statusId, 
    } = status;
    getStatusList(thisstatusType).then((thisstatusList) => {
      if (_.find(thisstatusList, o => o.statusName === statusName && o.statusId !== statusId)) {
        callback('状态名称已存在');
      } else if (_.find(thisstatusList, o => o.statusColor === statusColor && o.statusId !== statusId)) {
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
  const handleCreateStatusSubmit = (newStatus) => {
    setCreateStatusLoading(true);
    createStatus(newStatus).then((res) => {
      if (res.failed) {
        Choerodon.prompt('状态或颜色不能相同');
      } else {
        setStatusType(newStatus.statusType);
        ToggleCreateStatusVisible(false);
        loadStatusList();
      }
      setCreateStatusLoading(false);
    }).catch(() => {
      Choerodon.prompt('网络异常');
      setCreateStatusLoading(false);
    });
  };

  /**
   * 编辑状态侧边点击确定
   *
   * 
   */
  const handleEditStatusSubmit = (modifyedStatus) => {
    setEditStatusLoading(true);
    editStatus(modifyedStatus).then((res) => {
      if (res.failed) {
        Choerodon.prompt('状态或颜色不能相同');
      } else {
        ToggleEditStatusVisible(false);
        loadStatusList();
      }
      setEditStatusLoading(false);
    }).catch(() => {
      Choerodon.prompt('网络异常');
      setEditStatusLoading(false);
    });
  };
  
  return (
    <CustomStatusHome
      loading={loading}
      statusType={statusType}
      createVisible={createVisible}
      editVisible={editVisible}
      statusList={statusList}
      CurrentEditStatus={CurrentEditStatus}
      EditStatusLoading={EditStatusLoading}
      CreateStatusLoading={CreateStatusLoading}

      onShowCreateClick={handleShowCreateClick}
      onRefreshClick={handleRefreshClick}
      onEditStatusClick={handleEditStatusClick}
      onDeleteOk={handleDeleteOk}
      onTabChange={handleTabChange}
      onCheckStatusRepeat={handleCheckStatusRepeat}
      onCreateStatusCancel={handleCreateStatusCancel}
      onEditStatusCancel={handleEditStatusCancel}
      onCreateStatusSubmit={handleCreateStatusSubmit}
      onEditStatusSubmit={handleEditStatusSubmit}
    />
  );
}
export default CustomStatusHomeContainer;
