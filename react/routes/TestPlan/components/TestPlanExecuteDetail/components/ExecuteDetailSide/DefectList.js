import React from 'react';
import { Icon, Popconfirm, Tooltip } from 'choerodon-ui';
import { Link } from 'react-router-dom';
import { observer } from 'mobx-react';
import TypeTag from '../../../../../../components/TypeTag';
import PriorityTag from '../../../../../../components/PriorityTag';
import StatusTag from '../../../../../../components/StatusTag';
import { issueLink } from '../../../../../../common/utils';

const DefectList = ({ defects, onRemoveDefect }) => {
  const confirm = (issueId) => {
    onRemoveDefect(issueId);
  };

  const renderItem = (defect, index) => {
    const { id, issueInfosVO } = defect;
    const {
      priorityVO, issueTypeVO, issueNum, summary, issueId,
      statusVO,
    } = issueInfosVO;
    const { name: priorityName } = priorityVO || {};
    const { name: typeName, typeCode } = issueTypeVO || {};
    const { name: statusName } = statusVO || {};
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
              className="primary"
              style={{
                overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', marginBottom: 0,
              }}
              role="none"
            >
              <Link to={issueLink(issueId, typeCode, issueNum)}>
                {`${issueNum} ${summary}`}
              </Link>
            </p>
          </div>
        </Tooltip>
        <div style={{ marginRight: 12 }}>
          <Tooltip mouseEnterDelay={0.5} title={`优先级： ${priorityName}`}>
            <PriorityTag priority={priorityVO || {}} style={{ maxWidth: '20px' }} />
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
            <Icon type="delete_sweep-o" />
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
