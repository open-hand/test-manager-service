/*
 * @Author: LainCarl 
 * @Date: 2018-12-21 14:16:35 
 * @Last Modified by: LainCarl
 * @Last Modified time: 2018-12-21 14:17:35
 * @Feature:  选择应用以及版本
 */

import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import _ from 'lodash';
import { injectIntl, FormattedMessage } from 'react-intl';
import {
  Modal, Table, Select, Spin,
} from 'choerodon-ui';
import { stores, Content } from '@choerodon/boot';
import './SelectAppAndVersion.scss';
import { getApps, getAppVersions } from '../../../../../../api/AutoTestApi';
import CreateAutoTestStore from '../../../../../../store/project/AutoTest/CreateAutoTestStore';

const SideBar = Modal.Sidebar;
const { AppState } = stores;
const { Option } = Select;
@observer
class SelectAppAndVersion extends Component {
  state = {
    loading: false,
  }

  componentDidMount() {
    this.loadAppAndVersions();
  }

  /**
   * 加载应用以及版本，默认选择第一个应用
   *
   * 
   */
  loadAppAndVersions = (value = '') => {
    const { app, appVersionPagination } = CreateAutoTestStore;
    const { current, pageSize: size } = appVersionPagination;
    let searchParam = {};
    if (value !== '') {
      searchParam = { name: [value] };
    }
    getApps({
      page: current - 1,
      size,
      sort: { field: 'id', order: 'desc' },
      postData: { searchParam, param: '' },
    }).then((data) => {
      // 默认取第一个
      if (data.failed) {
        Choerodon.prompt(data.failed);
        return;
      }
      CreateAutoTestStore.setAppList(data.content);
      const appList = data.content;
      let targetApp = app;
      // 查看store中的app是否存在且合法
      if (!app.id || !_.find(appList, { id: app.id })) {
        if (data.content[0].id) {
          targetApp = data.content[0];
        }
      }
      // 是否有app
      if (targetApp.id) {
        this.loadAppVersions(targetApp.id);
        CreateAutoTestStore.setApp(targetApp);
      } else {
        CreateAutoTestStore.setApp({});
        this.setState({
          loading: false,
        });
      }
    });
  }

  loadApps = (value = '') => {
    const { app, appVersionPagination } = CreateAutoTestStore;
    const { current, pageSize: size } = appVersionPagination;
    let searchParam = {};
    if (value !== '') {
      searchParam = { name: [value] };
    }
    getApps({
      page: current - 1,
      size,
      sort: { field: 'id', order: 'desc' },
      postData: { searchParam, param: '' },
    }).then((data) => {
      // 默认取第一个
      if (data.failed) {
        Choerodon.prompt(data.failed);
        return;
      }
      CreateAutoTestStore.setAppList(data.content);
    });
  }

  loadAppVersions = (appId, pagination = CreateAutoTestStore.appVersionPagination, filters = {}) => {
    this.setState({
      loading: true,
    });
    getAppVersions(appId, pagination, filters).then((data) => {
      CreateAutoTestStore.setAppVersionList(data.content);
      CreateAutoTestStore.setAppVersionPagination({
        current: data.number + 1,
        pageSize: data.size,
        total: data.totalElements,
      });
    }).finally(() => {
      this.setState({
        loading: false,
      });
    });
  }

  /**
   * 切换分页
   * @param page
   * @param size
   */
  // onPageChange = (page, size) => {
  //   const { projectId } = this.state;
  //   this.loadData({
  //     projectId,
  //     page: page - 1,
  //     size,
  //   });
  // };

  /**
   * 获取本项目的app
   * @returns {*}
   */
  getProjectTable = () => {
    const { intl } = this.props;
    const { appVersion, appVersionList, appVersionPagination } = CreateAutoTestStore;
    const column = [{
      key: 'check',
      width: '50px',
      render: record => (
        record.id === appVersion.id && <i className="icon icon-check icon-select" />
      ),
    }, {
      title: <FormattedMessage id="app_name" />,
      dataIndex: 'appName',
      key: 'appName',
      // sorter: true,
      // filters: [],
    }, {
      title: <FormattedMessage id="autoteststep_one_version" />,
      dataIndex: 'version',
      key: 'version',
      filters: [],
      sorter: true,
    }, {
      title: <FormattedMessage id="app_code" />,
      dataIndex: 'appCode',
      key: 'appCode',
      // sorter: true,
      // filters: [],
    }];
    return (
      <Table
        filterBarPlaceholder={intl.formatMessage({ id: 'filter' })}
        rowClassName="col-check"
        onRow={record => ({
          onClick: this.handleSelectAppVersion.bind(this, record),
        })}
        onChange={this.tableChange}
        columns={column}
        rowKey={record => record.id}
        dataSource={appVersionList}
        pagination={appVersionPagination}
      />
    );
  };

