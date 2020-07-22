/*eslint-disable */
import React, { Component } from 'react';
import { Icon, Popconfirm, Popover } from 'choerodon-ui';
import _ from 'lodash';
import UserHead from '@/components/UserHead';
import DatetimeAgo from '../../../../../components/DateTimeAgo';
import './DataLog.scss';

const PROP = {
  summary: '问题概要',
  labels: '标签',
  IssueNum: '测试用例',
  'Folder Link': '文件夹'
};
const PROP_SIMPLE = {
  issue_test: '测试用例',
  issue_auto_test: '自动化测试用例',
  description: '描述',
  Attachment: '附件',
};

class DataLog extends Component {
  constructor(props, context) {
    super(props, context);
    this.state = {
    };
  }

  getMode1(datalog) {
    const {
      field, oldString, oldValue, newString, newValue, categoryCode,
    } = datalog;
    if ((!oldValue && oldValue !== 0) && (newValue || newValue === 0)) {
      // null -> xxx
      if (['labels', 'IssueNum'].includes(field)) {
        return '添加';
      }
      if (['Attachment'].includes(field)) {
        return '上传';
      }
      return '更新';
    } else if ((oldValue || oldValue === 0) && (newValue || newValue === 0)) {
      // xxx -> yyy
      if (['summary',].includes(field)) {
        return '将';
      }
      if (['description',].includes(field)) {
        return '更新';
      }
      // 自定义字段
      if (field && field.isCusLog) {
        return '将';
      }
      if (field === 'Folder Link') {
        return '移动'
      }
    } else if ((oldValue || oldValue === 0) && (!newValue && newValue !== 0)) {
      // yyy -> null
      if (['labels'].includes(field)) {
        return '移除';
      }
      if (['Attachment'].includes(field)) {
        if (oldString && !newString) {
          return '删除';
        }
      }
      // 自定义字段
      if (field && field.isCusLog) {
        return '移除';
      }
    } else {
      // 当字段为文本或数字时，oldValue和newValue均为null
      // null -> null
      if (field === 'description') {
        if (oldString && !newString) {
          return '移除';
        }
        return '更新';
      }
      if (field === 'summary') {
        return '将';
      }
      if (field === 'labels' || field === 'labels') {
        if (!oldString && newString) {
          return '创建';
        }
        return '移除';
      }
      // 自定义字段
      if (field && field.isCusLog) {
        return '将';
      }
      return '';
    }
  }

  getMode2(datalog) {
    const { field, fieldName } = datalog;
    // 自定义字段
    if (field && field.isCusLog) {
      return ` 【${fieldName}】 `;
    }
    if (field === 'status') {
      return '';
    }
    return ` 【${PROP[field] || PROP_SIMPLE[field]}】 `;
  }

  // ['由', '']
  getMode3(datalog) {
    const {
      field, oldString, oldValue, newString, newValue,
    } = datalog;
    if ((!oldValue && oldValue !== 0) && (newValue || newValue === 0)) {
      // null -> xxx
      return '';
    } else if ((oldValue || oldValue === 0) && (newValue || newValue === 0)) {
      // xxx -> yyy
      // 自定义字段
      if (field && field.isCusLog) {
        return '由';
      }
      if (['summary',].includes(field)) {
        return '由';
      } else {
        return '';
      }
    } else if ((oldValue || oldValue === 0) && (!newValue && newValue !== 0)) {
      return '';
    } else {
      if (field === 'summary') {
        return '由';
      }
      if (field === 'labels') {
        return '';
      }
      // 自定义字段
      if (field && field.isCusLog) {
        return '由';
      }
      return '';
    }
  }

