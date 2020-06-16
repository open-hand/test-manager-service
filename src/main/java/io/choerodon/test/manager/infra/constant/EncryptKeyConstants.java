package io.choerodon.test.manager.infra.constant;

/**
 * @author superlee
 * @since 2020-06-12
 */
public interface EncryptKeyConstants {

    String TEST_APP_INSTANCE = "test_app_instance";

    String TEST_APP_INSTANCE_LOG = "test_app_instance_log";

    String TEST_AUTOMATION_HISTORY = "test_automation_history";

    String TEST_AUTOMATION_RESULT = "test_automation_result";

    String TEST_CASE_ATTACHMENT = "test_case_attachment";

    String TEST_CASE = "test_case";

    String TEST_CASE_LABEL = "test_case_label";

    String TEST_CASE_LABEL_REL = "test_case_label_rel";

    String TEST_CASE_LINK = "test_case_link";

    String TEST_CASE_STEP = "test_case_step";

    String TEST_CYCLE_CASE_ATTACH_REL = "test_cycle_case_attach_rel";

    String TEST_CYCLE_CASE_DEFECT_REL = "test_cycle_case_defect_rel";

    String TEST_CYCLE_CASE = "test_cycle_case";

    String TEST_CYCLE_CASE_HISTORY = "test_cycle_case_history";

    String TEST_CYCLE_CASE_LABEL_REL = "test_cycle_case_label_rel";

    String TEST_CYCLE_CASE_STEP = "test_cycle_case_step";

    String TEST_CYCLE = "test_cycle";

    String TEST_DATA_LOG = "test_data_log";

    String TEST_ENV_COMMAND = "test_env_command";

    String TEST_ENV_COMMAND_VALUE = "test_env_command_value";

    String TEST_FILELOAD_HISTORY = "test_fileload_history";

    String TEST_ISSUE_FOLDER = "test_issue_folder";

    String TEST_ISSUE_FOLDER_REL = "test_issue_folder_rel";

    String TEST_PLAN = "test_plan";

    String TEST_PROJECT_INFO = "test_project_info";

    String TEST_STATUS = "test_status";

    /**
     * 下面的数据是没有表的情况(比如直接调用其他服务的feign返回)，直接用实体类作为加密key
     */
    String INSTANCE_VALUE = "instance_value";
}