package io.choerodon.test.manager.infra.constant;

/**
 * @author zhaotianxin
 * @since 2019/11/28
 */
public class SagaTaskCodeConstants {

    public final static String TEST_MANAGER_CREATE_PLAN_TASK = "test-manager-create-plan-task";

    /**
     * 复制计划
     */
    public final static String TEST_MANAGER_CLONE_PLAN_TASK = "test-manager-clone-plan-task";
    /**
     * 改变计划状态
     */
    public final static String TEST_MANAGER_PLAN_FAIL_TASK = "test-manager-status-fail-task";
    /**
     * 复制用例文件夹
     */
    public final static String TEST_MANAGER_CLONE_TEST_ISSUE_FOLDER_TASK = "test-manager-clone-test-issue-folder-task";

    /**
     * 创建组织SagaCode
     */
    public static final String ORG_CREATE = "org-create-organization";
    /**
     * 创建项目SagaTaskCode
     */
    public static final String TASK_ORG_CREATE = "agile-create-organization";

    /**
     * iam更新项目类型code
     */
    public static final String PROJECT_UPDATE = "iam-update-project";

    /**
     * 更新项目SagaTaskCode
     */
    public static final String TASK_PROJECT_UPDATE = "test-update-project";

    /**
     * 复制工作项SagaCode
     */
    public static final String CLONE_ISSUE = "agile-clone-issue";

    /**
     * 复制工作项SagaTaskCode
     */
    public static final String TASK_CLONE_ISSUE = "test-clone-issue";

}
