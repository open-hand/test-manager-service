/*
 * @Author: LainCarl
 * @Date: 2018-11-01 14:56:06
 * @Last Modified by: LainCarl
 * @Last Modified time: 2018-11-01 15:25:27
 * @Feature:
 */
import { stores } from '@choerodon/boot';
import { getProjectId, request } from '../common/utils';

const { AppState } = stores;

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
 *创建评论
 *
 * @export
 * @param {*} commitObj
 * @returns
 */
export function createCommit(commitObj) {
  return request.post(`/agile/v1/projects/${getProjectId()}/issue_comment`, commitObj);
}
/**
 *更新评论
 *
 * @export
 * @param {*} commitObj
 * @returns
 */
export function updateCommit(commitObj) {
  return request.post(`/agile/v1/projects/${getProjectId()}/issue_comment/update`, commitObj);
}
/**
 *删除评论
 *
 * @export
 * @param {*} commitId
 * @returns
 */
export function deleteCommit(commitId) {
  return request.delete(`/agile/v1/projects/${getProjectId()}/issue_comment/${commitId}`);
}
/**
 *获取用例状态列表
 *
 * @export
 * @param {*} statusId
 * @param {*} issueId
 * @param {*} typeId
 * @returns
 */
export function loadStatus(statusId, issueId, typeId) {
  return request.get(
    `/agile/v1/projects/${getProjectId()}/schemes/query_transforms?current_status_id=${statusId}&issue_id=${issueId}&issue_type_id=${typeId}&apply_type=test`,
  );
}
/**
 *获取单个用例详细信息
 *
 * @export
 * @param {*} issueId
 * @returns
 */
export function loadIssue(issueId) {
  return request.get(`/agile/v1/projects/${getProjectId()}/issues/${issueId}`);
}
/**
 *更新用例状态
 *
 * @export
 * @param {*} transformId
 * @param {*} issueId
 * @param {*} objVerNum
 * @returns
 */
export function updateStatus(transformId, issueId, objVerNum) {
  return request.put(`/agile/v1/projects/${getProjectId()}/issues/update_status?transformId=${transformId}&issueId=${issueId}&objectVersionNumber=${objVerNum}&applyType=test`);
}
/**
 *更新用例信息
 *
 * @export
 * @param {*} data
 * @returns
 */
export function updateIssue(data) {
  return request.put(`/agile/v1/projects/${getProjectId()}/issues?applyType=test`, data);
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
  return request.delete(`/agile/v1/projects/${getProjectId()}/issue_links/${issueLinkId}`);
}
/**
 *加载操作日志
 *
 * @export
 * @param {*} issueId
 * @returns
 */
