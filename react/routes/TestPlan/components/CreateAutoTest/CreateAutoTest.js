import React, { Component, Fragment } from 'react';
import { observer } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { Steps, Modal, Button } from 'choerodon-ui';
import './CreateAutoTest.less';
import { SelectVariable, ModifyConfig, ConfirmInfo } from './components';

const { Step } = Steps;
const { Sidebar } = Modal;
@observer
class CreateAutoTest extends Component {
  getTitle = () => {
    const { createAutoTestStore } = this.props;
    const { currentStep } = createAutoTestStore;
    const titles = {
      1: '选择测试实例',
      2: '修改配置信息',
      3: '确认信息并执行',
    };
    return titles[currentStep];
  }

  handleDeploy=() => {
    this.ConfirmInfo.handleDeploy();
  }

  renderFooter=() => {
    const { createAutoTestStore } = this.props;
    const {
      currentStep, app, env, version, appVersion, loading,
    } = createAutoTestStore;
    const { intl } = this.props;   
    const { formatMessage } = intl;
    const data = createAutoTestStore.getNewConfigValue;
    switch (currentStep) {
      case 1: {
        return (
          <Fragment>
            <Button
              type="primary"
              funcType="raised"
              disabled={!app.id || !appVersion.id || !env.id}
              onClick={createAutoTestStore.nextStep}
            >
              {formatMessage({ id: 'next' })}
            </Button>
            <Button funcType="raised" className="c7ntest-autotest-clear" onClick={createAutoTestStore.clearTestInfo}>{formatMessage({ id: 'cancel' })}</Button>
          </Fragment>
        );
      }
      case 2: {
        return (
          <Fragment>
            <Button
              type="primary"
              funcType="raised"
              onClick={createAutoTestStore.nextStep}
              disabled={!data || (data.errorLines && data.errorLines.length > 0)}
            >
              {formatMessage({ id: 'next' })}
            </Button>
            <Button onClick={createAutoTestStore.preStep} funcType="raised">{formatMessage({ id: 'previous' })}</Button>
            <Button funcType="raised" className="c7ntest-autotest-clear" onClick={createAutoTestStore.clearTestInfo}>{formatMessage({ id: 'cancel' })}</Button>
          </Fragment>
        );
      }
      case 3: {
        return (
          <Fragment>
            <Button type="primary" funcType="raised" onClick={this.handleDeploy} loading={loading}>{formatMessage({ id: 'autotestbtn_autotest' })}</Button>
            <Button funcType="raised" onClick={createAutoTestStore.preStep}>{formatMessage({ id: 'previous' })}</Button>
            <Button funcType="raised" className="c7ntest-autotest-clear" onClick={createAutoTestStore.clearTestInfo}>{formatMessage({ id: 'cancel' })}</Button>
          </Fragment>
        );
      }
      default: {
        return null;
      }
    }
  }

  render() {
    const { intl } = this.props;
    const { formatMessage } = intl;
    const { createAutoTestStore } = this.props;
    const { currentStep, visible } = createAutoTestStore;
    return (
      <Sidebar title="自动化测试" className="c7ntest-region c7ntest-deployApp" visible={visible} footer={this.renderFooter()}>
        <Steps current={currentStep - 1}>
          <Step
            title={<span style={{ color: currentStep === 1 ? 'var(--primary-color)' : '', fontSize: 14 }}>{formatMessage({ id: 'autoteststep_one_title' })}</span>}
          />
          <Step
            title={<span style={{ color: currentStep === 2 ? 'var(--primary-color)' : '', fontSize: 14 }}>{formatMessage({ id: 'autoteststep_two_title' })}</span>}
          />
          <Step
            title={<span style={{ color: currentStep === 3 ? 'var(--primary-color)' : '', fontSize: 14 }}>{formatMessage({ id: 'autoteststep_three_title' })}</span>}
          />
        </Steps>
        <div>
          {currentStep === 1 && <SelectVariable createAutoTestStore={createAutoTestStore} />}
          {currentStep === 2 && <ModifyConfig createAutoTestStore={createAutoTestStore} />}
          {currentStep === 3 && <ConfirmInfo saveRef={(ref) => { this.ConfirmInfo = ref; }} createAutoTestStore={createAutoTestStore} />}
        </div>
      </Sidebar>
    );
  }
}

export default injectIntl(CreateAutoTest);
