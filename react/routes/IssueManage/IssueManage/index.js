import React, { useState } from 'react';
import { Tabs } from 'choerodon-ui';
import { find } from 'lodash';
import IssueManage from './IssueManage';

let tabs = [{
  name: '功能测试',
  key: 'functional',
  component: IssueManage,
}];

export function inject({ tabs: otherTab }) {
  tabs = [...tabs, ...otherTab];
}

const { TabPane } = Tabs;
const Test = (props) => {
  const [activeKey, setActiveKey] = useState(tabs[0].key);
  const Component = find(tabs, { key: activeKey }).component;
  const tabComponent = (
    <Tabs activeKey={activeKey} onChange={setActiveKey} className="c7ntest-IssueTree-tab">
      {tabs.map(tab => <TabPane key={tab.key} tab={tab.name} />)}
    </Tabs>
  );
  return (
    <>
      {Component && <Component {...props} tab={tabComponent} tabs={tabs} />}
    </>
  );
};

export default Test;
