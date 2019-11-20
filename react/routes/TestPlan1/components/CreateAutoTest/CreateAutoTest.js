/* eslint-disable react-hooks/exhaustive-deps */
import React, {
  Component, Fragment, useState, useEffect, useRef,
} from 'react';
import { observer } from 'mobx-react-lite';
import { injectIntl } from 'react-intl';
import { Steps, Modal, Button } from 'choerodon-ui'; 
import { Choerodon } from '@choerodon/boot';
import './CreateAutoTest.less';
import CreateAutoTestStore from '../../stores/CreateAutoTestStore';
import { SelectVariable, ModifyConfig, ConfirmInfo } from './components';

const { Step } = Steps;
const prefixCls = 'c7ntest-testPlanHome-createAutoTestModal-children';
export default injectIntl(observer((props) => {
  const { intl, modal } = props;   
  const { formatMessage } = intl;
  const confirmInfoRef = useRef();
  const [currentStep, setCurrentStep] = useState(0);
  const steps = [
    {
      title: '选择测试实例',
      content: <SelectVariable />,
    },
    {
      title: '修改配置信息',
      content: <ModifyConfig />,
    },
    {
      title: '确认信息并执行',
      content: <ConfirmInfo confirmInfoRef={confirmInfoRef} />,
    },
  ];

  const handleDeploy = () => {
    console.log(confirmInfoRef);
    confirmInfoRef.current.handleDeploy();
  };

  useEffect(() => {
    const {
      app, env, appVersion, loading,
    } = CreateAutoTestStore;
    const data = CreateAutoTestStore.getNewConfigValue;

    const handleNextStep = () => {
      const nextStep = currentStep + 1;
      setCurrentStep(nextStep);
    };
  
    const handlePreStep = () => {
      setCurrentStep(currentStep - 1);
    };
    modal.update({
      footer: (okBtn, cancelBtn) => (
        <div className="c7ntest-testPlanHome-createAutoTestModal-footer">
          {
            currentStep === 2
            && <Button type="primary" funcType="raised" onClick={handleDeploy}>{formatMessage({ id: 'autotestbtn_autotest' })}</Button>
          }
          {
              currentStep > 0
              && (
                <Button funcType="raised" style={{ color: '#3f51b5' }} onClick={handlePreStep}>
                  {formatMessage({ id: 'previous' })}
                </Button>
              )
            }
          {
            currentStep < 2
            && <Button disabled={currentStep === 0 ? (!app.id || !appVersion.id || !env.id) : !data || (data.errorLines && data.errorLines.length > 0)} type="primary" funcType="raised" onClick={handleNextStep}>{formatMessage({ id: 'next' })}</Button>
            }
          <span className="c7ntest-testPlanHome-createAutoTestModal-footer-cancelBtn" role="none" onClick={CreateAutoTestStore.clearTestInfo}>
            {cancelBtn}
          </span>
        </div>
      ),
    });
  }, [currentStep, CreateAutoTestStore.appVersion, CreateAutoTestStore.app, CreateAutoTestStore.env, CreateAutoTestStore.newConfigValue]);

  return (
    <div className={`${prefixCls}`}>
      <Steps current={currentStep}>
        {steps.map(item => <Step key={item.title} title={item.title} />)}
      </Steps>
      <div className={`${prefixCls}-content`}>{steps[currentStep].content}</div>
    </div>
  );
}));
