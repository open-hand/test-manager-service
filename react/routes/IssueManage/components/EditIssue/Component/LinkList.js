/*eslint-disable */
import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import { Icon, Popconfirm, Tooltip } from 'choerodon-ui';
import _ from 'lodash';
import { deleteLink } from '../../../../../api/IssueManageApi';
import PriorityTag from '../../PriorityTag';
import StatusTag from '../../StatusTag';
import TypeTag from '../../TypeTag';
import UserHead from '@/components/UserHead';
import { issueLink } from '../../../../../common/utils';

class LinkList extends Component {
  confirm(issueId, e) {
    this.handleDeleteIssue(issueId);
  }

  handleDeleteIssue(linkId) {
    deleteLink(linkId)
      .then((res) => {
        this.props.onRefresh();
      });
  }

  render() {
    const { issue, i } = this.props;
    const {
      priorityVO, issueTypeVO, issueNum, summary, issueId, linkedIssueId, assigneeId, assigneeName, imageUrl,
      linkId, ward, statusVO,
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
          borderBottom: '1px solid rgba(0, 0, 0, 0.12)',
          borderTop: !i ? '1px solid rgba(0, 0, 0, 0.12)' : '',
        }}
      >
        <Tooltip mouseEnterDelay={0.5} title={`任务类型： ${typeName}`}>
          <div>
            <TypeTag data={issueTypeVO} />
          </div>
        </Tooltip>
        <Tooltip title={`编号概要： ${issueNum} ${summary}`}>
          <div style={{ marginLeft: 8, flex: 1, overflow: 'hidden' }}>
            <p
              className="primary"
              style={{
                overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', marginBottom: 0,
              }}
              role="none"
            // onClick={() => {
            //   this.props.onOpen(issue.issueId, issue.linkedIssueId);
            // }}
            >
              <Link to={issueLink(Reg.test(ward) ? issueId : linkedIssueId, typeCode, issueNum)}>
                {`${issueNum} ${summary}`}
              </Link>
            </p>
          </div>
        </Tooltip>
        <UserHead
          user={{
            id: assigneeId,
            name: assigneeName,
            loginName: assigneeName,
            realName: assigneeName,
            avatar: imageUrl,
          }}
        />
        <div style={{ marginRight: '15px', overflow: 'hidden' }}>
          <Tooltip mouseEnterDelay={0.5} title={`优先级： ${priorityName}`}>
            <div style={{ marginRight: 12 }}>
              <PriorityTag priority={priorityVO} />
            </div>
          </Tooltip>
        </div>
        <div style={{
          width: '48px', marginRight: '15px', display: 'flex', justifyContent: 'flex-end',
        }}
        >
          <Tooltip mouseEnterDelay={0.5} title={`任务状态： ${statusName}`}>
            <div>
              <StatusTag
                status={statusVO}
              />
            </div>
          </Tooltip>
        </div>
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            fontSize: '16px',
            marginBottom: '5px',
          }}
        >
          <Popconfirm
            title="确认要删除吗?"
            placement="left"
            onConfirm={this.confirm.bind(this, linkId)}
            onCancel={this.cancel}
            okText="删除"
            cancelText="取消"
            okType="danger"
          >
            <Icon type="delete_forever mlr-3 pointer" />
          </Popconfirm>
        </div>
      </div>
    );
  }
}

export default LinkList;
