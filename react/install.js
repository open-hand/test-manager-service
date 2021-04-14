import { set } from '@choerodon/inject';

console.log('ss');
set('testmanager:IssueLinkedTestCase', () => import('@/injects/issue-linked-test-case'));
