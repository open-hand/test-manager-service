import React, { Component } from 'react';
import './TestCaseDetail.scss';
import EditIssue from '../components/EditIssue';

class TestCaseDetail extends Component {
  render() {
    const { clickIssue, onClose } = this.props;
    const { caseId } = clickIssue;
    return (    
      <EditIssue
        caseId={caseId}
        visible={caseId}
        onClose={onClose}
        onUpdate={() => {}}
      />    
    );
  }
}
export default TestCaseDetail;
