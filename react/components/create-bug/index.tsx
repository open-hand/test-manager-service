import openCreateIssueModal, { CreateIssueProps } from '@choerodon/agile/lib/components/create-issue';
import { CreateIssueBaseProps } from '@choerodon/agile/lib/components/create-issue/BaseComponent';
import { addBugForExecuteOrStep } from '@/api/ExecuteDetailApi';
import { getProjectId } from '@/common/utils';

export type CreateBugProps = CreateIssueProps & Required<Pick<CreateIssueBaseProps, 'parentIssue'>> & {
  stepId: string
  onCreateBug?: () => void
}

const openModal = (props: CreateBugProps) => {
  const {
    stepId, onCreateBug, defaultValues, ...restProps
  } = props;
  const cachedAssignee = sessionStorage.getItem('test.plan.execute.detail.create.bug.default.value');
  let defaultAssignee;
  try {
    if (cachedAssignee) {
      const parsed = JSON.parse(cachedAssignee);
      if (parsed && parsed.projectId === getProjectId()) {
        defaultAssignee = parsed;
      } else {
        sessionStorage.removeItem('test.plan.execute.detail.create.bug.default.value');
      }
    }
  } catch (error) {
    //
  }
  const handleCreate = (res: any) => {
    const { assigneeId, assigneeRealName, projectId } = res.issueInfosVO;
    if (assigneeId) {
      sessionStorage.setItem('test.plan.execute.detail.create.bug.default.value', JSON.stringify({
        id: assigneeId,
        projectId,
        realName: assigneeRealName,
      }));
    }
    onCreateBug && onCreateBug();
  };
  openCreateIssueModal({
    typeCode: 'bug',
    request: (data) => addBugForExecuteOrStep('CASE_STEP', stepId, data),
    ...restProps,
    onCreate: handleCreate,
    defaultAssignee,
  });
};
export default openModal;
