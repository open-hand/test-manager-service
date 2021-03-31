import React from 'react';
import { observer } from 'mobx-react-lite';
import PropTypes from 'prop-types';
import { CheckBox } from 'choerodon-ui/pro';

const propTypes = {
  field: PropTypes.string.isRequired,
  checkedMap: PropTypes.object.isRequired,
  value: PropTypes.any.isRequired,
  dataSource: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
};
const CustomCheckBox = observer((props) => {
  const {
    field, checkedMap, value, dataSource, onChangeCallBack, ...restProps
  } = props;
  const handleChange = (newValue, oldValue) => {
    if (newValue) {
      if (newValue === 'all') {
        if (dataSource && dataSource.length) {
          dataSource.forEach((record) => {
            if (!checkedMap.has(record[field])) {
              checkedMap.set(record[field], true);
            }
          });
        }
      } else {
        checkedMap.set(newValue, true);
      }
      onChangeCallBack();
    } else if (oldValue) {
      if (oldValue === 'all') {
        if (dataSource && dataSource.length) {
          dataSource.forEach((record) => {
            if (checkedMap.has(record[field])) {
              checkedMap.delete(record[field]);
            }
          });
        }
      } else {
        checkedMap.delete(oldValue);
      }
      onChangeCallBack();
    }
  };

  const isAllChecked = () => {
    const allCheckedStatus = {
      allChecked: dataSource && dataSource.length > 0 ? dataSource.every((record) => checkedMap.has(record[field])) : false,
      hasChecked: dataSource && dataSource.length > 0 ? dataSource.some((record) => checkedMap.has(record[field])) && !dataSource.every((record) => checkedMap.has(record[field])) : false,
    };
    return allCheckedStatus;
  };

  return (
    <CheckBox
      value={value}
      onChange={handleChange}
      checked={value !== 'all' ? checkedMap.has(value) : isAllChecked().allChecked}
      indeterminate={value === 'all' ? isAllChecked().hasChecked : false}
      {...restProps}
    />
  );
});

CustomCheckBox.propTypes = propTypes;

export default CustomCheckBox;
