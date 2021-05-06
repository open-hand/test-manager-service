import { axios } from '@choerodon/boot';
import { getProjectId } from '@/common/utils';

const getData = async ({ planId }: { planId: string }) => axios.post(`/test/v1/projects/${getProjectId()}/plan/${planId}/reporter/issue?page=0&size=0`, {}).then((res: any) => res.content);
export default getData;
