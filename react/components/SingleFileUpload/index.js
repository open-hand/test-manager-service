let SingleFileUpload = null;
try {
  SingleFileUpload = require('@choerodon/agile-pro/lib/components/SingleFileUpload').default;
} catch (error) {
  SingleFileUpload = require('@choerodon/agile/lib/components/SingleFileUpload').default;
}

export default SingleFileUpload;
