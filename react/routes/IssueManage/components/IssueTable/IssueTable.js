/* eslint-disable react/jsx-no-bind */
import React, {
  useRef, useEffect, useState, useCallback,
} from 'react';
import { findIndex, set } from 'lodash';
import { toJS } from 'mobx';
import { observer } from 'mobx-react-lite';
import {
  Table, Pagination, Tooltip, Icon,
} from 'choerodon-ui';
import { Droppable, DragDropContext } from 'react-beautiful-dnd';
import { localPageCacheStore } from '@choerodon/agile/lib/stores/common/LocalPageCacheStore';
import Loading from '@choerodon/agile/lib/components/Loading';
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
import CustomCheckBox from '@/components/CustomCheckBox';
import useFormatMessage from '@/hooks/useFormatMessage';

const CHECKBOX_KEY = 'checkbox';

export default observer((props) => {
  const formatMessage = useFormatMessage();

  const [firstIndex, setFirstIndex] = useState(null);
  const [filteredColumns, setFilteredColumns] = useState([]);
  const instance = useRef();
  const tableRef = useRef();
  const handleColumnFilterChange = ({ selectedKeys }) => {
    setFilteredColumns(selectedKeys);
  };
  const transformFilters = (filters, reverse = false) => {
    const transformedFilters = Object.entries(filters).filter((item) => item[1].length > 0);
    const filterRules = [{ origin: 'sequence', to: 'priorityId' }, { origin: 'caseId', to: 'caseNum' }];
    const maps = new Map(filterRules.map((item) => (reverse ? [item.to, item.origin] : [item.origin, item.to])));
    const res = {};

    transformedFilters.forEach((item) => {
      let key = item[0];
      if (maps.has(item[0])) {
        key = maps.get(item[0]);
      }
      set(res, key, reverse ? [item[1]] : item[1][0]);
    });
    return res;
  };
  const handleSaveTableRef = useCallback((r) => {
    const { filter } = localPageCacheStore.getItem('issueManage.table') || {};
    if (r && r.state && filter) {
      let { content, searchArgs } = filter;
      content = toJS(content);
      searchArgs = toJS(searchArgs);
      if (content && Array.isArray(content)) {
        r.state.barFilters.push(...content);
      }
      if (searchArgs && Object.keys(searchArgs).length > 0) {
        const newSearchArgs = transformFilters(searchArgs, true);
        for (const key in newSearchArgs) {
          if (Object.prototype.hasOwnProperty.call(newSearchArgs, key) && newSearchArgs[key]) {
            set(r.state, `filters.${key}`, newSearchArgs[key]);
          }
        }
      }
    }
    // set(tableRef.current.state.filters, 'summary', ['59']);
  }, []);
  const shouldColumnShow = useAvoidClosure((column) => {
    if (column.title === '' || !column.dataIndex) {
      return true;
    }
    return filteredColumns.length === 0 ? true : filteredColumns.includes(column.key);
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
    const Columns = columns.filter((column) => shouldColumnShow(column));
    const ths = Columns.map((column) => (
      // <th style={{ flex: column.flex || 1 }} >
      <th
        className={IssueStore.order.orderField === column.key && `c7ntest-issuetable-sorter-${IssueStore.order.orderType}`}
        key={column.key}
        style={{ width: column.width, flex: column.width ? 'unset' : (column.flex || 1) }}
        onClick={column.sorter && handleSortByField.bind(this, column.key)}
      >
        {column.key === CHECKBOX_KEY && !column.title ? (
          <CustomCheckBox
            value="all"
            checkedMap={IssueStore.checkIdMap}
            dataSource={IssueStore.getIssues}
            field="caseId"
          />
        ) : (
          <>
            <span>{column.title}</span>
            {column.sorter && <Icon type="arrow_upward" className="c7ntest-issuetable-sorter-icon" />}
            {' '}
          </>
        )}
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
        const hasSelected = findIndex(old, { caseId: issue.caseId });

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
    const Columns = columns.filter((column) => shouldColumnShow(column));
    const tds = (index) => Columns.map((column) => {
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
      }
      return (
        // 由于drag结束后要经过一段时间，由于有动画，所以大约33-400ms后才执行onDragEnd,
        // 所以在这期间如果获取用例的接口速度很快，重新渲染table中的项，会无法执行onDragEnd,故加此key
        <TableDraggleItem key={`${issue.caseId}-${issue.objectVersionNumber}`} handleClickIssue={handleClickIssue.bind(this)} issue={issue} index={index} instanceRef={instance} onRow={onRow}>
          {tds(index)}
        </TableDraggleItem>
      );
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
    if (draggingTableItems.length < 1 || findIndex(draggingTableItems, { caseId: monitor.draggableId }) < 0) {
      const { index } = monitor.source;
      setFirstIndex(index);
      IssueStore.setDraggingTableItems([IssueStore.getIssues[index]]);
    }
    IssueStore.setTableDraging(true);
    IssueStore.setClickIssue({});
    document.addEventListener('keydown', enterCopy);
    document.addEventListener('keyup', leaveCopy);
  });

  const getComponents = useAvoidClosure((columns) => ({
    table: () => {
      const table = (
        <table>
          <thead>
            {renderThead(columns)}
          </thead>
          <Droppable droppableId="dropTable" isDropDisabled>
            {(provided) => (
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

  const renderTable = (columns) => (
    <div className="c7ntest-issuetable">
      <Table
        // filterBar={false}
        ref={handleSaveTableRef}
        rowKey="caseId"
        columns={columns}
        dataSource={IssueStore.getIssues}
        components={getComponents(columns)}
        onChange={handleFilterChange}
        onColumnFilterChange={handleColumnFilterChange}
        pagination={false}
        filters={IssueStore.getBarFilters || []}
        filterBarPlaceholder={formatMessage({ id: 'test.common.filter' })}
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

  const manageVisible = (columns) => columns.map((column) => (shouldColumnShow(column) ? { ...column, hidden: false } : { ...column, hidden: true }));

  const reLoadTable = () => {
    IssueStore.loadIssues();
  };

  const { onClick, history } = props;
  const columns = manageVisible([
    {
      title: '',
      key: CHECKBOX_KEY,
      width: 40,
      render: (text, record) => (
        <CustomCheckBox
          checkedMap={IssueStore.checkIdMap}
          value={record.caseId}
          field="caseId"
          dataSource={IssueStore.getIssues}
        />
      ),
    },
    {
      title: formatMessage({ id: 'test.caseLibrary.name' }),
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
      title: formatMessage({ id: 'test.caseLibrary.num' }),
      dataIndex: 'caseNum',
      key: 'caseId',
      sorter: true,
      filters: [],
      render: (caseNum) => renderIssueNum(caseNum),
    },
    {
      title: formatMessage({ id: 'test.common.custom.num' }),
      dataIndex: 'customNum',
      key: 'customNum',
      sorter: true,
      filters: [],
      render: (customNum) => renderIssueNum(customNum),
    },
    {
      title: formatMessage({ id: 'test.common.priority' }),
      dataIndex: 'priorityId',
      key: 'sequence',
      sorter: true,
      filters: IssueStore.priorityList.filter((priorityVO) => priorityVO.enableFlag)
        .map((priorityVO) => ({ text: priorityVO.name, value: priorityVO.id })),
      width: '1rem',
      render: (priorityId, record) => priorityId && <PriorityTag priority={record.priorityVO} />,
    },
    {
      title: formatMessage({ id: 'test.common.creator' }),
      dataIndex: 'createUser',
      key: 'createUser',
      render: (createUser) => createUser && <UserHead user={createUser} style={{ display: 'flex' }} />,
      width: '1rem',
    },
    {
      title: formatMessage({ id: 'test.common.create.date' }),
      dataIndex: 'creationDate',
      key: 'creationDate',
      sorter: true,
      render: (creationDate) => <Tooltip title={creationDate}><span>{creationDate}</span></Tooltip>,
    },
    {
      title: formatMessage({ id: 'test.common.update.user' }),
      dataIndex: 'lastUpdateUser',
      key: 'lastUpdateUser',
      render: (lastUpdateUser) => lastUpdateUser && <UserHead user={lastUpdateUser} style={{ display: 'flex' }} />,
      width: '1rem',
    },
    {
      title: formatMessage({ id: 'test.common.update.date' }),
      dataIndex: 'lastUpdateDate',
      key: 'lastUpdateDate',
      sorter: true,
      render: (lastUpdateDate) => <Tooltip title={lastUpdateDate}><span>{lastUpdateDate}</span></Tooltip>,
    },
  ]);

  const { currentFolder } = IssueTreeStore;

  return (
    <div className="c7ntest-issueArea">
      <div id="template_copy" style={{ display: 'none' }}>
        当前状态：
        <span style={{ fontWeight: 500 }}>{ formatMessage({ id: 'test.common.copy' })}</span>
      </div>
      <div id="template_move" style={{ display: 'none' }}>
        当前状态：
        <span style={{ fontWeight: 500 }}>{ formatMessage({ id: 'test.common.move' })}</span>
      </div>
      <section
        style={{
          boxSizing: 'border-box',
          width: '100%',
        }}
      >
        <Loading loading={IssueStore.loading} allowSelfLoading loadId="table" className="c7ntest-issueManage-table-loading">
          {renderTable(columns)}
        </Loading>
        <div
          className="c7ntest-backlog-sprintIssue"
          role="button"
        >
          <div
            style={{
              userSelect: 'none',
              background: 'white',
              paddingTop: 8,
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
