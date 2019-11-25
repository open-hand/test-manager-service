
import React, { Component } from 'react';
import { Button } from 'choerodon-ui';
import { observer } from 'mobx-react';
import { injectIntl, FormattedMessage } from 'react-intl';

import { YamlEditor } from '../../../../../../components';
import { getYaml, checkYaml } from '../../../../../../api/AutoTestApi';

@injectIntl
@observer
class ModifyConfig extends Component {
  state = {
    markers: null,
  };

  componentDidMount() {
    this.loadYaml();
  }

  loadYaml=() => {
    const { createAutoTestStore } = this.props;
    const { app, appVersion, env } = createAutoTestStore;
    getYaml(app.id, appVersion.id, env.id).then((data) => {
      if (data) {
        createAutoTestStore.setConfigValue(data);
      }
    });
  }

  /**
   * 获取values
   * @param value
   */
  handleChangeValue = (value) => {
    const { createAutoTestStore } = this.props;
    createAutoTestStore.setNewConfigValue(value);
    checkYaml(value)
      .then((data) => {      
        createAutoTestStore.setNewConfigValue(value, data);
      });
  };

  render() {
    const { markers } = this.state;
    const { createAutoTestStore } = this.props;
    const data = createAutoTestStore.getNewConfigValue;
    return (
      <div className="deployApp-env">
        {data && (
        <YamlEditor
          newLines={data.newLines}
          isFileError={data.errorLines && data.errorLines.length > 0}
          totalLine={data.totalLine}
          errorLines={data.errorLines}
          errMessage={data.errorMsg}
          modifyMarkers={markers}
          value={data.yaml}
          highlightMarkers={data.highlightMarkers}
          onChange={this.handleChangeValue}
          change
        />
        )}
      </div>
    );
  }
}

export default ModifyConfig;
