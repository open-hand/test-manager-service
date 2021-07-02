import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { ModalContainer } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { asyncLocaleProvider, nomatch } from '@choerodon/boot';
import { localPageCacheStore } from '@choerodon/agile/lib/stores/common/LocalPageCacheStore';
import 'moment/locale/zh-cn';
import 'moment/locale/en-nz';
import moment from 'moment';
import RunWhenProjectChange from './common/RunWhenProjectChange';
import './index.less';

const TestPlanIndex = React.lazy(() => import('./routes/TestPlan'));
const CustomStatusIndex = React.lazy(() => import('./routes/CustomStatus'));
const ReportIndex = React.lazy(() => import('./routes/Report'));
const IssueManageIndex = React.lazy(() => import('./routes/IssueManage'));
const AutoTestIndex = React.lazy(() => import('./routes/AutoTest'));
const Priority = React.lazy(() => import('./routes/priority'));

@inject('AppState')
class TestManagerIndex extends React.Component {
  componentWillUnmount() {
    RunWhenProjectChange(localPageCacheStore.clear);
  }

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
          <>
            <Switch>
              <Route path={`${match.url}/IssueManage`} component={IssueManageIndex} />
              <Route path={`${match.url}/TestPlan`} component={TestPlanIndex} />
              <Route path={`${match.url}/report`} component={ReportIndex} />
              <Route path={`${match.url}/status`} component={CustomStatusIndex} />
              <Route path={`${match.url}/AutoTest`} component={AutoTestIndex} />
              <Route path={`${match.url}/priority`} component={Priority} />
              <Route path="*" component={nomatch} />
            </Switch>
            <ModalContainer />
          </>
        </IntlProviderAsync>
      </div>
    );
  }
}

export default TestManagerIndex;
