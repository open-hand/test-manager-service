import React, { Component } from 'react';
import {
  Select, Button, Radio, Steps, Icon, Tooltip, Form,
} from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import { observer } from 'mobx-react';
import _ from 'lodash';
import { YamlEditor, SelectVersion } from '../../../../../components';
import CreateAutoTestStore from '../../../AutoTestStore/CreateAutoTestStore';
import { getAllEnvs } from '../../../../../api/AutoTestApi';
import SelectAppAndVersion from '../SelectAppAndVersion';

const { Option } = Select;
@injectIntl
@observer
class SelectVariable extends Component {
  state = {
    selectApp: false,
    // envs: [],
  }

  /**
   * 展开选择应用的弹框
   */
  showSideBar = () => {
    this.setState({ selectApp: true });
  };

  /**
   * 关闭弹框
   */
  handleCancel = () => {
    this.setState({ selectApp: false });
  };

  /**
   * 选择应用版本
   */
  handleOk = (selectdApp, selectedAppVersion) => {
    // console.log(`选择应用:${selectdApp}`, `选择应用版本:${selectedAppVersion}`);
    // CreateAutoTestStore.setApp(selectdApp);
    // CreateAutoTestStore.setAppVersion(selectedAppVersion);
    this.setState({ selectApp: false });
  }

  /**
   *选择目标版本
   *
   * @param {*} versionId
   * @memberof CreateAutoTest
   */
  handleVersionSelect=(versionId, other) => {
    const { children: versionName } = other.props;
    CreateAutoTestStore.setVersion({ versionId, versionName });    
  }

  handleSelectEnv=(envId, other) => {
    const { envList } = CreateAutoTestStore;
    CreateAutoTestStore.setEnv(_.find(envList, { id: envId }));
  }

  loadEnvs=() => {
    getAllEnvs().then((res) => {
      CreateAutoTestStore.setEnvList(res);      
    });
  }

  render() {
    const { intl } = this.props;
    const { selectApp } = this.state;
    const { formatMessage } = intl;
    const {
      app, env, version, appVersion, envList,
    } = CreateAutoTestStore;
    return (
      <div className="deployApp-app">        
        {/* 选择应用 */}
        <section className="deployApp-section">
          <div className="autotest-text">
            {/* 选择应用版本后才显示应用 */}
            {appVersion.id && (
              <div>
                <Tooltip title={<FormattedMessage id="project" />}><span className="icon icon-project section-text-icon" /></Tooltip>
                <span className="section-text">
                  {app.name}
                  {'('}
                  {app.code}
                  {')'}
                </span>
              </div>
            )}
            <a
              role="none"              
              onClick={this.showSideBar}
            >
              {formatMessage({ id: 'autotestapp_add' })}
              <i className="icon icon-open_in_new icon-small" />
            </a>
          </div>
        </section>
        {/* 选择目标版本 */}
        <section className="deployApp-section">         
          <SelectVersion
            value={version.versionId}            
            style={{ width: 482 }}
            onChange={this.handleVersionSelect}
          />
        </section>
        {/* 选择环境 */}
        <section className="deployApp-section">          
          <Select
            value={env.id}
            label={formatMessage({ id: 'autoteststep_one_environment' })}           
            onSelect={this.handleSelectEnv}
            style={{ width: 482 }}
            optionFilterProp="children"
            filterOption={(input, option) => option.props.children[1]
              .toLowerCase().indexOf(input.toLowerCase()) >= 0}
            filter
            onFocus={this.loadEnvs}
          >
            {envList.map(v => (
              // <Option value={v.id} key={v.id} disabled={!v.connect}>
              <Option value={v.id} key={v.id} disabled={!v.connect}>
                {v.connect ? <span className="c7ntest-ist-status_on" /> : <span className="c7ntest-ist-status_off" />}
                {v.name}
              </Option>
            ))}
          </Select>
        </section>
        {/* <section className="deployApp-section">
          <Button
            type="primary"
            funcType="raised"
            disabled={!app.id || !appVersion.id || !version.versionId || !env.id}
            onClick={CreateAutoTestStore.nextStep}
          >
            {formatMessage({ id: 'next' })}
          </Button>
          <Button funcType="raised" className="c7ntest-autotest-clear" onClick={CreateAutoTestStore.clearTestInfo}>{formatMessage({ id: 'cancel' })}</Button>
        </section> */}
        <SelectAppAndVersion
          show={selectApp}
          handleCancel={this.handleCancel}
          handleOk={this.handleOk}
        />
      </div>
    );
  }
}


export default SelectVariable;
