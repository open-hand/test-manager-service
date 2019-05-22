import React, { Fragment } from 'react';
import { Tooltip, Tag } from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import UserHead from '../UserHead';
import PriorityTag from '../PriorityTag';
import StatusTag from '../StatusTag';
import TypeTag from '../TypeTag';

const styles = {
  issueNum: {
    padding: '0 12px 0 5px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap',
  },
};
const testTypes = {
  issue_auto_test: '自动化测试',
  issue_test: '测试',
};
export function renderType(issueTypeDTO, showTypeName) {
  const { name, typeCode } = issueTypeDTO || {};
  return (
    <Tooltip mouseEnterDelay={0.5} title={`任务类型： ${name}`}>
      <div style={{ display: 'flex' }}>
        <TypeTag
          type={issueTypeDTO || {}}
        />
        {showTypeName && <span style={{ marginLeft: 5 }}>{testTypes[typeCode]}</span>}
      </div>
    </Tooltip>
  );
}
export function renderIssueNum(issueNum) {
  return (
    <Tooltip mouseEnterDelay={0.5} title={<FormattedMessage id="issue_issueNum" values={{ num: issueNum }} />}>
      <a style={styles.issueNum}>
        {issueNum}
      </a>
    </Tooltip>
  );
}
export function renderSummary(summary) {
  return (
    <div style={{ overflow: 'hidden' }}>
      <Tooltip mouseEnterDelay={0.5} placement="topLeft" title={<FormattedMessage id="issue_issueSummary" values={{ summary }} />}>
        <p style={{
          overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', marginBottom: 0, maxWidth: 'unset',
        }}
        >
          {summary}
        </p>
      </Tooltip>
    </div>
  );
}
export function renderPriority(priorityDTO) {
  if (!priorityDTO) {
    return null;
  }
  const { name } = priorityDTO;
  return ( 
    <Tooltip mouseEnterDelay={0.5} title={`优先级： ${name}`}>
      <div style={{ display: 'inline-block' }}>
        <PriorityTag
          priority={priorityDTO}
        />
      </div>
    </Tooltip>
  );
}
export function renderVersions(versions, priorityName) {
  return (
    <Tooltip mouseEnterDelay={0.5} title={`版本： ${versions.map(version => version.name).join(',')}`}>
      {versions.map(version => (
        <div
          style={{
            display: 'inline-block',
            maxWidth: 'calc(100% - 10px)',
            color: 'rgba(0,0,0,0.36)',
            height: 22,
            borderWidth: '1px',
            borderStyle: 'solid',
            borderColor: 'rgba(0,0,0,0.36)',
            borderRadius: '2px',
            fontSize: '13px',
            lineHeight: '20px',
            padding: '0 8px',
            margin: '0 5px',
          }}
          className="c7ntest-text-dot"
        >
          {version.name}
        </div>
      ))}
    </Tooltip>
  );
}
export function renderEpic(epicName, epicColor) {
  return (
    epicName ? (
      <div
        style={{
          color: epicColor,
          height: 22,
          borderWidth: '1px',
          borderStyle: 'solid',
          borderColor: epicColor,
          borderRadius: '2px',
          fontSize: '13px',
          lineHeight: '20px',
          padding: '0 8px',
          margin: '0 5px',
        }}
      >
        {epicName}
      </div>
    ) : null
  );
}
export function renderFolder(folderName) {
  return (
    folderName ? (
      <Tooltip mouseEnterDelay={0.5} title={`文件夹： ${folderName}`}>
        <div
          style={{
            display: 'inline-block',
            maxWidth: 'calc(100% - 10px)',
            color: '#4D90FE',
            height: 22,
            borderWidth: '1px',
            borderStyle: 'solid',
            borderColor: '#4D90FE',
            borderRadius: '2px',
            fontSize: '13px',
            lineHeight: '20px',
            padding: '0 8px',  
            margin: '0 5px',   
          }}
          className="c7ntest-text-dot"
        >
          {folderName}
        </div>
      </Tooltip>
    ) : null
  );
}
export function renderComponents(components) {
  return (
    components.length > 0 ? (
      <div style={{ margin: '0 5px', color: '#3F51B5', fontWeight: 500 }}>
        {
          components.map(component => component.name).join(',')
        }
      </div>
    ) : null
  );
}
export function renderLabels(labels) {
  if (labels.length > 0) {
    return (
      <Tooltip title={labels.map(label => label.labelName).join(',')}>
        <Tag
          color="blue"
          style={{
            maxWidth: 160,
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'nowrap',
            verticalAlign: 'bottom',
          }}
        >
          {labels[0].labelName}
        </Tag>
        {labels.length > 1 && <Tag color="blue">...</Tag>}
      </Tooltip>
    );
  } else {
    return null;
  }
}
export function renderAssigned(assigneeId, assigneeName, imageUrl, hiddenText) {
  return (
    assigneeId ? (
      <Tooltip mouseEnterDelay={0.5} title={`任务经办人： ${assigneeName}`}>
        <div>
          <UserHead
            hiddenText={hiddenText}
            user={{
              id: assigneeId,
              loginName: '',
              realName: assigneeName,
              avatar: imageUrl,
            }}
          />
        </div>
      </Tooltip>
    ) : null
  );
}
export function renderReporter(reporterId, reporterName, reporterImageUrl, hiddenText) {
  return (
    reporterId ? (
      <Tooltip mouseEnterDelay={0.5} title={`任务报告人： ${reporterName}`}>
        <div>
          <UserHead
            hiddenText={hiddenText}
            user={{
              id: reporterId,
              loginName: '',
              realName: reporterName,
              avatar: reporterImageUrl,
            }}
          />
        </div>
      </Tooltip>
    ) : null
  );
}
export function renderStatus(statusMapDTO) {
  const { name: statusName } = statusMapDTO;
  return (
    <div style={{ margin: '0 5px' }}>
      <Tooltip mouseEnterDelay={0.5} title={`任务状态： ${statusName}`}>
        <div>
          <StatusTag
            status={statusMapDTO}
          />
        </div>
      </Tooltip>
    </div>
  );
}
