import React from 'react';
import { withRouter } from 'react-router-dom';
import PropTypes from 'prop-types';
import { Icon } from 'choerodon-ui';
import { stores } from '@choerodon/master';
import { Tooltip } from 'choerodon-ui';
import { getFileSuffix } from '../../../../../common/utils';
import './SingleFileUpload.less';

const { AppState } = stores;
const previewSuffix = ['doc', 'docx', 'ppt', 'pptx', 'xls', 'xlsx', 'pdf', 'jpg', 'jpeg', 'gif', 'png'];

function SingleFileUplaod(props) {
  const {
    history, url, fileService, fileName, hasDeletePermission, onDeleteFile,
  } = props;
  const handlePreviewClick = (service, name, fileUrl) => {
    const urlParams = AppState.currentMenuType;
    window.open(`/#/agile/preview?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}${service ? `&fileService=${service}` : ''}&fileName=${name}&fileUrl=${fileUrl}`, '_blank');
    // history.push(`/agile/preview?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}${service ? `&fileService=${service}` : ''}&fileName=${name}&fileUrl=${fileUrl}`);
  };

  return (
    <div className="c7n-agile-singleFileUpload">
      <span className="c7n-agile-singleFileUpload-icon">
        {previewSuffix.includes(getFileSuffix(url)) && (
          <Tooltip title="预览">
            <Icon
              type="zoom_in"
              style={{ cursor: 'pointer' }}
              onClick={handlePreviewClick.bind(this, fileService, fileName, url)}
            />
          </Tooltip>
        )}
      </span>
      <a className="c7n-agile-singleFileUpload-download" href={fileService ? `${fileService}${url}` : `${url}`}>
        <span className="c7n-agile-singleFileUpload-icon">
          <Tooltip title="下载">
            <Icon type="get_app" style={{ color: '#000' }} />
          </Tooltip>
        </span>
        <span className="c7n-agile-singleFileUpload-fileName">{fileName}</span>
      </a>
      {(hasDeletePermission && onDeleteFile) && (
        <Tooltip title="删除">
          <Icon
            type="close"
            onClick={() => { onDeleteFile(); }}
          />
        </Tooltip>
      )}
    </div>
  );
}

SingleFileUplaod.propTypes = {
  url: PropTypes.string.isRequired,
  fileService: PropTypes.string,
  fileName: PropTypes.string.isRequired,
  hasDeletePermission: PropTypes.bool,
  onDeleteFile: PropTypes.func,
};

SingleFileUplaod.defaultProps = {
  hasDeletePermission: false,
};

export default withRouter(SingleFileUplaod);
