import { set } from '@choerodon/inject';

set('testmanager:IssueLinkedTestCase', () => import('@/injects/issue-linked-test-case'));
