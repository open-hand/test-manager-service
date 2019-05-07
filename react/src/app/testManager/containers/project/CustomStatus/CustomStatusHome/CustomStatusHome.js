/*
 * @Author: LainCarl 
 * @Date: 2019-01-25 11:36:04 
 * @Last Modified by: LainCarl
 * @Last Modified time: 2019-02-26 15:30:47
 * @Feature: 用户自定义状态展示组件
 */

import React from 'react';
import PropTypes from 'prop-types';
import {
  Tabs, Button, Spin, 
} from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import { Page, Header, Content } from 'choerodon-front-boot';
import { CreateStatus, EditStatus, StatusTable } from '../components';
import { getProjectName } from '../../../../common/utils';

const TabPane = Tabs.TabPane;
const defaultProps = {

};

const propTypes = {
  loading: PropTypes.bool.isRequired,
  createVisible: PropTypes.bool.isRequired,
  editVisible: PropTypes.bool.isRequired,
  statusList: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
  CurrentEditStatus: PropTypes.shape({}).isRequired,
  EditStatusLoading: PropTypes.bool.isRequired,
  CreateStatusLoading: PropTypes.bool.isRequired,
  onRefreshClick: PropTypes.func.isRequired,
  onTabChange: PropTypes.func.isRequired,
  onShowCreateClick: PropTypes.func.isRequired,
  onEditStatusClick: PropTypes.func.isRequired,
  onDeleteOk: PropTypes.func.isRequired,
  onCheckStatusRepeat: PropTypes.func.isRequired,
  onCreateStatusCancel: PropTypes.func.isRequired,
  onEditStatusCancel: PropTypes.func.isRequired,
  onCreateStatusSubmit: PropTypes.func.isRequired,
  onEditStatusSubmit: PropTypes.func.isRequired,
};
const CustomStatusHome = ({
  loading,
  createVisible,
  editVisible,
  statusList,
  statusType,
  CurrentEditStatus,
  EditStatusLoading,
  CreateStatusLoading,
  onRefreshClick,
  onTabChange,
  onShowCreateClick,
  onEditStatusClick,
  onDeleteOk,
  onCheckStatusRepeat,
  onCreateStatusCancel,
  onEditStatusCancel,
  onCreateStatusSubmit,
  onEditStatusSubmit,
}) => (
  <div>
    <CreateStatus
      visible={createVisible}
      loading={CreateStatusLoading}
      onCancel={onCreateStatusCancel}
      onCheckStatusRepeat={onCheckStatusRepeat}
      onSubmit={onCreateStatusSubmit}
    />
    <EditStatus
      visible={editVisible}
      loading={EditStatusLoading}
      initValue={CurrentEditStatus}
      onCancel={onEditStatusCancel}
      onCheckStatusRepeat={onCheckStatusRepeat}
      onSubmit={onEditStatusSubmit}
    />
    <Page>
      <Header title={<FormattedMessage id="status_title" />}>
        <Button icon="playlist_add" onClick={onShowCreateClick}>
          <FormattedMessage id="status_create" />
        </Button>
        <Button icon="autorenew" onClick={onRefreshClick}>
          <FormattedMessage id="refresh" />
        </Button>
      </Header>
      <Spin spinning={loading}>
        <Content
          title={<FormattedMessage id="status_custom_home_title" values={{ name: getProjectName() }} />}
          description={<FormattedMessage id="status_custom_home_description" />}
          link="http://choerodon.io/zh/docs/user-guide/test-management/setting/status/"
        >
          <Tabs activeKey={statusType} onChange={onTabChange}>
            <TabPane tab={<FormattedMessage id="status_executeStatus" />} key="CYCLE_CASE">
              <StatusTable          
                dataSource={statusList}
                onDeleteOk={onDeleteOk}
                onEditStatusClick={onEditStatusClick}
              />
            </TabPane>
            <TabPane tab={<FormattedMessage id="status_steptatus" />} key="CASE_STEP">
              <StatusTable              
                dataSource={statusList}
                onDeleteOk={onDeleteOk}
                onEditStatusClick={onEditStatusClick}
              />
            </TabPane>
          </Tabs>
        </Content>
      </Spin>
    </Page>
  </div>
);

CustomStatusHome.propTypes = propTypes;
CustomStatusHome.defaultProps = defaultProps;

export default CustomStatusHome;
