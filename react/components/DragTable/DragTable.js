// 可拖动table
import React, { Component } from 'react';
import { Draggable, Droppable, DragDropContext } from 'react-beautiful-dnd';
import { Table } from 'choerodon-ui';
import CustomCheckBox from '../CustomCheckBox';
import './DragTable.less';

const reorder = (list, startIndex, endIndex) => {
  const result = Array.from(list);
  const [removed] = result.splice(startIndex, 1);
  result.splice(endIndex, 0, removed);

  return result;
};
class DragTable extends Component {
  constructor(props) {
    super(props);
    this.state = {
      data: [],
      filteredColumns: [],
    };
  }

  componentWillReceiveProps(nextProps) {
    // 更新数据并避免拖动后的跳动
    if (!(this.props.loading === false && nextProps.loading === true)) {
      this.setState({ data: nextProps.dataSource });
    }
  }

  handleColumnFilterChange = ({ selectedKeys }) => {
    this.setState({
      filteredColumns: selectedKeys,
    });
  }

  shouldColumnShow = (column) => {
    if (column.title === '' || !column.dataIndex) {
      return true;
    }
    const { filteredColumns } = this.state;
    return filteredColumns.length === 0 ? true : filteredColumns.includes(column.key);
  }

  onDragEnd(result) {
    if (result.destination) {
      const fromIndex = result.source.index;
      const toIndex = result.destination.index;
      if (fromIndex === toIndex) {
        return;
      }
      const thedata = this.state.data;
      const data = reorder(
        thedata,
        fromIndex,
        toIndex,
      );
      // console.log('onDragEnd01', result);
      this.setState({ data });
      const { onDragEnd } = this.props;
      if (onDragEnd) {
        onDragEnd(fromIndex, toIndex);
      }
    }
  }

  onDragStart = () => {
    // document.addEventListener('keydown', (e) => { 
    //   e.preventDefault();  
    //   e.stopImmediatePropagation();
    //   console.log(e.keyCode);
    // });
  }

  components = {
    table: () => {
      const { disableContext, disabled } = this.props;
      const table = (
        <table>
          <thead>
            {this.renderThead()}
          </thead>
          {disabled ? (
            <tbody>
              {this.renderTbody(this.state.data)}
            </tbody>
          ) : (
            <Droppable droppableId="dropTable">
              {(provided, snapshot) => (
                <tbody
                  ref={provided.innerRef}
                >
                  {this.renderTbody(this.state.data)}
                  {provided.placeholder}
                </tbody>
              )}
            </Droppable>
          )}
        </table>
      );
      return disabled || disableContext ? table : (
        <DragDropContext onDragEnd={this.onDragEnd.bind(this)} onDragStart={this.onDragStart}>
          {table}
        </DragDropContext>
      );
    },

  }

  handleRow = (item, e) => {
    // console.log(item, e);
    const { onRow } = this.props;
    if (onRow && onRow(item).onClick) {
      onRow(item).onClick(e);
    }
  }

  renderThead = () => {
    const {
      columns, checkedMap, dataSource, checkField, 
    } = this.props;
    const Columns = columns.filter(column => this.shouldColumnShow(column));
    const ths = Columns.map(column => (
      <th style={{ flex: column.width ? 'unset' : (column.flex || 1), width: column.width }}>
        {column.key !== 'checkbox' ? column.title : (
          <CustomCheckBox value="all" checkedMap={checkedMap} dataSource={dataSource} field={checkField} />
        )}
      </th>
    ));
    return (<tr>{ths}</tr>);
  }

  renderTbody(data) {
    const {
      columns, dragKey, disabled, customDragHandle, onRow,
    } = this.props;
    const judgeProps = props => (customDragHandle ? {} : props);
    const Columns = columns.filter(column => this.shouldColumnShow(column));
    const rows = data && data.length > 0 && data.map((item, index) => (
      disabled
        ? (
          <tr onClick={this.handleRow.bind(this, item)}>
            {Columns.map((column) => {
              let renderedItem = null;
              const {
                dataIndex, key, flex, render, width, className,
              } = column;
              if (render) {
                renderedItem = render(data[index][dataIndex], data[index], index, {}, {});
              } else {
                renderedItem = data[index][dataIndex];
              }
              return (
                <td
                  className={className}
                  style={{
                    flex: width ? 'unset' : (flex || 1), width, display: 'flex', alignItems: 'center', 
                  }}
                >
                  {renderedItem}
                </td>
              );
            })}
          </tr>
        )
        : (
          <Draggable key={item[dragKey]} draggableId={item[dragKey]} index={index}>
            {(provided, snapshot) => (
              <tr
                ref={provided.innerRef}
                {...provided.draggableProps}
                {...judgeProps(provided.dragHandleProps)}
                onClick={this.handleRow.bind(this, item)}
                style={{ cursor: 'move', ...provided.draggableProps.style }}
              >
                {Columns.map((column) => {
                  let renderedItem = null;
                  const {
                    dataIndex, key, flex, render, width, className,
                  } = column;
                  if (render) {
                    renderedItem = render(data[index][dataIndex], data[index], index, provided, snapshot);
                  } else {
                    renderedItem = data[index][dataIndex];
                  }


                  return (
                    <td
                      className={className}
                      style={{
                        flex: width ? 'unset' : (flex || 1), width, display: 'flex', alignItems: 'center', 
                      }}
                    >
                      {renderedItem}
                    </td>
                  );
                })}
              </tr>
            )
            }
          </Draggable>
        )
    ));
    return rows;
  }

  render() {
    const { data } = this.state;
    return (
      <div className="c7ntest-dragtable">
        <Table
          {...this.props}
          dataSource={data}
          components={this.components}
          onColumnFilterChange={this.handleColumnFilterChange}
          filterBarPlaceholder="过滤表"
        />
      </div>
    );
  }
}

export default DragTable;
