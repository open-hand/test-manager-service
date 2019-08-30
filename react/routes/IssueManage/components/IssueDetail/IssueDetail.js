import React, { Component } from 'react';
import PropTypes from 'prop-types';
import EditIssue from '../EditIssue';

class IssueDetail extends Component {
  render() {
    return (
      <div style={{ height: '100%' }}>
        <EditIssue
          loading={loading}
          issueId={testCaseId}
          folderName={folderName}
          issueInfo={issueInfo}
          fileList={fileList}
          setFileList={this.setFileList}
          linkIssues={linkIssues}
          datalogs={datalogs}
          disabled={disabled}
          reloadIssue={this.reloadIssue.bind(this, testCaseId)}
          onClose={() => {
            this.setState({
              isExpand: false,
            });
          }}
          mode="wide"
        />
      </div>
    );
  }
}

IssueDetail.propTypes = {

};

export default IssueDetail;