  // 原值，只有移除和修改可能出现
  getMode4(datalog) {
    const {
      field, oldString, oldValue, newString, newValue,
    } = datalog;
    if ((!oldValue && oldValue !== 0) && (newValue || newValue === 0)) {
      // null -> xxx
      return '';
    } else if ((oldValue || oldValue === 0) && (newValue || newValue === 0)) {
      // xxx -> yyy
      if (['summary',].includes(field)) {
        return ` 【${oldString}】 `;
      }
      if (['description'].includes(field)) {
        return '';
      }
      // 自定义字段
      if (field && field.isCusLog) {
        return ` 【${oldString}】 `;
      }
    } else if ((oldValue || oldValue === 0) && (!newValue && newValue !== 0)) {
      // yyy -> null
      if (field === 'Attachment') {
        const attachnewArr = oldString.split('@');
        return ` 【${decodeURI(attachnewArr.slice(1, attachnewArr.length).join('_'))}】 `;
      } else {
        return ` 【${oldString}】 `;
      }
    } else {
      if (field === 'summary') {
        return ` 【${oldString}】 `;
      }
      if (field === 'labels') {
        if (!oldString && newString) {
          return '';
        }
        return ` 【${oldString}】 `;
      }
      // 自定义字段
      if (field && field.isCusLog) {
        return oldString ? ` 【${oldString}】 ` : ' 空 ';
      }
      return '';
    }
  }

  // ['改变为', '为', '']
  getMode5(datalog) {
    const {
      field, oldString, oldValue, newString, newValue,
    } = datalog;
    if ((!oldValue && oldValue !== 0) && (newValue || newValue === 0)) {
      // 自定义字段
      if (field && field.isCusLog) {
        return '为';
      }
      return '';
    } else if ((oldValue || oldValue === 0) && (newValue || newValue === 0)) {
      // xxx -> yyy
      if (['summary',].includes(field)) {
        return '改变为';
      }
      // 自定义字段
      if (field && field.isCusLog) {
        return '改变为';
      }
      return '';
    } else if ((oldValue || oldValue === 0) && (!newValue && newValue !== 0)) {
      return '';
    } else {
      if (field === 'summary') {
        return '改变为';
      }
      // 自定义字段
      if (field && field.isCusLog) {
        return '改变为';
      }
      return '';
    }
  }

  // 新值，只有新增和修改可能出现
  getMode6(datalog) {
    const {
      field, oldString, oldValue, newString, newValue,
    } = datalog;
    if ((!oldValue && oldValue !== 0) && (newValue || newValue === 0)) {
      // null -> xxx
      if (['summary'].includes(field)) {
        return ` 【${newString}】 `;
      }
      if (['description'].includes(field)) {
        return '';
      }
      if (['labels'].includes(field)) {
        return ` 【${newString}】 `;
      }
      if (field === 'Attachment') {
        const attachnewArr = newString.split('@');
        return ` 【${decodeURI(attachnewArr.slice(1, attachnewArr.length).join('_'))}】 `;
      }
      // 自定义字段
      if (field && field.isCusLog) {
        return ` 【${newString}】 `;
      }
    } else if ((oldValue || oldValue === 0) && (newValue || newValue === 0)) {
      // xxx -> yyy
      if (['summary', 'labels'].includes(field)) {
        return ` 【${newString}】 `;
      }
      if (['description', 'Attachment'].includes(field)) {
        return '';
      }
      if (field === 'status') {
        return '';
      }
      // 自定义字段
      if (field && field.isCusLog) {
        return ` 【${newString}】 `;
      }
    } else if ((oldValue || oldValue === 0) && (!newValue && newValue !== 0)) {
      // yyy -> null
      return '';
    } else {
      if (field === 'summary') {
        return ` 【${newString}】 `;
      }
      if (field === 'labels') {
        if (!oldString && newString) {
          return ` 【${newString}】 `;
        }
        return '';
      }
      // 自定义字段
      if (field && field.isCusLog) {
        return newString ? ` 【${newString}】 ` : ' 空 ';
      }
    }
  }

