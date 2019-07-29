import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { inject } from 'mobx-react';
import { asyncLocaleProvider, asyncRouter, nomatch } from '@choerodon/boot';
import 'moment/locale/zh-cn';
import 'moment/locale/en-nz';
import moment from 'moment';
import './src/app/testManager/assets/index.scss';


const TestExecuteIndex = asyncRouter(() => import('./routes/TestExecute'));
const TestPlanIndex = asyncRouter(() => import('./routes/TestPlan'));
const CustomStatusIndex = asyncRouter(() => import('./routes/CustomStatus'));
const ReportIndex = asyncRouter(() => import('./routes/Report'));
const SummaryIndex = asyncRouter(() => import('./routes/Summary'));
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
    
    const IntlProviderAsync = asyncLocaleProvider(langauge, () => import(`./src/app/testManager/locale/${langauge}`));
    return (
      <div>
        <IntlProviderAsync>
          <Switch>
            <Route path={`${match.url}/summary`} component={SummaryIndex} />
            <Route path={`${match.url}/IssueManage`} component={IssueManageIndex} />
            <Route path={`${match.url}/TestExecute`} component={TestExecuteIndex} />
            <Route path={`${match.url}/TestPlan`} component={TestPlanIndex} />
            <Route path={`${match.url}/report`} component={ReportIndex} />
            <Route path={`${match.url}/status`} component={CustomStatusIndex} />
            <Route path={`${match.url}/AutoTest`} component={AutoTestIndex} />
            <Route path="*" component={nomatch} />
          </Switch>        
        </IntlProviderAsync>
      </div>
      
    );
  }
}

export default TestManagerIndex;
