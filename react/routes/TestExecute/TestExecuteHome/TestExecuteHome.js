
import React from 'react';
import {
  Page, Header, Content, Breadcrumb,
} from '@choerodon/boot';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import {
  Button, Icon, Spin,
} from 'choerodon-ui';
import _ from 'lodash';
import { FormattedMessage } from 'react-intl';
import { ShowCycleData } from './components';
import { NoCycle } from '../../../components';
import { TestExecuteTreeToggle } from './components';
import { TestExecuteTable } from './components';
import './TestExecuteHome.scss';

const propTypes = {
  loading: PropTypes.bool.isRequired,
  treeSearchValue: PropTypes.string.isRequired,
  testList: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
  tableLoading: PropTypes.bool.isRequired,
  prioritys: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
  autoExpandParent: PropTypes.bool.isRequired,
  statusList: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
  treeData: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
  expandedKeys: PropTypes.arrayOf(PropTypes.string).isRequired,
  selectedKeys: PropTypes.arrayOf(PropTypes.string).isRequired,
  currentCycle: PropTypes.shape({}).isRequired,
  leftVisible: PropTypes.bool.isRequired,
  treeAssignedTo: PropTypes.number.isRequired,
  executePagination: PropTypes.shape({}).isRequired,
  onRefreshClick: PropTypes.func.isRequired,
  quickPass: PropTypes.func.isRequired,
  quickFail: PropTypes.func.isRequired,
  onTreeAssignedToChange: PropTypes.func.isRequired,
  onTreeNodeExpand: PropTypes.func.isRequired,
  onTreeNodeSelect: PropTypes.func.isRequired,
  filterCycle: PropTypes.func.isRequired,
  onExecuteByChange: PropTypes.func.isRequired,
  onAssignedToChange: PropTypes.func.isRequired,
  onExecuteTableChange: PropTypes.func.isRequired,
  onTableRowClick: PropTypes.func.isRequired,
};
const TestExecuteHome = ({
  loading,
  treeSearchValue,
  testList,
  tableLoading,
  prioritys,
  autoExpandParent,
  statusList,
  treeData,
  expandedKeys,
  selectedKeys,
  currentCycle,
  leftVisible,
  treeAssignedTo,
  executePagination,
  onRefreshClick,
  quickPass,
  quickFail,
  onTreeAssignedToChange,
  onTreeNodeExpand,
  onTreeNodeSelect,
  filterCycle,
  onExecuteByChange,
  onAssignedToChange,
  onExecuteTableChange,
  onTableRowClick,
}) => {
  const { cycleId } = currentCycle;
  return (
    <Page className="c7ntest-TestExecuteHome">
      {/* <Header title={<FormattedMessage id="cycle_title" />}>
        <Button onClick={onRefreshClick}>
          <Icon type="autorenew icon" />
          <span>
            <FormattedMessage id="refresh" />
          </span>
        </Button>
      </Header> */}
      <Breadcrumb title="" />
      <div className="breadcrumb-border" />
      <Content
        style={{
          padding: 0, display: 'flex',
        }}
      >
        <Spin spinning={loading}>
          <div className="c7ntest-TestExecuteHome-container">
            <TestExecuteTreeToggle
              loading={loading}
              leftVisible={leftVisible}
              filterCycle={filterCycle}
              onTreeAssignedToChange={onTreeAssignedToChange}
              treeAssignedTo={treeAssignedTo}
              treeData={treeData}
              treeSearchValue={treeSearchValue}
              selectedKeys={selectedKeys}
              expandedKeys={expandedKeys}
              onTreeNodeExpand={onTreeNodeExpand}
              onTreeNodeSelect={onTreeNodeSelect}
              autoExpandParent={autoExpandParent}
            />
            {cycleId ? (
              <div className="c7ntest-TestExecuteHome-right">
                <ShowCycleData data={currentCycle} />
                <TestExecuteTable
                  prioritys={prioritys}
                  statusList={statusList}
                  currentCycle={currentCycle}
                  loading={tableLoading}
                  onExecuteByChange={onExecuteByChange}
                  onAssignedToChange={onAssignedToChange}
                  treeAssignedTo={treeAssignedTo}
                  dataSource={testList}
                  quickPass={quickPass}
                  quickFail={quickFail}
                  onTableRowClick={onTableRowClick}
                  onTableChange={onExecuteTableChange}
                  pagination={executePagination}
                />
              </div>
            ) : !loading && <NoCycle />}
          </div>
        </Spin>
      </Content>
    </Page>
  );
};
TestExecuteHome.propTypes = propTypes;
export default observer(TestExecuteHome);
