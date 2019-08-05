import React, { memo } from 'react';
import PropTypes from 'prop-types';
import { Select } from 'choerodon-ui';
import { StatusTags } from '../../../../../components';

const { Option } = Select;
const style = {
  border: '1px solid #00BF96',
  borderRadius: '2px',
  marginLeft: 15,
  padding: '1px 5px',
  cursor: 'pointer',
  fontSize: '12px',
};
const propTypes = {
  statusList: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
  quickPass: PropTypes.func.isRequired,
  quickFail: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
};
const QuickOperate = ({
  statusList,
  quickPass,
  quickFail,
  onSubmit,
}) => {
  const options = statusList.map(status => (
    <Option value={status.statusId} key={status.statusId}>
      <StatusTags
        color={status.statusColor}
        name={status.statusName}
      />
    </Option>
  ));
  return (
    <div style={{ fontSize: '14px', display: 'flex', alignItems: 'center' }}>
      快速操作:
      <span
        style={{
          ...style,
          color: '#00BF96',
          borderColor: '#00BF96',
        }}
        role="button"
        onClick={quickPass}
        className="c7ntest-quick-pass"
        onKeyDown
      >
        通过
      </span>
      <span
        style={{
          ...style,
          color: '#F44336',
          borderColor: '#F44336',
        }}
        role="button"
        onClick={quickFail}
        className="c7ntest-quick-fail"
        onKeyDown
      >
        失败
      </span>
      <Select
        className="c7ntest-select"
        placeholder="其他状态"
        value={null}
        style={{ width: 80, marginLeft: 20 }}
        onChange={(id) => { onSubmit({ executionStatus: id }); }}
      >
        {options}
      </Select>
    </div>
  );
};
QuickOperate.propTypes = propTypes;
export default memo(QuickOperate);
