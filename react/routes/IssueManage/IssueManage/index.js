import React, { useEffect, useState } from 'react';
import { Tabs } from 'choerodon-ui';
import { Permission } from '@choerodon/master';
import { mount, has } from '@choerodon/inject';
import { useTabActiveKey } from '@choerodon/components';
import { LoadingContext, LoadingProvider } from '@choerodon/agile/lib/components/Loading';
import { isEqual } from 'lodash';
import IssueManage from './IssueManage';
import useFormatMessage from '@/hooks/useFormatMessage';

const { TabPane } = Tabs;
const code = 'test-pro:api-test';
const IssueManageTabKey = 'test-case';
const ApiTestTabKey = 'api-test-case';
const Test = (props) => {
  const formatMessage = useFormatMessage('test.caseLibrary');
  const [stableActiveKey, setStableActiveKey] = useState();
  const [activeKey, setActiveKey] = useTabActiveKey(IssueManageTabKey);
  const tabComponent = (
    <Permission service={['choerodon.code.project.test.manager.ps.api.default']}>
      {(hasPermission) => (
        hasPermission && (
          <Tabs
            activeKey={activeKey}
            onChange={setActiveKey}
            className="c7ntest-IssueTree-tab"
          >
            <TabPane key={IssueManageTabKey} tab={formatMessage({ id: 'function' })} />
            <TabPane key={ApiTestTabKey} tab={formatMessage({ id: 'api' })} />
          </Tabs>
        )
      )}
    </Permission>
  );
  useEffect(() => {
    //  避免重复渲染
    setStableActiveKey((old) => {
      if (isEqual(old, activeKey)) {
        return old;
      }
      return activeKey;
    });
  }, [activeKey]);
  return (
    <>
      {stableActiveKey === 'test-case' && (
        <LoadingProvider loadId="IssueManage" style={{ height: '100%' }}>
          <LoadingContext.Consumer>
            {({ change }) => <IssueManage {...props} tab={tabComponent} hasExtraTab={has(code)} change={change} />}
          </LoadingContext.Consumer>
        </LoadingProvider>
      )}
      {stableActiveKey === ApiTestTabKey && mount(code, {
        ...props,
        tab: tabComponent,
      })}
    </>
  );
};

export default Test;
