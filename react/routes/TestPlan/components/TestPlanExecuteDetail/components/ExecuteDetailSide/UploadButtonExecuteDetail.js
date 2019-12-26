import React from 'react';
import SingleFileUpload from '@/components/SingleFileUpload';
import './UploadButtonNow.less';

class UploadButtonExecuteDetail extends React.Component {
  render() {
    const {
      fileList,
    } = this.props;
    return (
      <div className="c7n-agile-uploadButtonNow">       
        <div className="c7n-agile-uploadButtonNow-fileList">
          {
            fileList && fileList.length > 0 && fileList.map(item => (
              <SingleFileUpload
                key={item.id}
                url={item.url}
                fileName={item.attachmentName}               
                hasDeletePermission={false}
              />
            ))
          }
        </div>
      </div>
    );
  }
}

export default UploadButtonExecuteDetail;
