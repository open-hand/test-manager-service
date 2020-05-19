/* eslint-disable */
import { getProjectId, request } from '../common/utils';


// 敏捷接口
/**
 * 删除文件
 * @param {number} resourceId 资源id
 * @param {string} 文件id
 */
export function deleteFileAgile(id) {
  return request.delete(`/agile/v1/projects/${getProjectId()}/issue_attachment/${id}`);
}

/**
 * 上传图片
 * @param {any} data
 */
export function uploadImage(data) {
  const axiosConfig = {
    headers: { 'content-type': 'multipart/form-data' },
  };

  return request.post(
    `/agile/v1/projects/${getProjectId()}/issue_attachment/upload_for_address`,
    data,
    axiosConfig,
  );
}

/**
 * 上传issue的附件
 * @param {*} data
 * @param {*} config
 */
export function uploadFileAgile(data, config) {
  const {
    issueType, issueId, fileName,
  } = config;
  const axiosConfig = {
    headers: { 'content-type': 'multipart/form-data' },
  };
  return request.post(
    `/zuul/agile/v1/projects/${getProjectId()}/issue_attachment?projectId=${getProjectId()}&issueId=${issueId}`,
    data,
    axiosConfig,
  );
}
// 测试管理接口

/**
 *文件上传(执行用例)
 *
 * @export
 * @param {*} data
 * @param {*} config
 * @returns
 */
export function uploadFile(data, config) {
  const { attachmentLinkId,attachmentType } = config;

  const axiosConfig = {
    headers: { 'content-type': 'multipart/form-data' },
  };

  return request.post(
    `test/v1/projects/${getProjectId()}/test/case/attachment?attachmentLinkId=${attachmentLinkId}&attachmentType=${attachmentType}`,
    data,
    axiosConfig,
  );
}

/**
 *文件上传(测试步骤)
 *
 * @export
 * @param {*} data
 * @param {*} config
 * @returns
 */
export function uploadFileStep(data, config) {
  const { bucketName, executeId, attachmentLinkId, attachmentType, description = '' } = config;

  const axiosConfig = {
    headers: { 'content-type': 'multipart/form-data' },
  };

  return request.post(
    `test/v1/projects/${getProjectId()}/test/case/attachment?attachmentLinkId=${attachmentLinkId}&attachmentType=${attachmentType}`,
    data,
    axiosConfig,
  );
}
/**
 *删除附件(执行用例删除附件)
 *
 * @export
 * @param {*} id
 * @returns
 */
export function deleteFile(id) {
  return request.delete(`test/v1/projects/${getProjectId()}/test/case/attachment/${id}`);
}
/**
 *删除附件(测试步骤附件 废弃)
 *
 * @export
 * @param {*} id
 * @returns
 */
export function deleteAttachment(id) {
  return request.delete(`test/v1/projects/${getProjectId()}/test/case/attachment/delete/bucket/test/attach/${id}`);
}
export function importIssue(data, versionId) {
  const axiosConfig = {
    headers: { 'content-type': 'multipart/form-data' },
  };
  return request.post(`/test/v1/projects/${getProjectId()}/case/import/testCase?folder_id=${versionId}`, data, axiosConfig);
}
