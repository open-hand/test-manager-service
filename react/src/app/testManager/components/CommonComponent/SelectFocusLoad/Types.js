import React from 'react';
import { Select } from 'choerodon-ui';
import { find } from 'lodash';
import User from '../User';
import { getUsers, getUser } from '../../../api/IamApi';
import { getFoldersByVersion } from '../../../api/IssueManageApi';
import { getProjectVersion } from '../../../api/agileApi';

const { Option } = Select;

export default {
  user: {
    request: (...args) => new Promise(resolve => getUsers(...args).then((UserData) => { resolve(UserData.content); })),
    render: user => (
      <Option key={user.id} value={user.id}>
        <User user={user} />
      </Option>
    ),
    avoidShowError: (props, List) => new Promise((resolve) => {
      const { value } = props;
      const UserList = [...List];

      if (value && !find(UserList, { id: value })) {
        getUser(value).then((res) => {
          if (res.content && res.content.length > 0) {
            UserList.push(res.content[0]);
            resolve(UserList);
          } else {
            resolve(null);
          }
        }).catch((err) => {
          console.log(err);
          resolve(null);
        });
      } else {
        resolve(null);
      }
    }),
  },
  version: {
    request: getProjectVersion,
    render: (version, { optionDisabled }) => (
      <Option value={version.versionId} key={version.versionId} disabled={optionDisabled && optionDisabled(version)}>
        {version.name}
      </Option>
    ),
  },
  folder: {
    propArg: 'versionId',
    request: (value, ...args) => getFoldersByVersion(...args),
    render: folder => (
      <Option value={folder.folderId} key={folder.folderId}>
        {folder.versionName}
        -
        {folder.name}
      </Option>
    ),
    props: {
      filterOption: (input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0,
    },
  },
};
