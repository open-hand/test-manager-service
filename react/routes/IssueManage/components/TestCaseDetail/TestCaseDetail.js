import React, { Component } from 'react';
import EditIssue from '../EditIssue';
import IssueStore from '../../stores/IssueStore';

class TestCaseDetail extends Component {
  render() {
    const { onClose } = this.props;
    const { clickIssue } = IssueStore;
    const { caseId } = clickIssue;
    return (    
      <EditIssue
        caseId={caseId}
        visible={caseId}
        onClose={onClose}
        IssueStore={IssueStore}
        onUpdate={() => {}}
      />    
    );
  }
}
export default TestCaseDetail;
