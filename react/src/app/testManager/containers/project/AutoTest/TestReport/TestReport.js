import React, { Component } from 'react';
import { Button, Icon, Progress } from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import {
  Content, Header, Page,
} from 'choerodon-front-boot';
import { MochaReport, TestNGReport } from './components';
import { getTestReport } from '../../../../api/AutoTestApi';
import { commonLink } from '../../../../common/utils';

const ReportComponents = {
  mocha: MochaReport,
  TestNG: TestNGReport,
};
class TestReport extends Component {
  state = {
    loading: true,
    ReportData: { framework: '', json: '{}' },
  }

  componentDidMount() {
    this.loadTestReport();
  }
  
  saveRef = name => (ref) => {
    this[name] = ref;
  }

  loadTestReport = () => {
    const { id } = this.props.match.params;
    this.setState({
      loading: true,
    });
    getTestReport(id).then((report) => {
      this.setState({
        loading: false,
        ReportData: report,
      });
    });
  }

  render() {
    const { loading, ReportData } = this.state;
    const { framework, json, creationDate } = ReportData;
    const Report = ReportComponents[framework] ? ReportComponents[framework] : () => <div />;
    const Data = JSON.parse(json);     
    return (
      <Page>
        <Header
          title="自动化测试报告"
          backPath={commonLink('/AutoTest/list')}
        >
          <Button
            onClick={this.loadTestReport}
          >
            <Icon type="autorenew icon" />
            <FormattedMessage id="refresh" />
          </Button>
        </Header>        
        <Content 
          style={{ padding: 0 }}
        > 
          {loading
            ? <Progress type="loading" className="spin-container" />
            : <Report data={Data} creationDate={creationDate} />}
        </Content>
      </Page>      
    );
  }
}


export default TestReport;
