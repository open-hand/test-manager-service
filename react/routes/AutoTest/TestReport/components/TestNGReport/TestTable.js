import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {
  Select, Table, Badge,
} from 'choerodon-ui';
import {
  toArray, groupClassByStatus, calculateTestByClass, setGroup, calculateTestByTest,
} from './utils';

const { Option } = Select;

const InnerTable = (TestClass) => {
  const { name } = TestClass;
  const innerColumns = [
    {
      title: name,
      dataIndex: 'name',
      key: 'name',
      colSpan: 3,
      render: (testName) => <div className="c7ntest-text-dot">{testName}</div>,
    },
    {
      title: '时长',
      dataIndex: 'duration-ms',
      key: 'duration-ms',
      colSpan: 0,
      render: (duration) => <div className="c7ntest-text-dot">{`${duration}ms`}</div>,
    },
    {
      title: 'log',
      key: 'log',
      colSpan: 0,
      render: (recordInner) => {
        const { params, exception, groups } = recordInner;
        return (
          <div>
            {groups && <div>{`Group: ${groups.join(',')}`}</div>}
            {params && <div>{`[DATA] ${toArray(params.param).map((param) => param.value).join(',')}`}</div>}
            {exception && <div className="primary">{exception.message}</div>}
            {exception && <div style={{ whiteSpace: 'pre-wrap' }}>{exception['full-stacktrace']}</div>}
          </div>
        );
      },
    },
  ];
  return (
    <Table
      filterBar={false}
      columns={innerColumns}
      dataSource={TestClass['test-method']}
      pagination={false}
    />
  );
};
const expandedRowRender = (record) => {
  const { PassClasses, SkipClasses, FailClasses } = groupClassByStatus(record.class);
  return (
    <div>
      {/* 通过 */}
      {PassClasses.length > 0
        && (
          <div>
            <div className="c7ntest-table-title c7ntest-table-header-pass">
              <Badge status="success" />
              测试通过
            </div>
            {PassClasses.map(InnerTable)}
          </div>
        )}
      {/* 跳过 */}
      {SkipClasses.length > 0
        && (
          <div>
            <div className="c7ntest-table-title c7ntest-table-header-skip">
              <Badge status="warning" />
              测试跳过
            </div>
            {SkipClasses.map(InnerTable)}
          </div>
        )}
      {/* 失败 */}
      {FailClasses.length > 0
        && (
          <div>
            <div className="c7ntest-table-title c7ntest-table-header-failed">
              <Badge status="error" />
              测试失败
            </div>
            {FailClasses.map(InnerTable)}
          </div>
        )}
    </div>
  );
};
class TestTable extends Component {
  state={
    allGroups: [],
    selectedGroup: [],
  }

  static getDerivedStateFromProps(props, state) {
    const suite = JSON.parse(JSON.stringify(props.suite));
    const allGroups = toArray(suite.groups.group);
    //
    setGroup(allGroups, suite, state.selectedGroup);
    const tests = toArray(suite.test);
    // 计算总计
    calculateTestByTest(tests);
    return {
      tests,
      allGroups,
    };
  }

  handleSelectGroup=(selectedGroup) => {
    this.setState({
      selectedGroup,
    });
  }

  render() {
    const { suite } = this.props;
    const { allGroups, tests } = this.state;

    const columns = [
      { title: '测试', dataIndex: 'name', key: 'name' },
      {
        title: '持续时间', dataIndex: 'duration-ms', key: 'duration-ms', render: (duration) => duration !== undefined && `${duration}ms`,
      },
      {
        title:
  <span>
    <Badge status="success" />
    通过
  </span>,
        dataIndex: 'pass',
        key: 'pass',
        render: (pass, record) => record.pass || calculateTestByClass(record.class).pass,
      },
      {
        title:
  <span>
    <Badge status="warning" />
    跳过
  </span>,
        dataIndex: 'skip',
        key: 'skip',
        render: (pass, record) => record.skip || calculateTestByClass(record.class).skip,
      },
      {
        title:
  <span>
    <Badge status="error" />
    失败
  </span>,
        dataIndex: 'fail',
        key: 'fail',
        render: (pass, record) => record.fail || calculateTestByClass(record.class).fail,
      },
      {
        title: '通过率',
        dataIndex: 'passPercent',
        key: 'passPercent',
        render: (pass, record) => `${record.passPercent || calculateTestByClass(record.class).passPercent}%`,
      },
    ];

    const SelectGroup = (
      <Select
        mode="multiple"
        className="quickSearchSelect"
        placeholder="Group"
        maxTagCount={0}
        maxTagPlaceholder={(ommittedValues) => `${ommittedValues.map((item) => item).join(', ')}`}
        onChange={this.handleSelectGroup}
      >
        {
          allGroups.map((group) => (
            <Option key={group.name} value={group.name}>
              {group.name}
            </Option>
          ))
        }
      </Select>
    );
    return (
      <Table
        rowClassName={(record, index) => (index === tests.length - 1 ? 'c7ntest-table-total' : '')}
        title={() => (
          <div className="c7ntest-between-center" style={{ padding: '0 57px' }}>
            {suite.name}
            {SelectGroup}
          </div>
        )}
        filterBar={false}
        columns={columns}
        expandedRowRender={expandedRowRender}
        dataSource={tests}
        pagination={false}
      />
    );
  }
}

TestTable.propTypes = {

};

export default TestTable;
