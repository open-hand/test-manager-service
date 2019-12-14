import React from 'react';
import { Choerodon } from '@choerodon/boot';
import { Button, Upload } from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import PropTypes from 'prop-types';
import SingleFileUpload from '@/components/SingleFileUpload';

import { uploadFileStep } from '../../api/FileApi';
import './UploadInTable.less';

const propTypes = {
  fileList: PropTypes.array,
  readOnly: PropTypes.bool,
  config: PropTypes.shape({}),
  onOk: PropTypes.func,
  handleDeleteFile: PropTypes.func,
  handleUpdateFileList: PropTypes.func,
};
const defaultProps = {
  onOk: () => {
  },
  handleUpdateFileList: () => {
  },
};

function UploadInTable(props) {
  const {
    fileList, config, onOk, handleDeleteFile, handleUpdateFileList, readOnly,
  } = props;


  return (
    <div className="c7ntest-upload-table">
      {
        fileList && fileList.length > 0 && fileList.map(item => (
          // <Tooltip trigger="focus" title="{item.name}{item.name}{item.name}{item.name}">
          <SingleFileUpload
            key={item.id}
            url={item.url}
            fileName={item.attachmentName}
            onDeleteFile={() => handleDeleteFile(item)}
            hasDeletePermission
          // hasDeletePermission={hasPermission || AppState.userInfo.id === item.userId}
          />
          // </Tooltip>
        ))
      }
      {
        readOnly || (
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
              uploadFileStep(formData, config).then((res) => {
                if (res.failed) {
                  Choerodon.prompt('不能有重复附件');
                } else {
                  // onOk();
                  handleUpdateFileList(res);
                  Choerodon.prompt('上传成功');
                }
              }).catch((error) => {
                window.console.log(error);
                Choerodon.prompt('网络错误');
              });
              return false;
            }}
          >
            <Button icon="file_upload">
              <FormattedMessage id="upload_attachment" />
            </Button>
          </Upload>
        )


      }

    </div>
  );
}

UploadInTable.propTypes = propTypes;
UploadInTable.defaultProps = defaultProps;

export default UploadInTable;
