import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { ModalContainer } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { asyncLocaleProvider, asyncRouter, nomatch } from '@choerodon/boot';
import 'moment/locale/zh-cn';
import 'moment/locale/en-nz';
import moment from 'moment';
import './index.scss';

const TestExecuteIndex = asyncRouter(() => import('./routes/TestExecute'));
const TestPlanIndex = asyncRouter(() => import('./routes/TestPlan'));
const CustomStatusIndex = asyncRouter(() => import('./routes/CustomStatus'));
const ReportIndex = asyncRouter(() => import('./routes/Report'));
const IssueManageIndex = asyncRouter(() => import('./routes/IssueManage'));
const AutoTestIndex = asyncRouter(() => import('./routes/AutoTest'));
@inject('AppState')
class TestManagerIndex extends React.Component {
  render() {
    const { match, AppState } = this.props;
    const langauge = AppState.currentLanguage;
    // const langauge = 'en_US';
    if (langauge === 'zh_CN') {
      moment.locale('zh-cn');
    }

    const IntlProviderAsync = asyncLocaleProvider(langauge, () => import(`./locale/${langauge}`));
    return (
      <div className="testManager">
        <IntlProviderAsync>
          <React.Fragment>
            <Switch>         
              <Route path={`${match.url}/IssueManage`} component={IssueManageIndex} />
              <Route path={`${match.url}/TestExecute`} component={TestExecuteIndex} />
              <Route path={`${match.url}/TestPlan`} component={TestPlanIndex} />     
              <Route path={`${match.url}/report`} component={ReportIndex} />
              <Route path={`${match.url}/status`} component={CustomStatusIndex} />
              <Route path={`${match.url}/AutoTest`} component={AutoTestIndex} />
              <Route path="*" component={nomatch} />
            </Switch>
            <ModalContainer />
          </React.Fragment>
        </IntlProviderAsync>
      </div>
    );
  }
}

export default TestManagerIndex;
