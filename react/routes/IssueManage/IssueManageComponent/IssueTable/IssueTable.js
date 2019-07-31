
import React, { Component } from 'react';
import _ from 'lodash';
import { observer } from 'mobx-react';
import { Spin, Table, Pagination } from 'choerodon-ui';
import { Droppable, DragDropContext } from 'react-beautiful-dnd';
import { FormattedMessage } from 'react-intl';
import EmptyBlock from '../EmptyBlock';
import CreateIssueTiny from '../CreateIssueTiny';
import IssueStore from '../../IssueManagestore/IssueStore';
import TableDraggleItem from './TableDraggleItem';
import {
  renderType, renderIssueNum, renderSummary, renderPriority, renderVersions, renderFolder,
  renderComponents, renderLabels, renderAssigned, renderStatus, renderReporter,
} from './tags';
import './IssueTable.scss';
import pic from '../../../../assets/testCaseEmpty.svg';

@observer
class IssueTable extends Component {
  state = {
    firstIndex: null,
    filteredColumns: ['issueNum', 'issueTypeVO', 'summary', 'versionIssueRelVOList', 'folderName', 'reporter', 'priorityId'],
  };


  handleColumnFilterChange = ({ selectedKeys }) => {
    this.setState({
      filteredColumns: selectedKeys,
    });
  }

  getComponents = columns => ({
    table: () => {
      const table = (
        <table>
          <thead>
            {this.renderThead(columns)}
          </thead>
          <Droppable droppableId="dropTable" isDropDisabled>
            {(provided, snapshot) => (
              <tbody
                ref={provided.innerRef}
              >
                {this.renderTbody(IssueStore.getIssues, columns)}
                {/* {provided.placeholder} */}
              </tbody>
            )}
          </Droppable>
        </table>
      );
      return (
        <DragDropContext onDragEnd={this.onDragEnd} onDragStart={this.onDragStart}>
          {table}
        </DragDropContext>
      );
    },
  })

  shouldColumnShow = (column) => {
    if (column.title === '' || !column.dataIndex) {
      return true;
    }
    const { filteredColumns } = this.state;
    return filteredColumns.length === 0 ? true : filteredColumns.includes(column.dataIndex);
  }

  renderThead = (columns) => {
    const Columns = columns.filter(column => this.shouldColumnShow(column));
    const ths = Columns.map(column => (
      <th style={{ flex: column.flex || 1 }}>
        {column.title}
        {' '}
      </th>
    ));
    return (<tr>{ths}</tr>);
  }

  renderTbody(data, columns) {
    const {
      disabled, onRow,
    } = this.props;
    const Columns = columns.filter(column => this.shouldColumnShow(column));
    const tds = index => Columns.map((column) => {
      let renderedItem = null;
      const {
        dataIndex, key, flex, render,
      } = column;
      if (render) {
        renderedItem = render(data[index][dataIndex], data[index], index);
      } else {
        renderedItem = data[index][dataIndex];
      }
      return (
        <td style={{ flex: flex || 1 }}>
          {renderedItem}
        </td>
      );
    });
    const rows = data.map((issue, index) => {
      if (disabled) {
        return tds(index);
      } else {
        return (
          // 由于drag结束后要经过一段时间，由于有动画，所以大约33-400ms后才执行onDragEnd,
          // 所以在这期间如果获取用例的接口速度很快，重新渲染table中的项，会无法执行onDragEnd,故加此key
          <TableDraggleItem key={`${issue.issueId}-${issue.objectVersionNumber}`} handleClickIssue={this.handleClickIssue.bind(this)} issue={issue} index={index} saveRef={(instance) => { this.instance = instance; }} onRow={onRow}>
            {tds(index)}
          </TableDraggleItem>
        );
      }
    });

    return rows;
  }

  renderNarrowIssue(issue) {
    const {
      issueId,
      issueTypeVO, issueNum, summary, assigneeId, assigneeName, assigneeImageUrl, reporterId,
      reporterName, reporterImageUrl, statusVO, priorityVO,
      folderName, epicColor, componentIssueRelDTOList, labelIssueRelDTOList,
      versionIssueRelVOList, creationDate, lastUpdateDate,
    } = issue;
    return (
      <div style={{ padding: '12px 16px', cursor: 'pointer', width: '100%' }}>
        <div style={{
          display: 'flex', marginBottom: '5px', width: '100%', flex: 1,
        }}
        >
          {renderType(issueTypeVO)}
          {renderIssueNum(issueNum)}
          <div className="c7ntest-flex-space" />
          {renderVersions(versionIssueRelVOList)}
          {renderFolder(folderName)}
          {renderReporter(reporterId, reporterName, reporterImageUrl, true)}
          {renderPriority(priorityVO)}
        </div>
        <div style={{ display: 'flex' }}>
          {renderSummary(summary)}
        </div>
      </div>
    );
  }

