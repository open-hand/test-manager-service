import React, { Component } from 'react';
import { Select } from 'choerodon-ui';
import { injectIntl } from 'react-intl';
import { observer } from 'mobx-react-lite';
import _ from 'lodash';
import { SelectFocusLoad } from '../../../../../../components';
import CreateAutoTestStore from '../../../../stores/CreateAutoTestStore';
import { getAllEnvs } from '../../../../../../api/AutoTestApi';
import './SelectVariable.less';

const { Option } = Select;

export default injectIntl(observer((props) => {
  const { intl } = props;
  const { formatMessage } = intl;

  const handleSelectEnv = (envId, other) => {
    const { envList } = CreateAutoTestStore;
    CreateAutoTestStore.setEnv(_.find(envList, { id: envId }));
  };

  const loadEnvs = () => {
    getAllEnvs().then((res) => {
      CreateAutoTestStore.setEnvList(res);
    });
  };

  /**
   * 点击选择数据
   * @param record
   */
  const handleSelectApp = (id) => {
    const { appList } = CreateAutoTestStore;
    CreateAutoTestStore.setApp(_.find(appList, { id }));
    CreateAutoTestStore.setAppVersion({});
  };

  const handleSelectAppVersion = (id) => {
    const { appVersionList } = CreateAutoTestStore;
    CreateAutoTestStore.setAppVersion(_.find(appVersionList, { id }));
  };

  const {
    app, appVersion, env, envList,
  } = CreateAutoTestStore;
  return (
    <div className="deployApp-app">
      {/* 选择应用 */}
      <SelectFocusLoad
        type="app"
        label="应用"
        style={{ width: 512, display: 'block' }}
        onChange={handleSelectApp}
        value={app.id}
        saveList={(list) => { CreateAutoTestStore.setAppList(list); }}
      />
      <SelectFocusLoad
        disabled={!app.id}
        type="appVersion"
        label="应用版本"
        style={{ width: 512, display: 'block', marginTop: 20 }}
        appId={app.id}
        onChange={handleSelectAppVersion}
        value={appVersion.id}
        saveList={(list) => { CreateAutoTestStore.setAppVersionList(list); }}
      />
      {/* 选择环境 */}
      <section className="deployApp-section">
        <Select
          value={env.id}
          label={formatMessage({ id: 'autoteststep_one_environment' })}
          onSelect={handleSelectEnv}
          style={{ width: 512 }}
          optionFilterProp="children"
          filterOption={(input, option) => option.props.children[1]
            .toLowerCase().indexOf(input.toLowerCase()) >= 0}
          filter
          onFocus={loadEnvs}
        >
          {envList.map(v => (
            <Option value={v.id} key={v.id} disabled={v.connect}>
              {!v.connect ? <span className="c7ntest-ist-status_on" /> : <span className="c7ntest-ist-status_off" />}
              {v.name}
            </Option>
          ))}
        </Select>
      </section>
    </div>
  );
}));
