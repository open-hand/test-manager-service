import React, { Component } from 'react';
import { observer } from 'mobx-react-lite';
import _ from 'lodash';
import { FormattedMessage } from 'react-intl';
import {
  Page, Header, Content, Breadcrumb, Choerodon,
} from '@choerodon/boot';
import { Button, Icon } from 'choerodon-ui';
import { Modal } from 'choerodon-ui/pro';
import CreateAutoTest from '../components/CreateAutoTest'; 
import './TestPlanHome.less';

const createAutoTestModalKey = Modal.key();

export default observer(() => {
  const handleCreateAutoTest = () => {
    Modal.open({
      key: createAutoTestModalKey,
      drawer: true,
      title: '自动化测试',
      children: (
        <CreateAutoTest />
      ),
      style: { width: '10.90rem' },
      className: 'c7ntest-testPlanHome-createAutoTestModal',
    });
  };

  return (
    <Page className="c7ntest-testPlanHome">
      <Header
        title={<FormattedMessage id="testPlan_name" />}
      >
        <Button icon="playlist_add icon">
          <FormattedMessage id="testPlan_createPlan" />
        </Button>
        <Button icon="mode_edit">
          <FormattedMessage id="testPlan_editPlan" />
        </Button>
        <Button icon="play_circle_filled">
          <FormattedMessage id="testPlan_manualTest" />
        </Button>
        <Button icon="auto_test" onClick={handleCreateAutoTest}>
          <FormattedMessage id="testPlan_autoTest" />
        </Button>
      </Header>
      <Breadcrumb />
      <Content style={{ display: 'flex', padding: '0', borderTop: '0.01rem solid rgba(0,0,0,0.12)' }} />
    </Page>
  );
});
