import { set } from '@choerodon/inject';

set('testmanager:IssueLinkedTestCase', () => import('@/injects/issue-linked-test-case'));
set('testmanager:StatusAutoTransform', () => import('@/injects/status-autoTransform'));