export function loadDatalogs(issueId) {
  return request.get(`agile/v1/projects/${getProjectId()}/data_log?issueId=${issueId}`);
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
export function createLink(issueId, issueLinkCreateDTOList) {
  return request.post(`/agile/v1/projects/${getProjectId()}/issue_links/${issueId}`, issueLinkCreateDTOList);
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
  return request.get(`/agile/v1/projects/${getProjectId()}/issue_links/${issueId}?no_issue_test=false`);
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
 *增加文件夹
 *
 * @export
 * @param {*} data
 * @returns
 */
export function addFolder(data) {
  return request.post(`/test/v1/projects/${getProjectId()}/issueFolder`, data);
}
/**
 *修改文件夹
 *
 * @export
 * @param {*} data
 * @returns
 */
export function editFolder(data) {
  return request.put(`/test/v1/projects/${getProjectId()}/issueFolder/update`, data);
}
/**
 *删除文件夹
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
 *获取用例的步骤
 *
 * @export
 * @param {*} issueId
 * @returns
 */
export function createIssueStep(testCaseStepDTO) {
  return request.put(`/test/v1/projects/${getProjectId()}/case/step/change`, testCaseStepDTO);
}
/**
 *获取用例关联的执行
 *
 * @export
 * @param {*} issueId
 * @returns
 */
export function getIssueExecutes(issueId) {
  return request.get(`/test/v1/projects/${getProjectId()}/cycle/case/query/issue/${issueId}`);
}

/**
 *获取单个issue,地址栏跳转情况
 *
 * @export
 * @param {number} [page=0]
 * @param {number} [size=10]
 * @param {*} search
 * @param {*} orderField
 * @param {*} orderType
 * @returns
 */
export function getSingleIssues(page = 1, size = 10, search, orderField, orderType) {
  // console.log(search);
  const searchDTO = { ...search };
  // searchDTO.advancedSearchArgs.typeCode = ['issue_test'];

  return request.post(`/test/v1/projects/${getProjectId()}/issueFolderRel/query?page=${page}&size=${size}`, { versionIds: [], searchDTO }, {
    params: {
      sort: `${orderField && orderType ? `${orderField},${orderType}` : ''}`,
    },
  });
}
/**
 *获取所有用例，分页
 *
 * @export
 * @param {number} [page=0]
 * @param {number} [size=10]
 * @param {*} search
 * @param {*} orderField
 * @param {*} orderType
 * @returns
 */
export function getAllIssues(page = 1, size = 10, search, orderField, orderType) {
  // console.log(search);
  const searchDTO = { ...search, otherArgs: search.searchArgs };
  // searchDTO.advancedSearchArgs.typeCode = ['issue_test'];

  return request.post(`/test/v1/projects/${getProjectId()}/issueFolderRel/query?page=${page}&size=${size}`, { versionIds: [], searchDTO: search }, {
    params: {
      sort: `${orderField && orderType ? `${orderField},${orderType}` : ''}`,
    },
  });
}
/**
 *获取一个/多个版本内的用例
 *
 * @export
 * @param {*} versionIds
 * @param {number} [page=0]
 * @param {number} [size=10]
 * @param {*} search
 * @param {*} orderField
 * @param {*} orderType
 * @returns
 */
export function getIssuesByVersion(versionIds, page = 1, size = 10, search, orderField, orderType) {
  const searchDTO = { ...search, otherArgs: search.searchArgs };
  // searchDTO.advancedSearchArgs.typeCode = ['issue_test'];

  return request.post(`/test/v1/projects/${getProjectId()}/issueFolderRel/query?page=${page}&size=${size}`, { versionIds, searchDTO: search }, {
    params: {
      sort: `${orderField && orderType ? `${orderField},${orderType}` : ''}`,
    },
  });
}
// /**
//  *获取文件夹中的用例
//  *
//  * @export
//  * @param {*} folderId
//  * @param {number} [page=0]
//  * @param {number} [size=10]
//  * @param {*} search
//  * @param {*} orderField
//  * @param {*} orderType
//  * @returns
//  */
// export function getIssuesByFolder(folderId, page = 1, size = 10, search, orderField, orderType) {
//   const searchDTO = { ...search, otherArgs: search.searchArgs };
//   // searchDTO.advancedSearchArgs.typeCode = ['issue_test'];

//   return request.post(`/test/v1/projects/${getProjectId()}/issueFolderRel/query?folderId=${folderId}&page=${page}&size=${size}`, { versionIds: [], searchDTO: search }, {
//     params: {
//       sort: `${orderField && orderType ? `${orderField},${orderType}` : ''}`,
//     },
//   });
// }

/**
 *获取文件夹中的用例
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
  const searchDTO = { ...search, otherArgs: search.searchArgs };
  return request.post(`/test/v1/projects/${getProjectId()}/case/list_by_folder_id?folder_id=${folderId}&page=${page}&size=${size}`, searchDTO, {
    params: {
      sort: `${orderField && orderType ? `${orderField},${orderType}` : ''}`,
    },
  });
  // return request.post(`/test/v1/projects/${getProjectId()}/case/list_by_folder_id?folder_id=${folderId}&page=${page}&size=${size}`, {});
}

/**
 *通过issueid换取issue信息
 *
 * @export
 * @param {*} versionId
 * @param {*} folderId
 * @param {*} ids
 * @returns
 */
export function getIssuesByIds(versionId, folderId, ids) {
  return request.post(`/test/v1/projects/${getProjectId()}/issueFolderRel/query/by/issueId${versionId ? `?versionId=${versionId}` : ''}${folderId ? `&folderId=${folderId}` : ''}`, ids);
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
export function moveIssues(versionId, folderId, issueLinks) {
  return request.put(`/test/v1/projects/${getProjectId()}/issueFolderRel/move?versionId=${versionId}&folderId=${folderId}`, issueLinks);
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
export function copyIssues(versionId, folderId, issueLinks) {
  return request.put(`/test/v1/projects/${getProjectId()}/issueFolderRel/copy?versionId=${versionId}&folderId=${folderId}`, issueLinks);
}
/**
 *文件夹移动
 *
 * @export
 * @param {*} data
 * @returns
 */
export function moveFolders(data) {
  return request.put(`/test/v1/projects/${getProjectId()}/issueFolder/move`, data);
}
/**
 *文件夹克隆
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
 *获取版本内的所有文件夹
 *
 * @export
 * @param {*} versionId
 * @returns
 */
export function getFoldersByVersion(versionId) {
  return request.get(`/test/v1/projects/${getProjectId()}/issueFolder/query/all${versionId ? `?versionId=${versionId}` : ''}`);
}
/**
 *版本上的同步
 *
 * @export
 * @param {*} versionId
 * @returns
 */
export function syncFoldersInVersion(versionId) {
  // cycleId || versionId

  return request.post(`/test/v1/projects/${getProjectId()}/cycle/synchro/folder/all/in/version/${versionId}`);
}
/**
 *循环上的同步
 *
 * @export
 * @param {*} cycleId
 * @returns
 */
export function syncFoldersInCycle(cycleId) {
  // cycleId || versionId

  return request.post(`/test/v1/projects/${getProjectId()}/cycle/synchro/folder/all/in/cycle/${cycleId}`);
}
/**
 *文件夹同步
 *
 * @export
 * @param {*} folderId
 * @param {*} cycleId
 * @returns
 */
export function syncFolder(folderId, cycleId) {
  return request.post(`/test/v1/projects/${getProjectId()}/cycle/synchro/folder/${folderId}/in/${cycleId}`);
}
/**
 *单个用例克隆自身
 *
 * @export
 * @param {*} issueId
 * @param {*} copyConditionDTO
 * @returns
 */
export function cloneIssue(issueId, copyConditionDTO) {
  return request.put(`/test/v1/projects/${getProjectId()}/issueFolderRel/copy/issue/${issueId}`, copyConditionDTO);
}
/**
 *所有用例导出
 *
 * @export
 * @returns
 */
export function exportIssues() {
  return request.get(`/test/v1/projects/${getProjectId()}/case/download/excel`);
}
/**
 *版本下的用例导出
 *
 * @export
 * @param {*} versionId
 * @returns
 */
export function exportIssuesFromVersion(versionId) {
  return request.get(`/test/v1/projects/${getProjectId()}/case/download/excel/version?versionId=${versionId}`);
}
/**
 *文件夹下的用例导出
 *
 * @export
 * @param {*} folderId
 * @returns
 */
export function exportIssuesFromFolder(folderId) {
  return request.get(`/test/v1/projects/${getProjectId()}/case/download/excel/folder?folderId=${folderId}&userId=${AppState.userInfo.id}`);
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
