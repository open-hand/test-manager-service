import React, { useState } from 'react';
import { toJS } from 'mobx';
import { observer } from 'mobx-react-lite';
import PropTypes from 'prop-types';
import { CheckBox } from 'choerodon-ui/pro';

const propTypes = {
  field: PropTypes.string.isRequired,
  checkedMap: PropTypes.object.isRequired,
  value: PropTypes.any.isRequired,
};
const CustomCheckBox = observer((props) => {
  const [selectAllChecked, setSelectAllChecked] = useState(false);
  const [indeterminate, setIndeterminate] = useState(false);
  const {
    field, checkedMap, value, dataSource, ...restProps 
  } = props;
  const handleChange = (newValue, oldValue) => {
    console.log(`new:${newValue}, old:${oldValue}`);
    console.log(toJS(dataSource));
    if (newValue) {
      if (newValue === 'all') {
        setSelectAllChecked(true);
        dataSource.forEach((record) => {
          if (!checkedMap.has(record[field])) {
            checkedMap.set(record[field], true);
          }
        });
      } else {
        checkedMap.set(newValue, true);
      }
    } else if (oldValue) {
      if (oldValue === 'all') {
        setSelectAllChecked(false);
        dataSource.forEach((record) => {
          if (!checkedMap.has(record[field])) {
            checkedMap.delete(record[field]);
          }
        });
      } else {
        checkedMap.delete(oldValue);
      }
    }
  };   
  
  return (
    <CheckBox 
      value={value} 
      onChange={handleChange} 
      checked={value !== 'all' ? checkedMap.has(value) : selectAllChecked} 
      indeterminate={indeterminate} 
      {...restProps} 
    />
  );
});

CustomCheckBox.propTypes = propTypes;

export default CustomCheckBox;
