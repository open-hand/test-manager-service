import React from 'react';
import { Link } from 'react-router-dom';
import {
  Menu, Dropdown, Icon, Button,
} from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import { commonLink } from '../../../../common/utils';

const ReporterSwitcher = (props) => {
  const menu = (
    <Menu>
      <Menu.Item key="0">
        <Link to={commonLink('/report/story')}>
          <FormattedMessage id="report_dropDown_demand" />
        </Link>
      </Menu.Item>
      <Menu.Item key="1">
        <Link to={commonLink('/report/test')}>
          <FormattedMessage id="report_dropDown_defect" />
        </Link>
      </Menu.Item>
      <Menu.Item key="2">
        <Link to={commonLink('/report/progress')}>
          <FormattedMessage id="report_dropDown_progress" />
        </Link>
      </Menu.Item>
      {
        !props.isHome && (
          <Menu.Item key="3">
            <Link to={commonLink('/report')}>
              <FormattedMessage id="report_dropDown_home" />
            </Link>
          </Menu.Item>
        )
      }
    </Menu>
  );
  return (
    <Dropdown placement="bottomCenter" overlay={menu} trigger={['click']}>
      <Button funcType="flat">
        <FormattedMessage id="report_switch" />
        <Icon type="arrow_drop_down" />
      </Button>
    </Dropdown>
  );
};
export default ReporterSwitcher;
