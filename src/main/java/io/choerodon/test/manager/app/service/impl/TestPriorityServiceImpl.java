package io.choerodon.test.manager.app.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.test.manager.api.vo.agile.ProjectDTO;
import io.choerodon.test.manager.app.service.TestPriorityService;
import io.choerodon.test.manager.infra.dto.TestPriorityDTO;
import io.choerodon.test.manager.infra.feign.BaseFeignClient;
import io.choerodon.test.manager.infra.mapper.TestPriorityMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 应用服务默认实现
 *
 * @author jiaxu.cui@hand-china.com 2020-08-19 17:25:29
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestPriorityServiceImpl implements TestPriorityService {

    private static final String NOT_FOUND = "error.priority.notFound";
    private static final String DELETE_ILLEGAL = "error.priority.deleteIllegal";
    private static final String LAST_ILLEGAL = "error.priority.lastIllegal";

    @Autowired
    private TestPriorityMapper testPriorityMapper;
    @Autowired
    private BaseFeignClient baseFeignClient;

    @Override
    public List<TestPriorityDTO> list(Long organizationId, TestPriorityDTO testPriorityDTO) {
        return testPriorityMapper.fulltextSearch(testPriorityDTO);
    }

    @Override
    public TestPriorityDTO create(Long organizationId, TestPriorityDTO testPriorityDTO) {
        if (checkName(organizationId, testPriorityDTO.getName())) {
            throw new CommonException("error.priority.create.name.same");
        }
        testPriorityDTO.setSequence((testPriorityMapper.getNextSequence(organizationId)).add(new BigDecimal(1)));
        testPriorityDTO.setOrganizationId(organizationId);
        //若设置为默认值，则清空其他默认值
        if (BooleanUtils.isTrue(testPriorityDTO.getDefaultFlag())) {
            testPriorityMapper.cancelDefaultPriority(organizationId);
        } else {
            testPriorityDTO.setDefaultFlag(false);
        }
        int isInsert = testPriorityMapper.insert(testPriorityDTO);
        if (isInsert != 1) {
            throw new CommonException("error.priority.create");
        }
        return testPriorityDTO;
    }

    @Override
    public TestPriorityDTO update(Long organizationId, TestPriorityDTO testPriorityDTO) {
        if (checkNameUpdate(testPriorityDTO.getOrganizationId(), testPriorityDTO.getId(), testPriorityDTO.getName())) {
            throw new CommonException("error.priority.update.name.same");
        }
        //若设置为默认值，则清空其他默认值
        if (BooleanUtils.isTrue(testPriorityDTO.getDefaultFlag())) {
            testPriorityMapper.cancelDefaultPriority(testPriorityDTO.getOrganizationId());
        } else {
            //如果只有一个默认优先级时，无法取消当前默认优先级
            TestPriorityDTO select = new TestPriorityDTO();
            select.setDefaultFlag(true);
            select.setOrganizationId(testPriorityDTO.getOrganizationId());
            TestPriorityDTO result = testPriorityMapper.selectOne(select);
            if (result.getId().equals(testPriorityDTO.getId())) {
                throw new CommonException("error.priority.illegal");
            }
        }
        int isUpdate = testPriorityMapper.updateByPrimaryKeySelective(testPriorityDTO);
        if (isUpdate != 1) {
            throw new CommonException("error.priority.update");
        }
        return testPriorityDTO;
    }

    @Override
    public void delete(Long organizationId, TestPriorityDTO testPriorityDTO) {
        Long priorityId = testPriorityDTO.getId();
        Long changePriorityId = testPriorityDTO.getChangePriorityId();
        if (priorityId.equals(changePriorityId)) {
            throw new CommonException(DELETE_ILLEGAL);
        }
        checkLastPriority(organizationId, priorityId);
        TestPriorityDTO priority = testPriorityMapper.selectByPrimaryKey(priorityId);
        List<ProjectDTO> projectVOS = baseFeignClient.listProjectsByOrgId(organizationId).getBody();
        List<Long> projectIds = projectVOS.stream().map(ProjectDTO::getId).collect(Collectors.toList());
        Long count;
        if (CollectionUtils.isEmpty(projectIds)) {
            count = 0L;
        } else {
            count = this.checkPriorityDelete(organizationId, priorityId, projectIds);
        }
        //执行优先级转换
        if (!count.equals(0L)) {
            if (changePriorityId == null) {
                throw new CommonException(DELETE_ILLEGAL);
            }
            CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
            this.batchChangeIssuePriority(organizationId, priorityId, changePriorityId, customUserDetails.getUserId(), projectIds);
        }
        int isDelete = testPriorityMapper.deleteByPrimaryKey(priorityId);
        if (isDelete != 1) {
            throw new CommonException("error.priority.delete");
        }
        if (priority.getDefaultFlag()) {
            updateOtherDefault(organizationId);
        }
    }

    @Override
    public void batchChangeIssuePriority(Long organizationId, Long priorityId, Long changePriorityId, Long userId, List<Long> projectIds) {
        if (CollectionUtils.isNotEmpty(projectIds)) {
            // FIXME 将用例优先级移动到指定优先级
//            issueAccessDataService.batchUpdateIssuePriority(organizationId, priorityId, changePriorityId, userId, projectIds);
        }
    }

    @Override
    public Long checkPriorityDelete(Long organizationId, Long priorityId, List<Long> projectIds) {
        if (projectIds == null || projectIds.isEmpty()) {
            return 0L;
        } else {
            // FIXME 检查是否有case或cycleCase在使用优先级
            return 1L;
//                    issueMapper.checkPriorityDelete(priorityId, projectIds);
        }
    }

    @Override
    public void changePriorityEnabled(Long organizationId, Long id, boolean enableFlag) {
        if (!enableFlag) {
            checkLastPriority(organizationId, id);
        }
        TestPriorityDTO priority = testPriorityMapper.selectByPrimaryKey(id);
        if (priority == null) {
            throw new CommonException(NOT_FOUND);
        }
        priority.setEnableFlag(enableFlag);
        testPriorityMapper.updateOptional(priority, TestPriorityDTO.FIELD_ENABLE_FLAG);
        //失效之后再进行默认优先级的重置
        if (!enableFlag && priority.getDefaultFlag()) {
            updateOtherDefault(organizationId);
        }
    }

    /**
     * 当执行失效/删除时，若当前是默认优先级，则取消当前默认优先级，并设置第一个为默认优先级，要放在方法最后执行
     *
     * @param organizationId
     */
    private void updateOtherDefault(Long organizationId) {
        testPriorityMapper.cancelDefaultPriority(organizationId);
        testPriorityMapper.updateMinSeqAsDefault(organizationId);
    }
    /**
     * 操作的是最后一个有效优先级则无法删除/失效
     *
     * @param organizationId
     */
    private void checkLastPriority(Long organizationId, Long priorityId) {
        TestPriorityDTO priority = new TestPriorityDTO();
        priority.setEnableFlag(true);
        priority.setOrganizationId(organizationId);
        List<TestPriorityDTO> priorities = testPriorityMapper.select(priority);
        if (priorities.size() == 1 && priorityId.equals(priorities.get(0).getId())) {
            throw new CommonException(LAST_ILLEGAL);
        }
    }


    private Boolean checkNameUpdate(Long organizationId, Long priorityId, String name) {
        TestPriorityDTO priority = new TestPriorityDTO();
        priority.setOrganizationId(organizationId);
        priority.setName(name);
        TestPriorityDTO res = testPriorityMapper.selectOne(priority);
        return res != null && !priorityId.equals(res.getId());
    }

    public Boolean checkName(Long organizationId, String name) {
        TestPriorityDTO priority = new TestPriorityDTO();
        priority.setOrganizationId(organizationId);
        priority.setName(name);
        TestPriorityDTO res = testPriorityMapper.selectOne(priority);
        return res != null;
    }
}
