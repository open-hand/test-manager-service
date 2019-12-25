/*
 * @Author: LainCarl
 * @Date: 2018-11-01 14:56:06
 * @Last Modified by: LainCarl
 * @Last Modified time: 2018-11-01 15:25:27
 * @Feature:
 */

import { getProjectId, request } from '../common/utils';
/**
 *创建issue
 *
 * @export
 * @param {*} issueObj
 * @param {*} folderId
 * @returns
 */
export function createIssue(issueObj) {
  const issue = {
    ...issueObj,
  };
  return request.post(`/test/v1/projects/${getProjectId()}/case/create`, issue);
}
/**
 *获取单个用例详细信息
 *
 * @export
 * @param {*} issueId
 * @returns
 */
export function loadIssue(issueId) {
  return request.get(`/test/v1/projects/${getProjectId()}/case/${issueId}/info`);
}
/**
 *更新用例信息
 *
 * @export
 * @param {*} data
 * @returns
 */
export function updateIssue(data) {
  return request.put(`/test/v1/projects/${getProjectId()}/case/update`, data);
}
/**
 *用例删除
 *
 * @export
 * @param {*} issueId
 * @returns
 */
export function deleteIssue(issueId) {
  return request.delete(`/test/v1/projects/${getProjectId()}/case/${issueId}/delete`);
}
/**
 *删除用例关联
 *
 * @export
 * @param {*} issueLinkId
 * @returns
 */
export function deleteLink(issueLinkId) {
  return request.delete(`/test/v1/projects/${getProjectId()}/case_link?linkId=${issueLinkId}`);
}
/**
 *加载操作日志
 *
 * @export
 * @param {*} issueId
 * @returns
 */
export function loadDatalogs(caseId) {
  return request.get(`/test/v1/projects/${getProjectId()}/data_log?case_id=${caseId}`);
}
/**
 *加载用例以建立关联
 *
 * @export
 * @param {number} [page=0]
 * @param {number} [size=10]
 * @param {*} issueId
 * @param {*} content
 * @returns
 */
export function loadIssuesInLink(page = 0, size = 10, issueId, content) {
  if (issueId && content) {
    return request.get(`/agile/v1/projects/${getProjectId()}/issues/agile/summary?issueId=${issueId}&self=false&content=${content}&page=${page}&size=${size}`);
  } else if (issueId && !content) {
    return request.get(`/agile/v1/projects/${getProjectId()}/issues/agile/summary?issueId=${issueId}&self=false&page=${page}&size=${size}`);
  } else if (!issueId && content) {
    return request.get(`/agile/v1/projects/${getProjectId()}/issues/agile/summary?self=false&content=${content}&page=${page}&size=${size}`);
  } else {
    return request.get(`/agile/v1/projects/${getProjectId()}/issues/agile/summary?self=false&page=${page}&size=${size}`);
  }
}
/**
 *创建用例间的关联
 *
 * @export
 * @param {*} issueId
 * @param {*} issueLinkCreateDTOList
 * @returns
 */
export function createLink(caseId, data) {
  return request.post(`/test/v1/projects/${getProjectId()}/case_link?case_id=${caseId}`, data);
}
// 需要更新
/**
 *加载单个用例的关联用例
 *
 * @export
 * @param {*} issueId
 * @returns
 */
export function loadLinkIssues(issueId) {
  return request.get(`/test/v1/projects/${getProjectId()}/case_link/list_issue_info?case_id=${issueId}`);
}
/**
 *获取用例树
 *
 * @export
 * @returns
 */
export function getIssueTree() {
  return request.get(`/test/v1/projects/${getProjectId()}/issueFolder/query`);
}
/**
 *增加目录
 *
 * @export
 * @param {*} data
 * @returns
 */
export function addFolder(data) {
  return request.post(`/test/v1/projects/${getProjectId()}/issueFolder`, { ...data, versionId: 0 });
}
/**
 *修改目录
 *
 * @export
 * @param {*} data
 * @returns
 */
export function editFolder(data) {
  return request.put(`/test/v1/projects/${getProjectId()}/issueFolder/update`, data);
}
/**
 *移动目录
 *
 * @export
 * @param {*} data
 * @returns
 */
export function moveFolder(data, targetFolderId) {
  return request.put(`/test/v1/projects/${getProjectId()}/issueFolder/move?targetFolderId=${targetFolderId}`, data);
}
/**
 *删除目录
 *
 * @export
 * @param {*} folderId
 * @returns
 */
export function deleteFolder(folderId) {
  return request.delete(`/test/v1/projects/${getProjectId()}/issueFolder/${folderId}`);
}
/**
 *获取用例的步骤
 *
 * @export
 * @param {*} issueId
 * @returns
 */
