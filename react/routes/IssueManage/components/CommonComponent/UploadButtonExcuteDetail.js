/* eslint-disable react/jsx-props-no-spreading */
import React from 'react';
import { Choerodon } from '@choerodon/boot';
import { stores } from '@choerodon/boot';

import SingleFileUpload from '@choerodon/agile/lib/components/SingleFileUpload';
import './UploadButtonNow.less';


const { AppState } = stores;
/**
 * hasPermission 校验是否有权限删除
 * 参数类型 Boolean
 * 后续需要开启 则传入校验结果即可
 * 默认无传入校验结果时，不进行删除权限控制
 */
class UploadButtonExcuteDetail extends React.Component {
  // static propTypes = {
  //   onRemove: PropTypes.func,
  //   beforeUpload: PropTypes.func,
  // };

  constructor(props, context) {
    super(props, context);
    this.state = {};
  }

  render() {
    const {
      fileList, updateNow, onRemove, onBeforeUpload, hasPermission = true,
    } = this.props;
    const props = {
      action: '//jsonplaceholder.typicode.com/posts/',
      multiple: true,
      beforeUpload: (file) => {
        if (file.size > 1024 * 1024 * 30) {
          Choerodon.prompt('文件不能超过30M');
          return false;
        } else if (file.name && encodeURI(file.name).length > 210) {
          // check name length, the name in the database will
          // like `file_uuid_encodeURI(file.name)`,
          // uuid's length is 32
          // the total could save is 255
          // so select length of encodeURI(file.name)
          // 255 - 32 - 6 = 217 -> 210

          Choerodon.prompt('文件名过长，建议不超过20个字');
          return false;
        } else {
          const tmp = file;
          tmp.status = 'done';
          if (updateNow) {
            if (fileList.length > 0) {
              updateNow(fileList.slice().concat(file));
            } else {
              updateNow([file]);
            }
          }
        }
        return false;
      },

    };
    
    const handleRemove = (file) => {
      if (onRemove) {
        onRemove(file);
      }
    };

    return (
      <div className="c7n-agile-uploadButtonNow">
        {/* <Upload
          {...props}
          className="upload-button"
        >

        </Upload> */}
        <div className="c7n-agile-uploadButtonNow-fileList">
          {
            fileList && fileList.length > 0 && fileList.map((item) => (
              <SingleFileUpload
                key={item.uid}
                url={item.url}
                fileName={item.name}
                onDeleteFile={() => { handleRemove(item); }}
                hasDeletePermission={hasPermission || AppState.userInfo.id === item.userId}
              />
            ))
          }
        </div>
      </div>
    );
  }
}

export default UploadButtonExcuteDetail;
