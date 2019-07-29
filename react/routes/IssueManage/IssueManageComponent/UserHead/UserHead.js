import React, { memo, Fragment } from 'react';
import { Tooltip } from 'choerodon-ui';

function getFirst(str) {
  if (!str) {
    return '';
  }
  const re = /[\u4E00-\u9FA5]/g;
  for (let i = 0, len = str.length; i < len; i += 1) {
    if (re.test(str[i])) {
      return str[i];
    }
  }
  return str[0];
}
const UserHead = memo(({ 
  user, 
  color, 
  size, 
  hiddenText, 
  style,
  type, 
  tooltip = true, 
}) => {
  const iconSize = size || 18;
  return (
    <Tooltip title={tooltip ? `${user.loginName || ''}${user.realName || ''}` : ''} mouseEnterDelay={0.5}>
      <div        
        style={{
          ...style,
          display: user.id ? 'inline-block' : 'none',
          maxWidth: 108,
          verticalAlign: 'text-bottom',
        }}
      >
        {
          type === 'datalog' ? (
            <div
              style={{
                width: 40,
                height: 40,
                background: '#b3bac5',
                color: '#fff',
                overflow: 'hidden',
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                textAlign: 'center',
                borderRadius: 4,
                flexShrink: 0,
              }}
            >
              {
                user.avatar ? (
                  <img src={user.avatar} alt="" style={{ width: '100%' }} />
                ) : (
                  <span
                    style={{
                      width: 40, height: 40, lineHeight: '40px', textAlign: 'center', color: '#fff', fontSize: '12px',
                    }}
                    className="user-Head-Title"
                  >
                    {getFirst(user.realName)}
                  </span>
                )
              }
            </div>
          ) : (
            <Fragment>
              {
                  user.avatar ? (
                    <img
                      src={user.avatar}
                      alt=""
                      style={{
                        width: iconSize,
                        height: iconSize,
                        marginRight: 5,   
                        borderRadius: '50%', 
                        verticalAlign: 'middle', 
                        background: '#c5cbe8',
                      }}
                    />
                  ) : (
                    <span style={{
                      width: iconSize,
                      height: iconSize,
                      lineHeight: `${iconSize}px`, 
                      textAlign: 'center',
                      color: '#6473c3',
                    }}
                    >
                      {getFirst(user.realName)}
                    </span>
                  )
                }
            </Fragment>
          )
        }
        {
          hiddenText ? null : (
            <span
              style={{                
                display: 'inline-block',       
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                borderRadius: '50%', 
                verticalAlign: 'middle',
                whiteSpace: 'nowrap',
                fontSize: '13px',
                lineHeight: '20px',
                color: color || 'rgba(0, 0, 0, 0.65)',
              }}
            >
              {`${user.realName || user.loginName}`}
            </span>
          )
        }
      </div>
    </Tooltip>
  ); 
});
export default UserHead;