  onDragEnd = (result) => {
    IssueStore.setTableDraging(false);
    document.removeEventListener('keydown', this.enterCopy);
    document.removeEventListener('keyup', this.leaveCopy);
  }

  onDragStart = (monitor) => {
    const draggingTableItems = IssueStore.getDraggingTableItems;
    if (draggingTableItems.length < 1 || _.findIndex(draggingTableItems, { issueId: monitor.draggableId }) < 0) {
      const { index } = monitor.source;
      this.setState({
        firstIndex: index,
      });
      IssueStore.setDraggingTableItems([IssueStore.getIssues[index]]);
    }
    IssueStore.setTableDraging(true);
    document.addEventListener('keydown', this.enterCopy);
    document.addEventListener('keyup', this.leaveCopy);
  }

  enterCopy = (e) => {
    e.preventDefault();
    e.stopImmediatePropagation();
    if (e.keyCode === 17 || e.keyCode === 93 || e.keyCode === 91 || e.keyCode === 224) {
      const templateCopy = document.getElementById('template_copy').cloneNode(true);
      templateCopy.style.display = 'block';

      if (this.instance.firstElementChild) {
        this.instance.replaceChild(templateCopy, this.instance.firstElementChild);
      } else {
        this.instance.appendChild(templateCopy);
      }
    }
  }

  leaveCopy = (e) => {
    e.preventDefault();
    e.stopImmediatePropagation();
    const templateMove = document.getElementById('template_move').cloneNode(true);
    templateMove.style.display = 'block';

    if (this.instance.firstElementChild) {
      this.instance.replaceChild(templateMove, this.instance.firstElementChild);
    } else {
      this.instance.appendChild(templateMove);
    }
  }

  handleClickIssue(issue, index, e) {
    const { onRow } = this.props;
    const { firstIndex } = this.state;
    // console.log(e.shiftKey, e.ctrlKey, issue, index, firstIndex);
    if (e.shiftKey || e.ctrlKey || e.metaKey) {
      if (e.shiftKey) {
        if (firstIndex !== null) {
          const start = Math.min(firstIndex, index);
          const end = Math.max(firstIndex, index);
          //
          const draggingTableItems = IssueStore.getIssues.slice(start, end + 1);
          // console.log(draggingTableItems);
          IssueStore.setDraggingTableItems(draggingTableItems);
        }
      } else {
        // 是否已经选择
        const old = IssueStore.getDraggingTableItems;
        const hasSelected = _.findIndex(old, { issueId: issue.issueId });

        // 已选择就去除
        if (hasSelected >= 0) {
          old.splice(hasSelected, 1);
        } else {
          old.push(issue);
        }
        // console.log(hasSelected, old);
        IssueStore.setDraggingTableItems(old);
      }
    } else {
      IssueStore.setDraggingTableItems([]);
      onRow(issue).onClick();
    }
    this.setState({
      firstIndex: index,
    });
  }


  renderTable = columns => (
    <div className="c7ntest-issuetable">
      <Table
          // filterBar={false}
        columns={columns}
        dataSource={IssueStore.getIssues}
        components={this.getComponents(columns)}
        onChange={this.handleFilterChange}
        onColumnFilterChange={this.handleColumnFilterChange}
        pagination={false}
        filters={IssueStore.barFilters || []}
        filterBarPlaceholder={<FormattedMessage id="issue_filterTestIssue" />}
        empty={(
          <EmptyBlock
            style={{ marginTop: 40 }}
            border
            pic={pic}
            title={<FormattedMessage id="issue_noIssueTitle" />}
            des={<FormattedMessage id="issue_noIssueDescription" />}
          />
          )}
      />
    </div>
  )

  handleFilterChange = (pagination, filters, sorter, barFilters) => {
    // 条件变化返回第一页
    IssueStore.setPagination({
      current: 1,
      pageSize: IssueStore.pagination.pageSize,
      total: IssueStore.pagination.total,
    });
    IssueStore.setFilteredInfo(filters);
    IssueStore.setBarFilters(barFilters);
    // window.console.log(pagination, filters, sorter, barFilters[0]);
    if (barFilters === undefined || barFilters.length === 0) {
      IssueStore.setBarFilters(undefined);
    }

    const {
      statusId, priorityId, issueNum, summary, labelIssueRelDTOList,
    } = filters;
    const search = {
      advancedSearchArgs: {
        statusId: statusId || [],
        priorityId: priorityId || [],
      },
      otherArgs: {
        label: labelIssueRelDTOList || [],
        issueNum: issueNum && issueNum.length ? issueNum[0] : '',
        summary: summary && summary.length ? summary[0] : '',
      },
    };
    IssueStore.setFilter(search);
    const { current, pageSize } = IssueStore.pagination;
    IssueStore.loadIssues(current - 1, pageSize);
  }