  getFirst(str) {
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

  renderOperator = (datalog) => {
    const content = <span style={{ color: '#303f9f' }}>
      {`${datalog.realName || '系统'} `}
    </span>
    if (datalog.realName) {
      return <Popover
        placement="bottomLeft"
        content={(
          <div style={{ padding: '5px 2px 0' }}>
            <div
              style={{
                width: 62,
                height: 62,
                background: '#c5cbe8',
                color: '#6473c3',
                overflow: 'hidden',
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                textAlign: 'center',
                borderRadius: '50%',
                fontSize: '28px',
                margin: '0 auto',
              }}
            >
              {
                datalog.imageUrl ? (
                  <img src={datalog.imageUrl} alt="" style={{ width: '100%' }} />
                ) : (
                    <span style={{
                      width: 62, height: 62, lineHeight: '62px', textAlign: 'center', color: '#6473c3',
                    }}
                    >
                      {this.getFirst(datalog.realName)}
                    </span>
                  )
              }
            </div>
            <h1 style={{
              margin: '8px auto 18px', fontSize: '13px', lineHeight: '20px', textAlign: 'center',
            }}
            >
              {datalog.realName}
            </h1>
            <div style={{
              color: 'rgba(0, 0, 0, 0.65)', fontSize: '13px', textAlign: 'center', display: 'flex',
            }}
            >
              <Icon type="markunread" style={{ lineHeight: '20px' }} />
              <span style={{
                marginLeft: 6, lineHeight: '20px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap',
              }}
              >
                {datalog.email}
              </span>
            </div>
          </div>
        )}
      >
        {content}
      </Popover>
    }
    return content;
  }
  render() {
    const {
      datalog, i, origin, expand,
    } = this.props;
    return (
      <div>
        {
          i > 4 && !expand ? null : (
            <div className="c7ntest-datalog" key={datalog.logId}>
              <div className="line-justify">
                {/* 头像 */}
                <div className="c7ntest-title-log" style={{ flexShrink: 0 }}>
                  <div
                    style={{
                      width: 40,
                      height: 40,
                      marginRight: 15,
                    }}
                  >
                    {
                      i && origin[i].lastUpdatedBy === origin[i - 1].lastUpdatedBy ? null : (
                        <UserHead
                          user={{
                            id: datalog.lastUpdatedBy,
                            name: datalog.name,
                            loginName: datalog.loginName,
                            realName: datalog.realName,
                            avatar: datalog.imageUrl,
                          }}
                          hiddenText
                          type="datalog"
                        />
                      )
                    }

                  </div>
                </div>
                <div style={{ flex: 1, borderBottom: '1px solid rgba(0, 0, 0, 0.12)', padding: '8.5px 0' }}>
                  <div>
                    {this.renderOperator(datalog)}
                    <div style={{ display: 'inline' }}>
                      <span>
                        {/* 操作 */}
                        {this.getMode1(datalog)}
                      </span>
                      <span style={{ color: '#303f9f', wordBreak: 'break-all' }}>
                        {/* 字段 */}
                        {this.getMode2(datalog)}
                      </span>
                      <span>
                        {/* 由 */}
                        {this.getMode3(datalog)}
                      </span>
                      <span style={{ color: '#303f9f', wordBreak: 'break-all' }}>
                        {/* oldValue */}
                        {this.getMode4(datalog)}
                      </span>
                      <span>
                        {/* 改变/为 */}
                        {this.getMode5(datalog)}
                      </span>
                      <span style={{ color: '#303f9f', wordBreak: 'break-all' }}>
                        {/* newValue */}
                        {this.getMode6(datalog)}
                      </span>
                    </div>

                  </div>
                  <div style={{ marginTop: 5, fontSize: '12px' }}>
                    <DatetimeAgo
                      date={datalog.lastUpdateDate}
                    />
                  </div>
                </div>
              </div>
            </div>
          )
        }
      </div>
    );
  }
}

export default DataLog;
