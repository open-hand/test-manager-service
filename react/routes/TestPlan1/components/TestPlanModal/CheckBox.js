import React, { useCallback } from 'react';
import { observer } from 'mobx-react-lite';
import { CheckBox as ProCheckBox } from 'choerodon-ui/pro';

function CheckBox({ item, onChange }) {
  // console.log(item);
  const handleChange = useCallback((checked) => {
    onChange(checked, item);
  }, [item, onChange]);
  return (
    <ProCheckBox
      indeterminate={item.isIndeterminate || false}
      checked={item.checked || false}
      onChange={handleChange}
    />
  );
}

export default observer(CheckBox);
