import React, { useState } from 'react';
import { Tabs } from 'choerodon-ui';
import { Permission } from '@choerodon/master';
import { mount, has } from '@choerodon/inject';
import IssueManage from './IssueManage';

const { TabPane } = Tabs;
const code = 'test-pro:api-test';
const Test = (props) => {
  const [activeKey, setActiveKey] = useState('functional');
  const tabComponent = (
    <Permission service={['choerodon.code.project.test.manager.ps.api.default']}>
      {(hasPermission) => (
        <Tabs activeKey={activeKey} onChange={setActiveKey} className="c7ntest-IssueTree-tab">
          <TabPane key="functional" tab="功能测试" />
          {hasPermission && <TabPane key="api" tab="api测试" />}
        </Tabs>
      )}

    </Permission>
  );
  return (
    <>
      {activeKey === 'functional' && <IssueManage {...props} tab={tabComponent} hasExtraTab={has(code)} />}
      {activeKey === 'api' && mount(code, {
        ...props,
        tab: tabComponent,
      })}
    </>
  );
};

export default Test;
