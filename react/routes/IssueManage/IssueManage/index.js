import React from 'react';
import { Tabs } from 'choerodon-ui';
import { Permission } from '@choerodon/master';
import { mount, has } from '@choerodon/inject';
import { useTabActiveKey } from '@choerodon/components';
import IssueManage from './IssueManage';

const { TabPane } = Tabs;
const code = 'test-pro:api-test';

const IssueManageTabKey = 'test-case';
const ApiTestTabKey = 'api-test-case';
const Test = (props) => {
  const [activeKey, setActiveKey] = useTabActiveKey(IssueManageTabKey);
  const tabComponent = (
    <Permission service={['choerodon.code.project.test.manager.ps.api.default']}>
      {(hasPermission) => (
        hasPermission && (
          <Tabs activeKey={activeKey} onChange={setActiveKey} className="c7ntest-IssueTree-tab">
            <TabPane key={IssueManageTabKey} tab="功能测试" />
            <TabPane key={ApiTestTabKey} tab="API测试" />
          </Tabs>
        )
      )}
    </Permission>
  );
  return (
    <>
      {activeKey === 'test-case' && <IssueManage {...props} tab={tabComponent} hasExtraTab={has(code)} />}
      {activeKey === ApiTestTabKey && mount(code, {
        ...props,
        tab: tabComponent,
      })}
    </>
  );
};

export default Test;
