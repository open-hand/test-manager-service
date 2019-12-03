import React, { Component } from 'react';
import { Choerodon } from '@choerodon/boot';
import { Button, Upload } from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
// import SingleFileUpload from '../../routes/IssueManage/components/CommonComponent/SingleFileUpload';
// react/routes/IssueManage/components/CommonComponent/SingleFileUpload
import SingleFileUpload from '@/components/SingleFileUpload';

import { uploadFile, deleteAttachment } from '../../api/FileApi';
import './UploadInTable.less';

// react\components\SingleFileUpload
class UploadInTable extends Component {
  render() {
    const handleRemove = (file) => {
      if (file.url) {
        this.props.enterLoad();
        deleteAttachment(file.id).then(() => {
          this.props.onOk();
        }).catch((error) => {
          window.console.log(error);
          this.props.leaveLoad();
          Choerodon.prompt('网络异常');
        });
      }
    };
    const { fileList, config } = this.props;
    return (
      <div className="c7ntest-upload-table">
        {
          fileList && fileList.length > 0 && fileList.map(item => (
            // <Tooltip trigger="focus" title="{item.name}{item.name}{item.name}{item.name}">
            <SingleFileUpload
              key={item.id}
              url={item.url}
              fileName={item.attachmentName}
              onDeleteFile={() => { handleRemove(item); }}
              hasDeletePermission
            // hasDeletePermission={hasPermission || AppState.userInfo.id === item.userId}
            />
            // </Tooltip>
          ))
        }
        <Upload
          // multiple
          className="c7ntest-upload-reverse"
          // fileList={fileList.map(attachment => ({
          //   uid: attachment.id,
          //   name: attachment.attachmentName,
          //   status: 'done',
          //   url: attachment.url,
          // }))}
          // onRemove={(file) => {
          //   if (file.url) {
          //     this.props.enterLoad();
          //     deleteAttachment(file.uid).then(() => {
          //       this.props.onOk();
          //     }).catch((error) => {
          //       window.console.log(error);
          //       this.props.leaveLoad();
          //       Choerodon.prompt('网络异常');
          //     });
          //   }
          // }}
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
            {!fileList ? '' : <FormattedMessage id="upload_attachment" />}
          </Button>
        </Upload>

      </div>
    );
  }
}

UploadInTable.propTypes = {

};

export default UploadInTable;
