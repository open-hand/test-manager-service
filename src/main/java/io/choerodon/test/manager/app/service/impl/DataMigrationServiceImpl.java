package io.choerodon.test.manager.app.service.impl;

import java.util.List;

import io.choerodon.test.manager.api.vo.TestCaseMigrateDTO;
import io.choerodon.test.manager.app.service.DataMigrationService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.infra.dto.TestCaseAttachmentDTO;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;
import io.choerodon.test.manager.infra.dto.TestIssueFolderRelDTO;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import io.choerodon.test.manager.infra.mapper.TestAttachmentMapper;
import io.choerodon.test.manager.infra.mapper.TestCaseMapper;
import io.choerodon.test.manager.infra.mapper.TestIssueFolderRelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
public class DataMigrationServiceImpl implements DataMigrationService {

    private static final Logger logger = LoggerFactory.getLogger(TestIssueFolderServiceImpl.class);

    @Autowired
    TestCaseService testCaseService;

    @Autowired
    TestCaseFeignClient testCaseFeignClient;

    @Autowired
    TestCaseMapper testCaseMapper;

    @Autowired
    TestAttachmentMapper testAttachmentMapper;

    @Autowired
    TestIssueFolderRelMapper testIssueFolderRelMapper;

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void migrateIssue() {
        List<Long> projectIds = testCaseFeignClient.queryIds(1L).getBody();
        Long startTime = System.currentTimeMillis();
        for (Long projectId : projectIds){
            List<TestCaseMigrateDTO> testCaseMigrateDTOS = testCaseFeignClient.migrateTestCase(projectId).getBody();
            for (TestCaseMigrateDTO testCaseMigrateDTO : testCaseMigrateDTOS){
                logger.info("caseID"+ testCaseMigrateDTO.getCaseId().toString());
                logger.info("description"+ testCaseMigrateDTO.getDescription());
                testCaseMapper.batchInsertTestCase(testCaseMigrateDTO);
                logger.info("InsertTestCase----By----ProjectId{}", testCaseMigrateDTO.getProjectId());
            }
        }
        logger.info("TestCaseDataMigrateSucceed");
        logger.info("Cost {}ms",System.currentTimeMillis() - startTime);

        //更新文件夹相关联的folderid
        List<Long> issueIds = testCaseMapper.listIssueIds();
        for (Long issueid : issueIds) {
            TestIssueFolderRelDTO relDTO = new TestIssueFolderRelDTO();
            relDTO.setIssueId(issueid);
            List<TestIssueFolderRelDTO> list = testIssueFolderRelMapper.select(relDTO);
            for (TestIssueFolderRelDTO testIssueFolderRelDTO : list){
                TestCaseDTO testCaseDTO = new TestCaseDTO();
                testCaseDTO.setCaseId(issueid);
                testCaseDTO.setFolderId(testIssueFolderRelDTO.getFolderId());
                testCaseMapper.updateByPrimaryKeySelective(testCaseDTO);
            }
        }
    }

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void migrateAttachment(){

        logger.info("start---migrate---test-case-attachment");
        Long startTime = System.currentTimeMillis();
        List<TestCaseAttachmentDTO> attachmentDTOS = testCaseFeignClient.migrateAttachment(1L).getBody();
        if (!CollectionUtils.isEmpty(attachmentDTOS)){
            for (TestCaseAttachmentDTO testCaseAttachmentDTO: attachmentDTOS){
                if (testCaseAttachmentDTO != null){
                    logger.info("insert-test-case-attachment{}",testCaseAttachmentDTO.getCaseId());
                    testAttachmentMapper.insert(testCaseAttachmentDTO);
                }
            }
        }
        logger.info("TestCaseAttachmentMigrateSucceed");
        logger.info("Cost {} ms",System.currentTimeMillis() - startTime);
    }
}