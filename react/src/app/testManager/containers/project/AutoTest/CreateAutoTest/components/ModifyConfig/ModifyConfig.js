
import React, { Component } from 'react';
import { Button } from 'choerodon-ui';
import { observer } from 'mobx-react';
import { injectIntl, FormattedMessage } from 'react-intl';

import { YamlEditor } from '../../../../../../components/CommonComponent';
import CreateAutoTestStore from '../../../../../../store/project/AutoTest/CreateAutoTestStore';
import { getYaml, checkYaml } from '../../../../../../api/AutoTestApi';

@injectIntl
@observer
class ModifyConfig extends Component {
  state = {
    markers: null,
    errorLine: [],
  };

  componentDidMount() {
    this.loadYaml();
  }

  loadYaml=() => {
    const { app, appVersion, env } = CreateAutoTestStore;
    getYaml(app.id, appVersion.id, env.id).then((data) => {
      if (data) {
        CreateAutoTestStore.setConfigValue(data);
      }
    });
  }

  /**
   * 获取values
   * @param value
   */
  handleChangeValue = (value) => {
    CreateAutoTestStore.setNewConfigValue(value);
    checkYaml(value)
      .then((data) => {
        this.setState({ errorLine: data });
      });
  };

  render() {
    const { intl } = this.props;
    const { formatMessage } = intl;
    const { errorLine, markers } = this.state;
    const data = CreateAutoTestStore.getNewConfigValue;
    return (
      <div className="deployApp-env">
        <p>
          {formatMessage({ id: 'autoteststep_two_description' })}
        </p>

        <section className="deployApp-section">
          <div className="autotest-title">
            <i className="icon icon-description section-title-icon " />
            <span className="section-title">{formatMessage({ id: 'autoteststep_two_config' })}</span>
          </div>
          {data && (
            <YamlEditor
              newLines={data.newLines}
              isFileError={!!data.errorLines}
              totalLine={data.totalLine}
              errorLines={errorLine}
              errMessage={data.errorMsg}
              modifyMarkers={markers}
              value={data.yaml}
              highlightMarkers={data.highlightMarkers}
              onChange={this.handleChangeValue}
              change
            />
          )}
        </section>
        <section className="deployApp-section">
          <Button
            type="primary"
            funcType="raised"
            onClick={CreateAutoTestStore.nextStep}
            disabled={!data || errorLine.length > 0}
          >
            {formatMessage({ id: 'next' })}
          </Button>
          <Button onClick={CreateAutoTestStore.preStep} funcType="raised">{formatMessage({ id: 'previous' })}</Button>
          <Button funcType="raised" className="c7ntest-autotest-clear" onClick={CreateAutoTestStore.clearTestInfo}>{formatMessage({ id: 'cancel' })}</Button>
        </section>
      </div>
    );
  }
}

export default ModifyConfig;
