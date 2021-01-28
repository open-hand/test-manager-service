/* eslint-disable react/jsx-props-no-spreading */
import React from 'react';
import { Choerodon, stores } from '@choerodon/boot';

import SingleFileUpload from '@/components/SingleFileUpload';
import { deleteFile } from '../../../../api/IssueManageApi';

import './UploadButtonNow.less';

const { AppState } = stores;

export function FileList({
  setIssueInfo,
  fileList,
  setFileList,
  hasPermission = true,
  store,
  issueId,
}) {
  const handleRemove = (file) => {
    const index = fileList.indexOf(file);
    const newFileList = fileList.slice();
    if (file.url) {
      deleteFile(file.uid)
        .then((response) => {
          if (response) {
            newFileList.splice(index, 1);
            setFileList(newFileList);
            setIssueInfo(newFileList);
            Choerodon.prompt('删除成功');
          }
        })
        .catch(() => {
          Choerodon.prompt('删除失败，请稍后重试');
        });
    } else {
      newFileList.splice(index, 1);
      setFileList(newFileList);
      setIssueInfo(newFileList);
    }
  };

  return (
    <div className="c7n-agile-uploadButtonNow-fileList">
      {
        fileList && fileList.length > 0 && fileList.map((item) => (
          <SingleFileUpload
            key={item.uid}
            url={item.url}
            fileName={item.name}
            onDeleteFile={() => { handleRemove(item); }}
            hasDeletePermission={hasPermission || AppState.userInfo.id === item.userId}
            percent={!item.url && (item.percent || 0)}
            error={!item.url && item.status === 'error'}
          />
        ))
      }
    </div>
  );
}
