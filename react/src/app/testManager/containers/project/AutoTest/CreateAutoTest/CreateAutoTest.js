import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { injectIntl, FormattedMessage } from 'react-intl';
import { withRouter } from 'react-router-dom';
import { Steps } from 'choerodon-ui';
import {
  Content, Header, Page, stores, 
} from 'choerodon-front-boot';
import _ from 'lodash';
import './CreateAutoTest.scss';
import { commonLink } from '../../../../common/utils';
import CreateAutoTestStore from '../../../../store/project/AutoTest/CreateAutoTestStore';
import { SelectVariable, ModifyConfig, ConfirmInfo } from './components';

const Step = Steps.Step;
const { AppState } = stores;

@observer
class CreateAutoTest extends Component {
  render() {
    const { intl } = this.props;
    const { formatMessage } = intl; 
    const projectName = AppState.currentMenuType.name;
    const { currentStep } = CreateAutoTestStore;
    return (
      <Page
        className="c7ntest-region c7ntest-deployApp"
      >
        <Header
          title={<FormattedMessage id="autotest_create_header_title" />}
          backPath={commonLink('/AutoTest/list')}
        />
        <Content className="c7ntest-deployApp-wrapper" code="autotest" values={{ name: projectName }}>
          <div className="deployApp-card">
            <Steps current={currentStep - 1}>
              <Step
                title={<span style={{ color: currentStep === 1 ? '#3F51B5' : '', fontSize: 14 }}>{formatMessage({ id: 'autoteststep_one_title' })}</span>}
              />
              <Step                
                title={<span style={{ color: currentStep === 2 ? '#3F51B5' : '', fontSize: 14 }}>{formatMessage({ id: 'autoteststep_two_title' })}</span>}
              />
              <Step               
                title={<span style={{ color: currentStep === 3 ? '#3F51B5' : '', fontSize: 14 }}>{formatMessage({ id: 'autoteststep_three_title' })}</span>}
              />
            </Steps>
            <div className="deployApp-card-content">
              {currentStep === 1 && <SelectVariable />}
              {currentStep === 2 && <ModifyConfig />}
              {currentStep === 3 && <ConfirmInfo />}
            </div>
          </div>

        </Content>
      </Page>
    );
  }
}

export default withRouter(injectIntl(CreateAutoTest));
