
import { getProjectId, request } from '../common/utils';

export function getCycleTree(assignedTo) {
  return request.get(`/test/v1/projects/${getProjectId()}/cycle/query${assignedTo ? `?assignedTo=${assignedTo}` : ''}`);
}
export function getCycleTreeByVersionId(versionId) {
  return request.get(`/test/v1/projects/${getProjectId()}/cycle/batch/clone/query/${versionId}`);
}
export function getExecutesByCycleId(pagination, cycleId, filters, type) {
  const { size, page } = pagination;
  const Filters = {
    ...filters || {},
    searchDTO: {
      advancedSearchArgs: {
        statusId: [],
        priorityId: [],
      },
      searchArgs: {
        issueNum: '',
        summary: '',
      },
    },
  };
  if (Filters) {
    Object.keys(filters || {}).forEach((filter) => {
      // console.log(filter, Filters);
      if (['priorityId'].includes(filter)) {
        Filters.searchDTO.advancedSearchArgs[filter] = Filters[filter];
      } else if (['summary'].includes(filter)) {
        Filters.searchDTO.searchArgs[filter] = Filters[filter][0];
      } else {
        Filters[filter] = Filters[filter][0];
      }
    });
  }
  return request.post(`/test/v1/projects/${getProjectId()}/cycle/case/query/cycleId?size=${size}&page=${page}`, {
    cycleId,
    ...Filters,
  });
}
export function addCycle(data) {
  return request.post(`/test/v1/projects/${getProjectId()}/cycle`, data);
}
export function editExecuteDetail(data) {
  return request.post(`/test/v1/projects/${getProjectId()}/cycle/case/update`, data);
}
export function deleteExecute(executeId) {
  return request.delete(`/test/v1/projects/${getProjectId()}/cycle/case?cycleCaseId=${executeId}`);
}
export function deleteCycleOrFolder(cycleId) {
  return request.delete(`/test/v1/projects/${getProjectId()}/cycle/delete/${cycleId}`);
}
export function clone(cycleId, data, type) {
  if (type === 'CLONE_FOLDER') {
    return request.post(`/test/v1/projects/${getProjectId()}/cycle/clone/folder/${cycleId}`, data);
  } else if (type === 'CLONE_CYCLE') {
    return request.post(`/test/v1/projects/${getProjectId()}/cycle/clone/cycle/${cycleId}`, data);
  }
  return request.post(`/test/v1/projects/${getProjectId()}/cycle/clone/folder/${cycleId}`, data);
}
export function batchClone(targetVersionId, data) {  
  return request.post(`/test/v1/projects/${getProjectId()}/cycle/batch/clone/${targetVersionId}`, data);
}
export function getLastCloneData() {  
  return request.get(`/test/v1/projects/${getProjectId()}/cycle/batch/clone/latest`);
}

export function addFolder(data) {
  return request.post(`/test/v1/projects/${getProjectId()}/cycle`, data);
}
/**
 * 修改循环或阶段信息
 * @param {*} data 
 */
export function editFolder(data) {
  return request.put(`/test/v1/projects/${getProjectId()}/cycle`, data);
}
/**
 * 拖动改变日期
 * @param {*} data 
 */
export function editCycleTime(data) {
  // return request.put(`/test/v1/projects/${getProjectId()}/cycle`, data);
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve();
    }, 300);
  });
}
export function exportCycle(cycleId) {
  return request.get(`/test/v1/projects/${getProjectId()}/cycle/case/download/excel/${cycleId}`);
}
export function getCyclesByVersionId(versionId) {
  return request.get(`/test/v1/projects/${getProjectId()}/cycle/get/cycles/all/in/version/${versionId}`);
}
export function getFoldersByCycleId(cycleId) {
  return request.get(`/test/v1/projects/${getProjectId()}/cycle/query/folder/cycleId/${cycleId}`);
}
/**
 *获取导出历史
 *
 * @export
 * @returns
 */
export function getExportList() {
  return request.get(`/test/v1/projects/${getProjectId()}/test/fileload/history/cycle`);
}
export function assignBatch(userId, cycleId) {
  return request.put(`test/v1/projects/${getProjectId()}/cycle/batch/change/cycleCase/assignedTo/${userId}/in/cycle/${cycleId}`);
}
