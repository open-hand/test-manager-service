import React, { Component } from 'react';
import PropTypes from 'prop-types';
import moment from 'moment';
import { Content } from '@choerodon/master';
import { Tabs, Select } from 'choerodon-ui';
import TestTable from './TestTable';
import { toArray } from './utils';
import './TestNGReport.less';

const { TabPane } = Tabs;
const { Option } = Select;


class TestNGReport extends Component {
  state = {
    selectedSuites: [],
    allSuites: [],
    filteredSuites: [],
  }

  handleSelect = (selectedSuites) => {
    this.setState({
      selectedSuites,
    });
  }

  static getDerivedStateFromProps(props, state) { 
    const suites = JSON.parse(JSON.stringify(props.data['testng-results'].suite));
    const filteredSuites = suites.filter((suite) => {
      if (state.selectedSuites.length === 0) { return suite.test; } else {
        return suite.test && state.selectedSuites.includes(suite.name);
      }
    });
    const allSuites = suites.filter(suite => suite.test);
    return {
      allSuites,
      filteredSuites,
    };
  }

  render() {
    const log = toArray(this.props.data['testng-results']['reporter-output'].line).join('\n');
    const { filteredSuites, allSuites } = this.state;
    const { creationDate } = this.props;
    return (
      <Content 
        title="自动化测试报告"
        description={`报告生成时间：${moment(creationDate).format('YYYY年MM月DD日 HH:mm:ss')}`}    
      >    
        <div className="c7ntest-TestNGReport">        
          <Tabs defaultActiveKey="1">
            <TabPane tab="总览" key="1">
              <section style={{ display: 'flex', alignItems: 'center', marginBottom: '15px' }}>
                <span style={{ fontWeight: 500 }}>筛选：</span>
                <Select
                  mode="multiple"
                  className="quickSearchSelect"
                  placeholder="Suite"
                  maxTagCount={0}
                  maxTagPlaceholder={ommittedValues => `${ommittedValues.map(item => item).join(', ')}`}
                  onChange={this.handleSelect}
                  getPopupContainer={triggerNode => triggerNode.parentNode}
                >
                  {
                  allSuites.map(suite => (
                    <Option key={suite.name} value={suite.name}>
                      {suite.name}
                    </Option>
                  ))
                }
                </Select>
              </section>
              <section>
                {
                filteredSuites.map(suite => <TestTable suite={suite} />)
              }
              </section>
            </TabPane>
            <TabPane tab="日志" key="2">
              <div style={{ whiteSpace: 'pre-wrap' }}>
                {log}
              </div>
            </TabPane>
          </Tabs>
        </div>
      </Content>
    );
  }
}

TestNGReport.propTypes = {

};

export default TestNGReport;