  /**
   * 初始化选择数据
   */
  // handleSelectData = () => {
  //   if (this.props.app) {
  //     if (this.props.isMarket) {
  //       const app = this.props.app;
  //       app.appId = app.id;
  //     }
  //     this.setState({ app: this.props.app, isMarket: this.props.isMarket });
  //   }
  // };


  /**
   * 清空搜索框数据
   */
  // clearInputValue = (key) => {
  //   const { projectId, activeKey } = this.state;
  //   const keys = key || activeKey;
  //   SelectAppStore.setSearchValue('');
  //   if (keys === '1') {
  //     SelectAppStore.loadData({
  //       projectId,
  //       page: 0,
  //       size: SelectAppStore.localPageInfo.pageSize,
  //     });
  //   } else {
  //     SelectAppStore.loadApps({
  //       projectId,
  //       page: 0,
  //       size: SelectAppStore.storePageInfo.pageSize,
  //     });
  //   }
  // };

  /**
   * 点击选择数据
   * @param record
   */
  handleSelectApp = (id) => {
    this.loadAppVersions(id);
    const { app, appList } = CreateAutoTestStore;
    CreateAutoTestStore.setApp(_.find(appList, { id }));
  };

  handleSelectAppVersion = (record) => {
    CreateAutoTestStore.setAppVersion(record);
  }

  /**
   * table 改变的函数
   * @param pagination 分页
   * @param filters 过滤
   * @param sorter 排序
   */
  tableChange = (pagination, filters, sorter, paras) => {
    // console.log(filters, sorter, paras);
    const { app } = CreateAutoTestStore;

    this.loadAppVersions(app.id, pagination, paras.length > 0 ? { version: paras } : filters);
    // const menu = AppState.currentMenuType;
    // const organizationId = menu.id;
    // const sort = { field: 'id', order: 'desc' };
    // if (sorter.column) {
    //   sort.field = sorter.field || sorter.columnKey;
    //   // sort = sorter;
    //   if (sorter.order === 'ascend') {
    //     sort.order = 'asc';
    //   } else if (sorter.order === 'descend') {
    //     sort.order = 'desc';
    //   }
    // }
    // let searchParam = {};
    // const page = pagination.current - 1;
    // if (Object.keys(filters).length) {
    //   searchParam = filters;
    // }
    // const postData = {
    //   searchParam,
    //   param: paras.toString(),
    // };

    // SelectAppStore.loadData({
    //   projectId: organizationId,
    //   sort,
    //   postData,
    //   page,
    //   size: pagination.pageSize,
    // });
  };


  /**
   * 确定选择数据
   */
  handleOk = () => {
    const { handleOk, intl } = this.props;
    const { app, appVersion } = CreateAutoTestStore;
    if (app.id || appVersion.id) {
      handleOk();
    } else {
      Choerodon.prompt('未选择应用版本');
    }
  };

  render() {
    const { intl: { formatMessage }, show, handleCancel } = this.props;
    const { loading } = this.state;
    const { app, appList } = CreateAutoTestStore;
    const projectName = AppState.currentMenuType.name;
    const appOptions = appList.map(a => <Option value={a.id} key={a.id}>{a.name}</Option>);
    return (
      <SideBar
        title={<FormattedMessage id="autoteststep_one_app" />}
        visible={show}
        onOk={this.handleOk}
        okText={formatMessage({ id: 'ok' })}
        cancelText={formatMessage({ id: 'cancel' })}
        onCancel={handleCancel}
      >
        <Content className="c7ntest-deployApp-sidebar sidebar-content" code="autotest.sidebar" value={projectName}>
          <Spin spinning={loading}>
            <Select
              style={{ width: 200, marginBottom: 30 }}
              label="应用名称"
              onChange={this.handleSelectApp}
              value={app.id}
              filter
              filterOption={false}
              onFilterChange={(value) => { this.loadApps(value); }}
            >
              {appOptions}
            </Select>
            <div>
              {this.getProjectTable()}
            </div>
          </Spin>
        </Content>
      </SideBar>
);
  }
}

export default withRouter(injectIntl(SelectAppAndVersion));
