import React, { memo } from 'react';
import PropTypes from 'prop-types';
import {
  Select, Dropdown, Button, Menu, Icon,
} from 'choerodon-ui';
import { StatusTags } from '../../../../../components';
import './QuickOperate.less';

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
  // const options = statusList.map((status) => (
  //   <Option value={status.statusId} key={status.statusId}>
  //     <StatusTags
  //       color={status.statusColor}
  //       name={status.statusName}
  //     />
  //   </Option>
  // ));
  const menuItems = statusList.map(item => (
    <Menu.Item key={item.statusId} style={{ display: 'flex', alignItems: 'center' }}>
      <StatusTags
        color={item.statusColor}
        name={item.statusName}
      />
    </Menu.Item>
  ));
  const menu = (
    <Menu onClick={item => onSubmit({ executionStatus: item.key })}>
      {menuItems}
    </Menu>
  );
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
      <Dropdown overlay={menu} trigger="click">
        <Button style={{ padding: '0 5px', marginLeft: 15 }}>
          其它状态
          <Icon type="arrow_drop_down" />
        </Button>
      </Dropdown>
    </div>
  );
};
QuickOperate.propTypes = propTypes;
export default memo(QuickOperate);
