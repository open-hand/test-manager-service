import React, { useMemo, forwardRef } from 'react';
import { Select } from 'choerodon-ui/pro';
import { stores } from '@choerodon/boot';
import { getUsers } from '@/api/IamApi';
import { useSelect, FlatSelect } from '@choerodon/components';
import { SelectConfig } from '@choerodon/components/lib/hooks/useSelect';
import type { User } from '@/common/types';
import { SelectProps } from 'choerodon-ui/pro/lib/select/Select';

const { AppState } = stores;
export interface SelectUserProps extends Partial<SelectProps> {
  flat?: boolean
  self?:boolean
}

const SelectUser: React.FC<SelectUserProps> = forwardRef(({ flat = false, self = true, ...otherProps }, ref: React.Ref<Select>) => {
  const config = useMemo((): SelectConfig<User> => ({
    textField: 'realName',
    valueField: 'id',
    request: ({ filter, page }) => getUsers(filter, undefined, page),
    middleWare: self ? undefined : (users) => users.filter((u) => AppState.userInfo.id.toString() !== String(u.id)),
  }), [self]);
  const props = useSelect(config);
  const Component = flat ? FlatSelect : Select;
  return (
    <Component
      ref={ref}
      clearButton={false}
      {...props}
      {...otherProps}
    />
  );
});
export default SelectUser;
