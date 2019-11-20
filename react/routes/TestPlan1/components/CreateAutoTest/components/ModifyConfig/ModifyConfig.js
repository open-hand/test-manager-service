
import React, { useState, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { injectIntl, FormattedMessage } from 'react-intl';

import { YamlEditor } from '../../../../../../components';
import CreateAutoTestStore from '../../../../stores/CreateAutoTestStore';
import { getYaml, checkYaml } from '../../../../../../api/AutoTestApi';

export default injectIntl(observer(() => {
  const [markers, setMarkers] = useState(null);

  const loadYaml = () => {
    const { app, appVersion, env } = CreateAutoTestStore;
    getYaml(app.id, appVersion.id, env.id).then((data) => {
      if (data) {
        CreateAutoTestStore.setConfigValue(data);
      }
    });
  };
  
  useEffect(() => {
    loadYaml();
  }, []);

 
  /**
   * 获取values
   * @param value
   */
  const handleChangeValue = (value) => {
    CreateAutoTestStore.setNewConfigValue(value);
    checkYaml(value)
      .then((data) => {      
        CreateAutoTestStore.setNewConfigValue(value, data);
      });
  };

  const data = CreateAutoTestStore.getNewConfigValue;
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
          onChange={handleChangeValue}
          change
        />
      )}
    </div>
  );
}));
