package io.choerodon.test.manager.app.service.impl;

import java.util.List;

import io.choerodon.test.manager.api.vo.TestCaseMigrateVO;
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
            List<TestCaseMigrateVO> testCaseMigrateVOS = testCaseFeignClient.migrateTestCase(projectId).getBody();
            for (TestCaseMigrateVO testCaseMigrateVO : testCaseMigrateVOS){
                testCaseMapper.batchInsertTestCase(testCaseMigrateVO);
                logger.info("InsertTestCase----By----ProjectId{}",testCaseMigrateVO.getProjectId());
            }
        }
        logger.info("TestCaseDataMigrateSucceed");
        logger.info("Cost {}ms",System.currentTimeMillis() - startTime);

        List<Long> issueIds = testCaseMapper.listIssueIds();
        for (Long issueid : issueIds) {
            TestIssueFolderRelDTO relDTO = new TestIssueFolderRelDTO();
            relDTO.setIssueId(issueid);
            relDTO = testIssueFolderRelMapper.selectOneByExample(relDTO);
            TestCaseDTO testCaseDTO = new TestCaseDTO();
            testCaseDTO.setCaseId(issueid);
            testCaseDTO.setFolderId(relDTO.getFolderId());
            testCaseMapper.updateByPrimaryKeySelective(testCaseDTO);
        }
    }

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void migrateAttachment() {

        List<Long> issueIds = testCaseMapper.listIssueIds();
        logger.info("start---migrate---test-case-attachment");
        Long startTime = System.currentTimeMillis();
        for (Long issueId : issueIds) {
            List<TestCaseAttachmentDTO> attachmentDTOS = testCaseFeignClient.migrateAttachment(1L,issueId).getBody();
            for (TestCaseAttachmentDTO testCaseAttachmentDTO: attachmentDTOS){
                if (testCaseAttachmentDTO != null){
                    testAttachmentMapper.insertTestCaseAttachment(testCaseAttachmentDTO);
                }
            }
        }
        logger.info("TestCaseAttachmentMigrateSucceed");
        logger.info("Cost {} ms",System.currentTimeMillis() - startTime);
    }
}