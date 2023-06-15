import React from 'react';
import {
  Choerodon,
  Page, Header, Content, Breadcrumb,
} from '@choerodon/boot';

import moment from 'moment';
import {
  Icon, Button, Table, Select, Menu, Dropdown,
} from 'choerodon-ui';
import { HeaderButtons } from '@choerodon/master';
import { FormattedMessage } from 'react-intl';
import _ from 'lodash';
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/base16-dark.css';
import { User } from '../../../components';
import {
  PODSTATUS, TESTRESULT, PodStatus, TestResult,
} from './AutoTestTags';
import { ContainerLog } from './components';
import { humanizeDuration } from '../../../common/utils';
import CreateAutoTest from '../CreateAutoTest';
import './AutoTestList.less';
import useFormatMessage from '@/hooks/useFormatMessage';

const { Option } = Select;
const { Item: MenuItem } = Menu;
const AutoTestList = ({
  loading,
  appList,
  selectLoading,
  autoRefresh,
  currentApp,
  historyList,
  envList,
  pagination,
  toCreateAutoTest,
  onRefreshClick,
  onItemClick,
  onAppChange,
  onFilterChange,
  onTableChange,
  onSaveLogRef,
  onAutoRefreshChange,
}) => {
  const formatMessage = useFormatMessage('test.autoTest');
  const commonFormatMessage = useFormatMessage('test.common');

  const getMenu = (record) => (
    <Menu onClick={({ item, key, keyPath }) => { onItemClick(record, { item, key, keyPath }); }} style={{ margin: '10px 0 0 28px' }}>
      <MenuItem key="log" disabled={record.testAppInstanceVO.podStatus === 0 || (record.testAppInstanceVO.podStatus !== 1 && !record.testAppInstanceVO.logId)}>
        {formatMessage({ id: 'view.log' })}
      </MenuItem>
      <MenuItem key="retry">
        {formatMessage({ id: 'retry.execute' })}
      </MenuItem>
      <MenuItem key="report" disabled={!record.resultId}>
        {formatMessage({ id: 'test.report' })}
      </MenuItem>
    </Menu>
  );

  const appOptions = appList.map((app) => <Option value={app.id}>{app.name}</Option>);
  const ENVS = envList.map((env) => ({ text: env.name, value: env.id.toString() }));
  const columns = [{
    title: formatMessage({ id: 'run.status' }),
    dataIndex: 'podStatus',
    key: 'podStatus',
    filters: PODSTATUS.map((item) => ({ ...item, text: formatMessage({ id: item.text }) })),
    render: (status, record) => {
      const { testAppInstanceVO } = record;
      const { podStatus } = testAppInstanceVO || {};
      return <PodStatus status={podStatus} />;
    },
  },
  {
    title: '',
    dataIndex: 'action',
    key: 'action',
    render: (action, record) => (
      <div style={{ display: 'flex' }}>
        <div className="c7ntest-flex-space" />
        <Dropdown overlay={getMenu(record)} trigger={['click']}>
          <Button shape="circle" icon="more_vert" style={{ marginRight: -5 }} />
        </Dropdown>
      </div>
    ),
  },
  {
    title: formatMessage({ id: 'environment' }),
    dataIndex: 'envId',
    key: 'envId',
    filters: ENVS,
    render: (env, record) => {
      const { testAppInstanceVO } = record;
      const { envId } = testAppInstanceVO || {};
      const target = _.find(envList, { id: envId });
      return <span>{target && target.name}</span>;
    },
  },
  {
    title: formatMessage({ id: 'executive' }),
    dataIndex: 'createUser',
    key: 'createUser',
    render: (createUser) => <User user={createUser} />,
  },
  {
    title: formatMessage({ id: 'test.framework' }),
    dataIndex: 'framework',
    key: 'framework',
    filters: [],
  },
  {
    title: formatMessage({ id: 'app.version' }),
    dataIndex: 'version',
    key: 'version',
    filters: [],
    render: (version, record) => {
      const { testAppInstanceVO } = record;
      const { appVersionName } = testAppInstanceVO || {};
      return <span>{appVersionName}</span>;
    },
  },
  {
    title: formatMessage({ id: 'during' }),
    dataIndex: 'during',
    key: 'during',
    render: (during, record) => {
      const { creationDate, lastUpdateDate } = record;
      const diff = moment(lastUpdateDate).diff(moment(creationDate));
      return creationDate && lastUpdateDate
        ? humanizeDuration(diff)
        : null;
    },
  },
  {
    title: formatMessage({ id: 'execute.time' }),
    dataIndex: 'creationDate',
    key: 'creationDate',
  },
  {
    title: formatMessage({ id: 'result' }),
    dataIndex: 'testStatus',
    key: 'testStatus',
    filters: TESTRESULT.map((item) => ({ ...item, text: formatMessage({ id: item.text }) })),
    render: (testStatus) => <TestResult result={testStatus} />,
  }];
  return (
    <Page
      className="c7ntest-AutoTestList"
    >
      <Header title={formatMessage({ id: 'route' })}>
        <HeaderButtons items={[{
          name: formatMessage({ id: 'add' }),
          icon: 'playlist_add',
          handler: toCreateAutoTest,
          display: true,
        }]}
        />
      </Header>
      <Breadcrumb />
      <Content>
        <Select
          label={formatMessage({ id: 'chose.app' })}
          style={{ width: 512, marginBottom: 20 }}
          filter
          value={currentApp}
          loading={selectLoading}
          onChange={onAppChange}
          onFilterChange={onFilterChange}
        >
          {appOptions}
        </Select>
        <Table filterBarPlaceholder={commonFormatMessage({ id: 'filter' })} loading={loading} columns={columns} dataSource={historyList} pagination={pagination} onChange={onTableChange} />
        <ContainerLog
          ref={onSaveLogRef('ContainerLog')}
        />
        <CreateAutoTest />
      </Content>
    </Page>
  );
};

export default AutoTestList;
