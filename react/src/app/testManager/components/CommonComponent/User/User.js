import React, { memo } from 'react';
import isEqual from 'react-fast-compare';
import { Tooltip } from 'choerodon-ui';

const User = ({
  user,
}) => (user ? (
  <Tooltip title={`${user.loginName}${user.realName}`}>  
    <div
      style={{
        display: 'inline-flex',
        alignItems: 'center',
        padding: 2,
      }}
    >
      <div
        style={{
          width: 18,
          height: 18,
          background: '#c5cbe8',
          color: '#6473c3',
          overflow: 'hidden',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          marginRight: 5,
          textAlign: 'center',
          borderRadius: '50%',
        }}
      >
        {
        user.imageUrl ? (
          <img
            src={user.imageUrl}
            alt=""
            style={{ width: '100%', height: '100%' }}
          />
        ) : (
          <span>
            {user.realName[0]}
          </span>
        )
      }
      </div>
      <div>
        <span
          style={{
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'nowrap',
            fontSize: '13px',
            lineHeight: '20px',
            color: 'rgba(0, 0, 0, 0.65)',
          }}
        >
          {/* {user.loginName}
        {' '} */}
          {user.realName}
        </span>
      </div>
    </div>
  </Tooltip>
) : null);


export default memo(User, isEqual);
