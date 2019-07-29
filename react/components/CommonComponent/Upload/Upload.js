import React, { Component } from 'react';
import { Button } from 'choerodon-ui';

class Upload extends Component {
  handleUpload = (e) => {
    if (this.props.handleUpload) {
      this.props.handleUpload(e.target.files);
      this.uploadInput.value = '';
    }
  }

  render() {
    return (
      <Button className="c7ntest-upload-button" onClick={() => this.uploadInput.click()}>
        {this.props.children}
        <input
          ref={
            (uploadInput) => { this.uploadInput = uploadInput; }
          }
          type="file"
          multiple
          onChange={this.handleUpload}
          style={{ display: 'none' }}
        />
      </Button>
    );
  }
}

Upload.propTypes = {

};

export default Upload;
