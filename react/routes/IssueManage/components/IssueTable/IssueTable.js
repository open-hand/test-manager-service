
import React, { useRef, useState, useCallback } from 'react';
import _ from 'lodash';
import { toJS } from 'mobx';
import { observer } from 'mobx-react-lite';
import {
  Spin, Table, Pagination, Tooltip, Icon,
} from 'choerodon-ui';
import { Droppable, DragDropContext } from 'react-beautiful-dnd';
import UserHead from '@/components/UserHead';
import useAvoidClosure from '@/hooks/useAvoidClosure';
import CreateIssueTiny from '../CreateIssueTiny';
import IssueStore from '../../stores/IssueStore';
import TableDraggleItem from './TableDraggleItem';
import IssueTreeStore from '../../stores/IssueTreeStore';
import { getTask } from '../IssueTree/TreeNode';
import {
  renderIssueNum, renderSummary, renderAction,
} from './tags';
import './IssueTable.less';
import PriorityTag from '../../../../components/PriorityTag';


export default observer((props) => {
  const [firstIndex, setFirstIndex] = useState(null);
  const [filteredColumns, setFilteredColumns] = useState([]);
  const instance = useRef();

  const handleColumnFilterChange = ({ selectedKeys }) => {
    setFilteredColumns(selectedKeys);
  };

  const shouldColumnShow = useAvoidClosure((column) => {
    if (column.title === '' || !column.dataIndex) {
      return true;
    }
    return filteredColumns.length === 0 ? true : filteredColumns.includes(column.dataIndex);
  });

  const handleSortByField = (key) => {
    let orderType = 'ASC';
    if (IssueStore.order.orderField === key && IssueStore.order.orderType === 'ASC') {
      orderType = 'DESC';
    }
    IssueStore.setOrder({
      orderField: key,
      orderType,
    });
    IssueStore.loadIssues();
  };
  const renderThead = (columns) => {
    const Columns = columns.filter(column => shouldColumnShow(column));
    const ths = Columns.map(column => (
      // <th style={{ flex: column.flex || 1 }} >
      <th
        className={IssueStore.order.orderField === column.key && `c7ntest-issuetable-sorter-${IssueStore.order.orderType}`}
        key={column.key}
        style={{ width: column.width, flex: column.width ? 'unset' : (column.flex || 1) }}
        onClick={column.sorter && handleSortByField.bind(this, column.key)}
      >
        <span>{column.title}</span>
        {column.sorter && <Icon type="arrow_upward" className="c7ntest-issuetable-sorter-icon" />}
        {' '}
      </th>
    ));
    return (<tr>{ths}</tr>);
  };

  const handleClickIssue = useAvoidClosure((issue, index, e) => {
    if (e.shiftKey || e.ctrlKey || e.metaKey) {
      if (e.shiftKey) {
        if (firstIndex !== null) {
          const start = Math.min(firstIndex, index);
          const end = Math.max(firstIndex, index);
          //
          const draggingTableItems = IssueStore.getIssues.slice(start, end + 1);
          IssueStore.setDraggingTableItems(draggingTableItems);
        }
      } else {
        // 是否已经选择
        const old = IssueStore.getDraggingTableItems;
        const hasSelected = _.findIndex(old, { caseId: issue.caseId });

        // 已选择就去除
        if (hasSelected >= 0) {
          old.splice(hasSelected, 1);
        } else {
          old.push(issue);
        }
        IssueStore.setDraggingTableItems(old);
      }
    } else {
      IssueStore.setDraggingTableItems([]);
      // onRow(issue).onClick();
    }
    setFirstIndex(index);
  });


  const renderTbody = (data, columns) => {
    const {
      disabled, onRow,
    } = props;
    const Columns = columns.filter(column => shouldColumnShow(column));
    const tds = index => Columns.map((column) => {
      let renderedItem = null;
      const {
        dataIndex, render,
      } = column;
      if (render) {
        renderedItem = render(data[index][dataIndex], data[index], index);
      } else {
        renderedItem = data[index][dataIndex];
      }
      return (
        // <td style={{ flex: flex || 1 }} >
        <td key={column.key} style={{ width: column.width, flex: column.width ? 'unset' : (column.flex || 1) }}>
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
          <TableDraggleItem key={`${issue.caseId}-${issue.objectVersionNumber}`} handleClickIssue={handleClickIssue.bind(this)} issue={issue} index={index} instanceRef={instance} onRow={onRow}>
            {tds(index)}
          </TableDraggleItem>
        );
      }
    });

    return rows;
  };

  const enterCopy = useCallback((e) => {
    e.preventDefault();
    e.stopImmediatePropagation();
    if (e.keyCode === 17 || e.keyCode === 93 || e.keyCode === 91 || e.keyCode === 224) {
      const templateCopy = document.getElementById('template_copy').cloneNode(true);
      templateCopy.style.display = 'block';
      if (instance.current && instance.current.firstElementChild) {
        instance.current.replaceChild(templateCopy, instance.current.firstElementChild);
      } else {
        instance.current.appendChild(templateCopy);
      }
    }
  }, []);

  const leaveCopy = useCallback((e) => {
    e.preventDefault();
    e.stopImmediatePropagation();
    const templateMove = document.getElementById('template_move').cloneNode(true);
    templateMove.style.display = 'block';

    if (instance.current && instance.current.firstElementChild) {
      instance.current.replaceChild(templateMove, instance.current.firstElementChild);
    } else {
      instance.current.appendChild(templateMove);
    }
  }, []);

  const onDragEnd = useAvoidClosure(() => {
    const task = getTask();
    if (task) {
      task();
    }
    IssueStore.setTableDraging(false);
    document.removeEventListener('keydown', enterCopy);
    document.removeEventListener('keyup', leaveCopy);
  });

  const onDragStart = useAvoidClosure((monitor) => {
    const draggingTableItems = IssueStore.getDraggingTableItems;
    if (draggingTableItems.length < 1 || _.findIndex(draggingTableItems, { caseId: monitor.draggableId }) < 0) {
      const { index } = monitor.source;
      setFirstIndex(index);
      IssueStore.setDraggingTableItems([IssueStore.getIssues[index]]);
    }
    IssueStore.setTableDraging(true);
    IssueStore.setClickIssue({});
    document.addEventListener('keydown', enterCopy);
    document.addEventListener('keyup', leaveCopy);
  });

  const getComponents = useAvoidClosure(columns => ({
    table: () => {
      const table = (
        <table>
          <thead>
            {renderThead(columns)}
          </thead>
          <Droppable droppableId="dropTable" isDropDisabled>
            {provided => (
              <tbody
                ref={provided.innerRef}
              >
                {renderTbody(IssueStore.getIssues || [], columns)}
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

  const transformFilters = (filters) => {
    const transformedFilters = Object.entries(filters).filter(item => item[1].length > 0);
    const res = {};
    transformedFilters.forEach((item) => {
      if (item[0] === 'summary') {
        // eslint-disable-next-line prefer-destructuring
        res.summary = item[1][0];
      } else if (item[0] === 'caseNum') {
        // eslint-disable-next-line prefer-destructuring
        res.caseNum = item[1][0];
      }
    });
    return res;
  };

  const handleFilterChange = (pagination, filters, sorter, barFilters) => {
    // 条件变化返回第一页
    IssueStore.setPagination({
      current: 1,
      pageSize: IssueStore.pagination.pageSize,
      total: IssueStore.pagination.total,
    });
    IssueStore.setFilter({ searchArgs: transformFilters(filters) });
    IssueStore.setBarFilters(barFilters);
    // window.console.log(pagination, filters, sorter, barFilters[0]);
    if (barFilters === undefined || barFilters.length === 0) {
      IssueStore.setBarFilters(undefined);
    }
    const { current, pageSize } = IssueStore.pagination;
    IssueStore.loadIssues(current - 1, pageSize);
  };

  const renderTable = columns => (
    <div className="c7ntest-issuetable">
      <Table
        // filterBar={false}
        rowKey="caseId"
        columns={columns}
        dataSource={IssueStore.getIssues}
        components={getComponents(columns)}
        onChange={handleFilterChange}
        onColumnFilterChange={handleColumnFilterChange}
        pagination={false}
        filters={IssueStore.getBarFilters || []}
        filterBarPlaceholder="过滤表"
        noFilter
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
      render: (summary, record) => renderSummary(summary, record, onClick, history),
    },
    {
      key: 'action',
      render: (text, record) => renderAction(record, history, reLoadTable),
      width: '0.6rem',
    },

    {
      title: '用例编号',
      dataIndex: 'caseNum',
      key: 'sourceCaseNum',
      sorter: true,
      filters: [],
      render: caseNum => renderIssueNum(caseNum),
    },
    {
      title: '优先级',
      dataIndex: 'priorityId',
      key: 'sequence',
      sorter: true,
      width: '1rem',
      render: (priorityId, record) => priorityId && <PriorityTag priority={record.priorityVO} />,
    },
    {
      title: '创建人',
      dataIndex: 'createUser',
      key: 'createUser',
      render: createUser => createUser && <UserHead user={createUser} />,
      width: '1rem',
    },
    {
      title: '创建时间',
      dataIndex: 'creationDate',
      key: 'creationDate',
      sorter: true,
      render: creationDate => <Tooltip title={creationDate}><span style={{ color: 'rgba(0, 0, 0, 0.65)' }}>{creationDate}</span></Tooltip>,
    },
    {
      title: '更新人',
      dataIndex: 'lastUpdateUser',
      key: 'lastUpdateUser',
      render: lastUpdateUser => lastUpdateUser && <UserHead user={lastUpdateUser} />,
      width: '1rem',
    },
    {
      title: '更新时间',
      dataIndex: 'lastUpdateDate',
      key: 'lastUpdateDate',
      sorter: true,
      render: lastUpdateDate => <Tooltip title={lastUpdateDate}><span style={{ color: 'rgba(0, 0, 0, 0.65)' }}>{lastUpdateDate}</span></Tooltip>,
    },
  ]);

  const { currentFolder } = IssueTreeStore;

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
              padding: '16px 0 12px 0',
              fontSize: 13,
              display: 'flex',
              alignItems: 'center',
            }}
          >
            {
              currentFolder && currentFolder.children && currentFolder.children.length === 0 ? <CreateIssueTiny key={currentFolder.id} /> : null
            }
          </div>
        </div>
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
      </section>
    </div>
  );
});
