/*eslint-disable */
import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import { Icon, Popconfirm, Tooltip } from 'choerodon-ui';
import _ from 'lodash';
import PriorityTag from '../PriorityTag';
import StatusTag from '../StatusTag';
import TypeTag from '../TypeTag';
import UserHead from '@/components/UserHead';
import { issueLink } from '@/common/utils';

function LinkList(props) {

  function confirm(issueId, e) {
    const { deleteLink } = props;
    if (deleteLink) {
      deleteLink();
    }
  }

  function render() {
    const { issue, i, deleteLink } = props;
    const {
      priorityVO, issueTypeVO, issueNum, summary, issueId, assigneeId, assigneeName, imageUrl,
      linkId, ward, statusVO, applyType,
    } = issue;
    const { colour: priorityColor, name: priorityName } = priorityVO || {};
    const { colour: typeColor, name: typeName, typeCode } = issueTypeVO || {};
    const { colour: statusColor, name: statusName } = statusVO || {};
    const Reg = /被/g;
    return (
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          padding: '8px 10px',
          cursor: 'pointer',
          borderBottom: '1px solid var(--divider)',
          borderTop: !i ? '1px solid var(--divider)' : '',
        }}
      >
        <div style={{ display: 'flex', alignContent: 'center', flex: 1, overflow: 'hidden' }}>
          <Tooltip mouseEnterDelay={0.5} title={`任务类型： ${typeName}`}>
            <TypeTag data={issueTypeVO} style={{ flexShrink: 0 }} />
          </Tooltip>
          <Tooltip title={`编号概要： ${issueNum} ${summary}`}>
            <div
              className="primary"
              style={{
                display: 'inline-block',
                marginLeft: 8,
                overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', flex: 1,
              }}
              role="none"
            >
              <Link to={issueLink(issueId, typeCode, issueNum, false, applyType)} style={{ lineHeight: '27px' }}>
                {`${issueNum} ${summary}`}
              </Link>
            </div>
          </Tooltip>
        </div>
        <UserHead
          user={{
            id: assigneeId,
            name: assigneeName,
            loginName: assigneeName,
            realName: assigneeName,
            avatar: imageUrl,
            maxWidth: 128,
          }}
        />
        <div style={{ marginRight: '15px', overflow: 'hidden' }}>
          <Tooltip mouseEnterDelay={0.5} title={`优先级： ${priorityName}`}>
            <PriorityTag priority={priorityVO} />
          </Tooltip>
        </div>
        <div style={{
          width: '48px', marginLeft: 12, marginRight: '15px', display: 'flex', justifyContent: 'flex-end',
        }}
        >
          <Tooltip mouseEnterDelay={0.5} title={`任务状态： ${statusName}`}>
            <StatusTag
              status={statusVO}
            />
          </Tooltip>
        </div>
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            fontSize: '16px',
          }}
        >
          {
            deleteLink && <Popconfirm
              title="确认要删除该工作项链接吗?"
              placement="left"
              onConfirm={confirm.bind(this, linkId)}
              onCancel={null}
              okText="删除"
              cancelText="取消"
            >
              <Icon type="delete_sweep-o" />
            </Popconfirm>
          }

        </div>
      </div>
    );
  }
  return render();
}

export default LinkList;
