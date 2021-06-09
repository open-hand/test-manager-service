import React, { useEffect } from 'react';
import { Upload } from 'choerodon-ui';
import { Button } from 'choerodon-ui/pro';
import { randomWord } from '@/common/utils';
import './UploadButton.less';

const UploadButton = (props) => {
  const className = randomWord(false, 32);
  useEffect(() => {
    const uploadElement = document.querySelector(`.${className} .c7n-upload-select`);
    const uploadListElement = document.querySelector(`.${className} .c7n-upload-list`);
    if (uploadElement && uploadListElement) {
      uploadListElement.appendChild(uploadElement);
    }
  });
  const innerProps = {
    multiple: true,
    beforeUpload: () => false,
  };
  return (
    <Upload
      {...innerProps}
      {...props}
      className={`c7nagile-upload-button ${className}`}
    >
      <Button icon="backup-o">上传附件</Button>
    </Upload>
  );
};

export default UploadButton;
