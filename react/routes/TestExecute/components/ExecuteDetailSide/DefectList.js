import React, { Component } from 'react';
import { Icon, Popconfirm, Tooltip } from 'choerodon-ui';
import { Link } from 'react-router-dom';
import _ from 'lodash';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import TypeTag from '../../../IssueManage/components/TypeTag';
import PriorityTag from '../../../IssueManage/components/PriorityTag';
import StatusTag from '../../../IssueManage/components/StatusTag';
import { issueLink } from '../../../../common/utils';

const DefectList = ({ defects, onRemoveDefect }) => {
  const confirm = (issueId, e) => {
    onRemoveDefect(issueId);
  };

  const renderItem = (defect, index) => {
    const { id, issueInfosVO } = defect;
    const {
      priorityVO, issueTypeVO, issueNum, summary, issueId, 
      ward, statusVO,
    } = issueInfosVO;
    const { colour: priorityColor, name: priorityName } = priorityVO || {};
    const { colour: typeColor, name: typeName, typeCode } = issueTypeVO || {};
    const { colour: statusColor, name: statusName } = statusVO || {};
    const Reg = /被/g;
    return (
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          padding: '5px 0',
          cursor: 'pointer',
          borderBottom: '1px solid rgba(0, 0, 0, 0.12)',
          borderTop: !index ? '1px solid rgba(0, 0, 0, 0.12)' : '',
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
              style={{
                overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', marginBottom: 0, color: 'rgb(63, 81, 181)',
              }}
              role="none"
            >
              <Link to={issueLink(issueId, typeCode, issueNum)} target="_blank">
                {`${issueNum} ${summary}`}
              </Link>
            </p>
          </div>
        </Tooltip>
        <div style={{ width: '34px', marginRight: '15px', overflow: 'hidden' }}>
          <Tooltip mouseEnterDelay={0.5} title={`优先级： ${priorityName}`}>
            <div style={{ marginRight: 12 }}>
              <PriorityTag priority={priorityVO || {}} />
            </div>
          </Tooltip>
        </div>
        <div style={{
          width: '48px', marginRight: '15px', display: 'flex', justifyContent: 'flex-end',
        }}
        >
          <Tooltip mouseEnterDelay={0.5} title={`任务状态： ${statusName}`}>
            <div style={{ lineHeight: '10px' }}>
              <StatusTag
                status={statusVO || {}}
              />
            </div>
          </Tooltip>
        </div>
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            fontSize: '16px',
          }}
        >
          <Popconfirm
            title="确认要移除该缺陷吗?"
            placement="left"
            onConfirm={() => { confirm(id); }}
            okText="删除"
            cancelText="取消"
            okType="danger"
          >
            <Icon type="delete_forever mlr-3 pointer" />
          </Popconfirm>
        </div>
      </div>
    );
  };
  return defects.map((defect, i) => renderItem(defect, i));
};


DefectList.propTypes = {

};

export default observer(DefectList);
