import React, { Component } from 'react';
import { Button, Upload } from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import { uploadFile, deleteAttachment } from '../../api/FileApi';
import './UploadInTable.less';

class UploadInTable extends Component {
  render() {
    const { fileList, config } = this.props;
    return (
      <Upload
      // multiple
        className="c7ntest-upload-reverse"
        fileList={fileList.map(attachment => ({
          uid: attachment.id,
          name: attachment.attachmentName,
          status: 'done',
          url: attachment.url,
        }))}
        onRemove={(file) => {
          if (file.url) {
            this.props.enterLoad();
            deleteAttachment(file.uid).then(() => {
              this.props.onOk();
            }).catch((error) => {
              window.console.log(error);
              this.props.leaveLoad();
              Choerodon.prompt('网络异常');
            });
          }
        }}
        beforeUpload={(file) => {
          const formData = new FormData();
          // const config = {
          //   bucket_name: 'test',
          //   attachmentLinkId: record.executeStepId,
          //   attachmentType: 'CYCLE_STEP',
          // };
          // upload file                
          formData.append('file', file);      
          // formData.append('file', file);
          this.props.enterLoad();
          uploadFile(formData, config).then((res) => {
            if (res.failed) {
              this.props.leaveLoad();
              Choerodon.prompt('不能有重复附件');
            } else {
              this.props.onOk();
            }
          }).catch((error) => {
            window.console.log(error);
            this.props.leaveLoad();
            Choerodon.prompt('网络错误');
          });
          return false;
        }}
      >
        <Button icon="file_upload">
          <FormattedMessage id="upload_attachment" />
        </Button>
      </Upload>
    );
  }
}

UploadInTable.propTypes = {

};

export default UploadInTable;
