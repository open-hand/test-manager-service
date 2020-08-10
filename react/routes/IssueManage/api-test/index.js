import React from 'react';
import {
  Page, Header, Content, Breadcrumb,
} from '@choerodon/boot';
import { Button } from 'choerodon-ui/pro';

const ApiTest = ({ tab }) => (
  <Page>
    <Header>
      <Button icon="playlist_add">test</Button>
    </Header>
    <Breadcrumb />
    <Content>
      <div>
        {tab}
      </div>
    </Content>
  </Page>
);
const ApiTestInject = (inject) => {
  inject({
    tabs: [{
      name: 'API测试',
      key: 'api',
      component: ApiTest,
    }],
  });
};

export default ApiTestInject;
