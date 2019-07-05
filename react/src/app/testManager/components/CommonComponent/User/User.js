import React, { memo } from 'react';
import isEqual from 'react-fast-compare';
import { Tooltip } from 'choerodon-ui';

const User = ({
  user,
}) => (user ? (
  <Tooltip title={`${user.loginName}${user.realName}`}>  
    <div
      style={{
        display: 'inline-block',
        padding: 2,
        lineHeight: '18px',
        verticalAlign: 'text-bottom',
      }}
    >      
      {
        user.imageUrl ? (
          <img
            src={user.imageUrl}
            alt=""
            style={{        
              width: 18,
              height: 18,   
              marginRight: 5,   
              borderRadius: '50%', 
              verticalAlign: 'middle',
              background: '#c5cbe8',
            }}
          />
        ) : (
          <span style={{  
            display: 'inline-block',   
            textAlign: 'center',
            borderRadius: '50%', 
            verticalAlign: 'middle',
            background: '#c5cbe8',
            color: '#6473c3',
            marginRight: 5,   
            width: 18,
            height: 18,       
          }}
          >
            {user.realName[0]}
          </span>
        )
      }     
      <span
        style={{
          overflow: 'hidden',
          textOverflow: 'ellipsis',
          whiteSpace: 'nowrap',
          fontSize: '13px',
          verticalAlign: 'middle',
          color: 'rgba(0, 0, 0, 0.65)',
        }}
      >
        {user.realName}
      </span>
    </div>
  </Tooltip>
) : null);


export default memo(User, isEqual);
