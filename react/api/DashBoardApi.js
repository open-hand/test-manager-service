import { getProjectId, request } from '../common/utils';

export default function loadProgressByVersion(versionId, cycleId) {
  return request.get(`/test/v1/projects/${getProjectId()}/cycle/count/color/in/version/${versionId}${cycleId ? `?cycleId=${cycleId}` : ''}`);
}