export function getIssueSteps(issueId) {
  return request.get(`/test/v1/projects/${getProjectId()}/case/step/query/${issueId}`);
}
/**
 *创建用例的步骤
 *
 * @export
 * @param {*} issueId
 * @returns
 */
export function createIssueStep(testCaseStepDTO) {
  return request.put(`/test/v1/projects/${getProjectId()}/case/step/change`, testCaseStepDTO);
}

/**
 *获取目录中的用例
 *
 * @export
 * @param {*} folderId
 * @param {number} [page=0]
 * @param {number} [size=10]
 * @param {*} search
 * @param {*} orderField
 * @param {*} orderType
 * @returns
 */
export function getIssuesByFolder(folderId, page = 1, size = 10, search, orderField, orderType) {
  const searchDTO = { ...search };
  return request.post(`/test/v1/projects/${getProjectId()}/case/list_by_folder_id?folder_id=${folderId}&page=${page}&size=${size}`, searchDTO, {
    params: {
      sort: `${orderField && orderType ? `${orderField},${orderType}` : ''}`,
    },
  });
}

/**
 *用例移动
 *
 * @export
 * @param {*} versionId
 * @param {*} folderId
 * @param {*} issueLinks
 * @returns
 */
export function moveIssues(issueLinks, folderId) {
  return request.post(`/test/v1/projects/${getProjectId()}/case/batch_move?folder_id=${folderId}`, issueLinks);
}
/**
 *用例克隆
 *
 * @export
 * @param {*} versionId
 * @param {*} folderId
 * @param {*} issueLinks
 * @returns
 */
export function copyIssues(issueLinks, folderId) {
  return request.post(`/test/v1/projects/${getProjectId()}/case/batch_clone?folder_id=${folderId}`, issueLinks);
}
/**
 *目录克隆
 *
 * @export
 * @param {*} data
 * @param {*} versionId
 * @returns
 */
export function copyFolders(data, versionId) {
  const folderIds = data.map(item => item.folderId);
  return request.put(`/test/v1/projects/${getProjectId()}/issueFolder/copy?versionId=${versionId}`, folderIds);
}

/**
 *下载导入模板
 *
 * @export
 * @returns
 */
export function downloadTemplate() {
  return request.get(`/test/v1/projects/${getProjectId()}/case/download/excel/import_template`, { responseType: 'arraybuffer' });
}
/**
 *获取导出历史
 *
 * @export
 * @returns
 */
export function getExportList() {
  return request.get(`/test/v1/projects/${getProjectId()}/test/fileload/history/issue`);
}
/**
 *导出失败重试
 *
 * @export
 * @param {*} historyId
 * @returns
 */
export function exportRetry(historyId) {
  return request.get(`/test/v1/projects/${getProjectId()}/case/download/excel/fail?historyId=${historyId}`);
}
/**
 *获取导入历史
 *
 * @export
 * @returns
 */
export function getImportHistory() {
  return request.get(`/test/v1/projects/${getProjectId()}/test/fileload/history/latest`);
}
/**
 * 取消本次导入
 *
 * @export
 * @param {*} historyId
 * @returns
 */
export function cancelImport(historyId) {
  return request.put(`/test/v1/projects/${getProjectId()}/test/fileload/history/cancel?historyId=${historyId}`);
}
/**
 * 克隆一个步骤
 *
 * @export
 * @param {*} data
 * @returns
 */
export function cloneStep(data) {
  return request.post(`/test/v1/projects/${getProjectId()}/case/step/clone`, data);
}
/**
 * 更新一个步骤
 *
 * @export
 * @param {*} data
 * @returns
 */
export function updateStep(data) {
  return request.put(`/test/v1/projects/${getProjectId()}/case/step/change`, data);
}
/**
 * 删除一个步骤
 *
 * @export
 * @param {*} data
 * @returns
 */
export function deleteStep(data) {
  return request.delete(`/test/v1/projects/${getProjectId()}/case/step`, data);
}
/**
 * 测试用例上传附件
 * @param {*} caseId 
 * @param {*} data 
 */
export function uploadFile(caseId, data) {
  const axiosConfig = {
    headers: { 'content-type': 'multipart/form-data' },
  };
  return request.post(`/test/v1/projects/${getProjectId()}/attachment?caseId=${caseId}`, data, axiosConfig);
}
/**
 * 删除测试用例附件
 * @param {number} resourceId 资源id
 * @param {string} 文件id
 */
export function deleteFile(id) {
  return request.delete(`/test/v1/projects/${getProjectId()}/attachment/${id}`);
}
