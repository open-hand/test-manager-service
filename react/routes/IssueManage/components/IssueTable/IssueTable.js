
import React, { useRef, useState, useEffect } from 'react';
import _ from 'lodash';
import { observer } from 'mobx-react-lite';
import {
  Spin, Table, Pagination, Icon, Tooltip,
} from 'choerodon-ui';
import { Droppable, DragDropContext } from 'react-beautiful-dnd';
import { FormattedMessage } from 'react-intl';
import EmptyBlock from '../EmptyBlock';
import CreateIssueTiny from '../CreateIssueTiny';
import IssueStore from '../../stores/IssueStore';
import TableDraggleItem from './TableDraggleItem';
import IssueTreeStore from '../../stores/IssueTreeStore';
import {
  renderIssueNum, renderSummary, renderAssigned, renderReporter, renderAction,
} from './tags';
import './IssueTable.less';
import pic from '../../../../assets/testCaseEmpty.svg';
import  useAvoidClosure from '@/hooks/index';

export default observer((props) => {
  const [firstIndex, setFirstIndex] = useState(null);
  const [filteredColumns, setFilteredColumns] = useState([]);
  const instance = useRef(null);

  const handleColumnFilterChange = ({ selectedKeys }) => {
    setFilteredColumns(selectedKeys);
  };

  const shouldColumnShow = (column) => {
    if (column.title === '' || !column.dataIndex) {
      return true;
    }
    return filteredColumns.length === 0 ? true : filteredColumns.includes(column.dataIndex);
  };

  const renderThead = (columns) => {
    const Columns = columns.filter(column => shouldColumnShow(column));
    const ths = Columns.map(column => (
      <th style={{ flex: column.flex || 1 }}>
        {column.title}
        {' '}
      </th>
    ));
    return (<tr>{ths}</tr>);
  };

  const handleClickIssue = (issue, index, e) => {
    const { onRow } = props;
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
      // onRow(issue).onClick();
    }
    setFirstIndex(index);
  };


  const renderTbody = (data, columns) => {
    const {
      disabled, onRow, clickIssue,
    } = props;
    const Columns = columns.filter(column => shouldColumnShow(column));
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
          <TableDraggleItem key={`${issue.issueId}-${issue.objectVersionNumber}`} clickIssue={clickIssue} handleClickIssue={handleClickIssue.bind(this)} issue={issue} index={index} ref={instance} onRow={onRow}>
            {tds(index)}
          </TableDraggleItem>
        );
      }
    });

    return rows;
  };

  const enterCopy = (e) => {
    e.preventDefault();
    e.stopImmediatePropagation();
    if (e.keyCode === 17 || e.keyCode === 93 || e.keyCode === 91 || e.keyCode === 224) {
      const templateCopy = document.getElementById('template_copy').cloneNode(true);
      templateCopy.style.display = 'block';

      if (instance.current.firstElementChild) {
        instance.current.replaceChild(templateCopy, instance.current.firstElementChild);
      } else {
        instance.current.appendChild(templateCopy);
      }
    }
  };

  const leaveCopy = (e) => {
    e.preventDefault();
    e.stopImmediatePropagation();
    const templateMove = document.getElementById('template_move').cloneNode(true);
    templateMove.style.display = 'block';

    if (instance.current.firstElementChild) {
      instance.current.replaceChild(templateMove, instance.current.firstElementChild);
    } else {
      instance.current.appendChild(templateMove);
    }
  };

  const onDragEnd = () => {
    IssueStore.setTableDraging(false);
    document.removeEventListener('keydown', enterCopy);
    document.removeEventListener('keyup', leaveCopy);
  };

  const onDragStart = (monitor) => {
    const draggingTableItems = IssueStore.getDraggingTableItems;
    if (draggingTableItems.length < 1 || _.findIndex(draggingTableItems, { issueId: monitor.draggableId }) < 0) {
      const { index } = monitor.source;
      setFirstIndex(index);
      IssueStore.setDraggingTableItems([IssueStore.getIssues[index]]);
    }
    IssueStore.setTableDraging(true);
    document.addEventListener('keydown', enterCopy);
    document.addEventListener('keyup', leaveCopy);
  };

  const getComponents =  useAvoidClosure(columns => ({
    table: () => {
      const table = (
        <table>
          <thead>
            {renderThead(columns)}
          </thead>
          <Droppable droppableId="dropTable" isDropDisabled>
            {(provided, snapshot) => (
              <tbody
                ref={provided.innerRef}
              >
                {renderTbody(IssueStore.getIssues, columns)}
                {/* {provided.placeholder} */}
              </tbody>
            )}
          </Droppable>
        </table>
      );
      return (
        <DragDropContext onDragEnd={onDragEnd} onDragStart={onDragStart}>
          {table}
        </DragDropContext>
      );
    },
  }));

  const handleFilterChange = (pagination, filters, sorter, barFilters) => {
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
      statusId, priorityId, issueNum, summary,
      labelIssueRelVOList, versionIssueRelVOList, componentIssueRelVOList,
    } = filters;
    const search = {
      advancedSearchArgs: {
        statusId: statusId || [],
        priorityId: priorityId || [],
      },
      otherArgs: {
        componentIds: componentIssueRelVOList || [],
        label: labelIssueRelVOList || [],
        issueNum: issueNum && issueNum.length ? issueNum[0] : '',
        summary: summary && summary.length ? summary[0] : '',
        versionStatusCode: versionIssueRelVOList && versionIssueRelVOList.length ? versionIssueRelVOList[0] : '',
      },
    };
    IssueStore.setFilter(search);
    const { current, pageSize } = IssueStore.pagination;
    IssueStore.loadIssues(current - 1, pageSize);
  };

  const renderTable = columns => (
    <div className="c7ntest-issuetable">
      <Table
        // filterBar={false}
        columns={columns}
        dataSource={IssueStore.getIssues}
        components={getComponents(columns)}
        onChange={handleFilterChange}
        onColumnFilterChange={handleColumnFilterChange}
        pagination={false}
        filters={IssueStore.barFilters || []}
        filterBarPlaceholder={<FormattedMessage id="issue_filterTestIssue" />}
        empty={!IssueStore.loading && (
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
  );
  

  const handlePaginationChange = (page, pageSize) => {
    IssueStore.loadIssues(page, pageSize);
  };

  const handlePaginationShowSizeChange = (current, size) => {
    IssueStore.loadIssues(current, size);
  };

  const manageVisible = columns => columns.map(column => (shouldColumnShow(column) ? { ...column, hidden: false } : { ...column, hidden: true }));

  const reLoadTable = () => {
    IssueStore.loadIssues();
  };


  const { onClick, history } = props;
  const columns = manageVisible([
    {
      title: '用例名称',
      dataIndex: 'summary',
      key: 'summary',
      filters: [],
      width: 400,
      render: (summary, record) => renderSummary(summary, record, onClick, history),
    },
    {
      key: 'action',
      width: 30,
      render: (text, record) => renderAction(record, history, reLoadTable),
    },
    {
      title: '用例编号',
      dataIndex: 'issueNum',
      key: 'issueNum',
      filters: [],
      render: (issueNum, record) => renderIssueNum(issueNum),
    },
    {
      title: '创建人',
      dataIndex: 'reporter',
      key: 'reporter',
      render: (assign, record) => {
        const {
          reporterId, reporterName, reporterLoginName, reporterRealName, reporterImageUrl,
        } = record;
        return renderReporter(reporterId, reporterName, reporterLoginName, reporterRealName, reporterImageUrl);
      },
    },
    {
      title: '创建时间',
      dataIndex: 'creationDate',
      key: 'creationDate',
      render: (creationDate, record) => <Tooltip title={creationDate}><span>{creationDate}</span></Tooltip>,
    },
    {
      title: '更新人',
      dataIndex: 'assigneeId',
      key: 'assigneeId',
      render: (assign, record) => {
        const {
          assigneeId, assigneeName, assigneeLoginName, assigneeRealName, assigneeImageUrl,
        } = record;
        return renderAssigned(assigneeId, assigneeName, assigneeLoginName, assigneeRealName, assigneeImageUrl);
      },
    },
    {
      title: '更新时间',
      dataIndex: 'lastUpdateDate',
      key: 'lastUpdateDate',
      render: (lastUpdateDate, record) => <Tooltip title={lastUpdateDate}><span>{lastUpdateDate}</span></Tooltip>,
    },
  ]);

  const { currentCycle } = IssueTreeStore;
  console.log(currentCycle);

  return (
    <div className="c7ntest-issueArea">
      <div id="template_copy" style={{ display: 'none' }}>
          当前状态：
        <span style={{ fontWeight: 500 }}>复制</span>
      </div>
      <div id="template_move" style={{ display: 'none' }}>
          当前状态：
        <span style={{ fontWeight: 500 }}>移动</span>
      </div>
      <section
        style={{
          boxSizing: 'border-box',
          width: '100%',
        }}
      >
        <Spin spinning={IssueStore.loading}>
          {renderTable(columns)}
        </Spin>

        <div
          className="c7ntest-backlog-sprintIssue"
          role="button"
        >
          <div
            style={{
              userSelect: 'none',
              background: 'white',
              padding: '12px 0 12px 12px',
              fontSize: 13,
              display: 'flex',
              alignItems: 'center',
              borderBottom: '1px solid #e8e8e8',
            }}
          >
            {/* table底部创建用例 */}
            {
              currentCycle && currentCycle.children && currentCycle.children.length === 0 ? <CreateIssueTiny /> : null
            }
          </div>
        </div>
        {
            IssueStore.issues.length !== 0 ? (
              <div style={{
                display: 'flex', justifyContent: 'flex-end', marginBottom: 16,
              }}
              >
                <Pagination
                  current={IssueStore.pagination.current}
                  defaultCurrent={1}
                  defaultPageSize={10}
                  pageSize={IssueStore.pagination.pageSize}
                  showSizeChanger
                  total={IssueStore.pagination.total}
                  onChange={handlePaginationChange.bind(this)}
                  onShowSizeChange={handlePaginationShowSizeChange.bind(this)}
                />
              </div>
            ) : null
          }
      </section>
    </div>
  );
});
