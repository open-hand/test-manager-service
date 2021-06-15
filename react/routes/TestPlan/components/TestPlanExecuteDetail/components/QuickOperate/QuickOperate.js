import React, { memo } from 'react';
import PropTypes from 'prop-types';
import {
  Button, Menu, Icon,
} from 'choerodon-ui';
import { Dropdown } from 'choerodon-ui/pro';
import { debounce } from 'lodash';
import { StatusTags } from '../../../../../../components';
import styles from './QuickOperate.less';

const style = {
  border: '1px solid #00BF96',
  borderRadius: '2px',
  marginLeft: 15,
  padding: '1px 5px',
  cursor: 'pointer',
  fontSize: '12px',
};
const statusArr = [
  { name: '通过', color: '#00BF96' },
  { name: '失败', color: '#F44336' },
  { name: '无需测试', color: '#4D90FE' },
  { name: '重测', color: '#FFB100' }];
const QuickStatus = ({
  name, color, children, onClick, disable,
}) => (
  <span
    style={{
      ...style,
      color,
      borderColor: color,
    }}
    role="button"
    onClick={disable ? null : onClick.bind(this, name)}
    onKeyDown
  >
    {children}
  </span>
);
const propTypes = {
  statusList: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
  quickHandle: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
  readOnly: PropTypes.bool,
};
const QuickOperate = ({
  statusList,
  quickHandle,
  onSubmit,
  readOnly,
}) => {
  const menu = (
    <Menu onClick={(item) => {
      if (!readOnly) {
        onSubmit({
          executionStatus: item.key,
        });
      }
    }}
    >
      {statusList.filter((status) => !statusArr.some((s) => s.name === status.statusName)).map((item) => (
        <Menu.Item key={item.statusId} className={styles.menuItem}>
          <StatusTags
            color={item.statusColor}
            name={item.statusName}
            style={{
              height: 22,
              lineHeight: '22px',
            }}
          />
        </Menu.Item>
      ))}
    </Menu>
  );
  return (
    <div style={{ fontSize: '14px', display: 'flex', alignItems: 'center' }}>
      快速操作:
      {
        statusArr.map((status) => (
          <QuickStatus name={status.name} color={status.color} onClick={debounce(quickHandle, 300)} disable={readOnly}>
            {status.name}
          </QuickStatus>
        ))
      }
      <Dropdown overlay={menu} trigger="click">
        <Button style={{ padding: '0 5px', marginLeft: 15 }}>
          变更状态
          <Icon type="arrow_drop_down" />
        </Button>
      </Dropdown>
    </div>
  );
};
QuickOperate.propTypes = propTypes;
export default memo(QuickOperate);