  handlePaginationChange(page, pageSize) {
    IssueStore.loadIssues(page, pageSize);
  }

  handlePaginationShowSizeChange(current, size) {
    IssueStore.loadIssues(current, size);
  }

  manageVisible = columns => columns.map(column => (this.shouldColumnShow(column) ? { ...column, hidden: false } : { ...column, hidden: true }))


  render() {
    const prioritys = IssueStore.getPrioritys;
    const labels = IssueStore.getLabels;
    const issueStatusList = IssueStore.getIssueStatus;
    const columns = this.manageVisible([
      {
        title: '编号',
        dataIndex: 'issueNum',
        key: 'issueNum',
        filters: [],
        render: (issueNum, record) => renderIssueNum(issueNum),
      },
      {
        title: '类型',
        dataIndex: 'issueTypeVO',
        key: 'issueTypeVO',
        render: (issueTypeVO, record) => renderType(issueTypeVO, true),
      },
      {
        title: '概要',
        dataIndex: 'summary',
        key: 'summary',
        filters: [],
        render: (summary, record) => renderSummary(summary),
      },
      {
        title: '版本',
        dataIndex: 'versionIssueRelVOList',
        key: 'versionIssueRelVOList',
        render: (versionIssueRelVOList, record) => renderVersions(versionIssueRelVOList),
      },
      {
        title: '文件夹',
        dataIndex: 'folderName',
        key: 'folderName',
        render: (folderName, record) => renderFolder(folderName),
      },
      {
        title: '报告人',
        dataIndex: 'reporter',
        key: 'reporter',
        render: (assign, record) => {
          const {
            reporterId, reporterLoginName, reporterRealName, reporterImageUrl,
          } = record;
          return renderReporter(reporterId, reporterLoginName, reporterRealName, reporterImageUrl);
        },
      },
      {
        title: '优先级',
        dataIndex: 'priorityId',
        key: 'priorityId',
        filters: prioritys.map(priority => ({ text: priority.name, value: priority.id.toString() })),
        filterMultiple: true,
        render: (priorityId, record) => renderPriority(record.priorityVO),
      },
      {
        title: '经办人',
        dataIndex: 'assign',
        key: 'assign',
        render: (assign, record) => {
          const {
            assigneeId, assigneeLoginName, assigneeRealName, assigneeImageUrl,
          } = record;
          return renderAssigned(assigneeId, assigneeLoginName, assigneeRealName, assigneeImageUrl);
        },
      },
      {
        title: '状态',
        dataIndex: 'statusId',
        key: 'statusId',
        filters: issueStatusList.map(status => ({ text: status.name, value: status.id.toString() })),
        filterMultiple: true,
        render: (statusVO, record) => renderStatus(record.statusVO),
      },
      {
        title: '标签',
        dataIndex: 'labelIssueRelDTOList',
        key: 'labelIssueRelDTOList',
        filters: labels.map(label => ({ text: label.labelName, value: label.labelId.toString() })),
        filterMultiple: true,
        render: (labelIssueRelDTOList, record) => renderLabels(labelIssueRelDTOList),
      },
    ]);
    return (
      <div className="c7ntest-issueArea">
        <div id="template_copy" style={{ display: 'none' }}>
          {'当前状态：'}
          <span style={{ fontWeight: 500 }}>复制</span>
        </div>
        <div id="template_move" style={{ display: 'none' }}>
          {'当前状态：'}
          <span style={{ fontWeight: 500 }}>移动</span>
        </div>
        <section
          style={{
            boxSizing: 'border-box',
            width: '100%',
          }}
        >
          <Spin spinning={IssueStore.loading}>
            {this.renderTable(columns)}
          </Spin>

          <div className="c7ntest-backlog-sprintIssue">
            <div
              style={{
                userSelect: 'none',
                background: 'white',
                padding: '12px 0 12px 20px',
                fontSize: 13,
                display: 'flex',
                alignItems: 'center',
                borderBottom: '1px solid #e8e8e8',
              }}
            >
              {/* table底部创建用例 */}
              <CreateIssueTiny />
            </div>
          </div>
          {
            IssueStore.issues.length !== 0 ? (
              <div style={{
                display: 'flex', justifyContent: 'flex-end', marginTop: 16, marginBottom: 16,
              }}
              >
                <Pagination
                  current={IssueStore.pagination.current}
                  defaultCurrent={1}
                  defaultPageSize={10}
                  pageSize={IssueStore.pagination.pageSize}
                  showSizeChanger
                  total={IssueStore.pagination.total}
                  onChange={this.handlePaginationChange.bind(this)}
                  onShowSizeChange={this.handlePaginationShowSizeChange.bind(this)}
                />
              </div>
            ) : null
          }
        </section>
      </div>

    );
  }
}

IssueTable.propTypes = {

};

export default IssueTable;
