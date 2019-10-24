import React, { Component } from 'react';
import { Choerodon } from '@choerodon/boot';
import { DashBoardNavBar } from '@choerodon/boot';

import './index.scss';

export default class Announcement extends Component {
  render() {
    return (
      <div className="c7ntest-dashboard-Test">
        <ul>
          <li>           
            <a target="choerodon" href="http://choerodon.io/zh/docs/user-guide/test-management/case-management/">
              {Choerodon.getMessage('测试用例', 'issue manage')}
            </a>
          </li>
          <li>           
            <a target="choerodon" href="http://choerodon.io/zh/docs/user-guide/test-management/test-plan/">
              {Choerodon.getMessage('测试计划', 'test plan')}
            </a>
          </li>
          <li>           
            <a target="choerodon" href="http://choerodon.io/zh/docs/user-guide/test-management/test-execute/">
              {Choerodon.getMessage('测试执行', 'test execute')}
            </a>
          </li>
          <li>           
            <a target="choerodon" href="http://choerodon.io/zh/docs/user-guide/test-management/test-report/">
              {Choerodon.getMessage('测试报告', 'report')}
            </a>
          </li>
        </ul>
        <DashBoardNavBar>
          <a target="choerodon" href="http://choerodon.io/zh/docs/user-guide/test-management/">{Choerodon.getMessage('查看测试管理文档', 'review test manage document')}</a>
        </DashBoardNavBar>
      </div>
    );
  }
}
