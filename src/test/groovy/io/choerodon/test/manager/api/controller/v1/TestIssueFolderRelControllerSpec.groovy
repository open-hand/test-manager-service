package io.choerodon.test.manager.api.controller.v1

import com.alibaba.fastjson.JSONObject
import io.choerodon.agile.api.dto.IssueCreateDTO
import io.choerodon.agile.api.dto.IssueDTO
import io.choerodon.agile.api.dto.SearchDTO
import io.choerodon.core.domain.Page
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.dto.IssueInfosDTO
import io.choerodon.test.manager.api.dto.TestIssueFolderRelDTO
import io.choerodon.test.manager.app.service.TestCaseService
import io.choerodon.test.manager.app.service.TestIssueFolderRelService
import io.choerodon.test.manager.infra.dataobject.TestIssueFolderRelDO
import io.choerodon.test.manager.infra.mapper.TestIssueFolderMapper
import io.choerodon.test.manager.infra.mapper.TestIssueFolderRelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestIssueFolderRelControllerSpec extends Specification {
    @Autowired
    TestCaseService testCaseService

    @Autowired
    TestIssueFolderRelService testIssueFolderRelService

    @Autowired
    TestIssueFolderRelMapper testIssueFolderRelMapper

    @Autowired
    TestIssueFolderMapper testIssueFolderMapper

    @Autowired
    TestRestTemplate restTemplate

    @Shared
    List objectVersionNumbers = new ArrayList()
    @Shared
    List foldersId = new ArrayList()
    @Shared
    def projectId = 1L
    @Shared
    def versionId = 1L

    def "InsertTestAndRelationship"() {
        given:
        //等等IssueCreateDTO测试数据
        IssueCreateDTO issueCreateDTO = new IssueCreateDTO()
        issueCreateDTO.setProjectId(projectId)
        issueCreateDTO.setTypeCode("issue_test")
        issueCreateDTO.setSummary("测试创建")
        IssueDTO issueDTO = new IssueDTO()
        issueDTO.setIssueId(11L)
        issueDTO.setObjectVersionNumber(1L)
        issueDTO.setProjectId(projectId)

        IssueCreateDTO issueCreateDTO2 = new IssueCreateDTO()
        issueCreateDTO2.setProjectId(projectId)
        issueCreateDTO2.setTypeCode("issue_test2")
        issueCreateDTO2.setSummary("测试创建2")
        IssueDTO issueDTO2 = new IssueDTO()
        issueDTO2.setIssueId(999999L)
        issueDTO2.setObjectVersionNumber(1L)
        issueDTO2.setProjectId(projectId)

        List list = testIssueFolderRelMapper.selectAll()
        println("list.size:"+list.size())
        for (TestIssueFolderRelDO testIssueFolderRelDO:list){
            println("issueId:"+testIssueFolderRelDO.issueId)
        }

        when: '向issueFolderRel的插入创建接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issueFolderRel/testAndRelationship?folder_id={folderId}&version_id={versionId}', issueCreateDTO, TestIssueFolderRelDTO, projectId, 11L, versionId)
        then:
        1 * testCaseService.createTest(_, _) >> issueDTO
        entity.statusCode.is2xxSuccessful()

        and:
        entity.body != null
        entity.body.issueId == 11L

        and: '设置值'
        objectVersionNumbers.add(entity.body.objectVersionNumber)
        foldersId.add(entity.body.folderId)

        when: '向issueFolderRel的插入创建接口发请求'
        def entity2 = restTemplate.postForEntity('/v1/projects/{project_id}/issueFolderRel/testAndRelationship?folder_id={folderId}&version_id={versionId}', issueCreateDTO2, TestIssueFolderRelDTO, projectId, null, versionId)
        then:
        1 * testCaseService.createTest(_, _) >> issueDTO2
        entity2.statusCode.is2xxSuccessful()

        and:
        entity2.body != null
        entity2.body.issueId == 999999L

        when: '向testIssueFolderRel的插入接口发请求'
        def resultFailure = restTemplate.postForEntity('/v1/projects/{project_id}/issueFolderRel/testAndRelationship?folder_id={folderId}&version_id={versionId}', issueCreateDTO, String.class, projectId, foldersId[0], versionId)
        then: '返回值'
        1 * testCaseService.createTest(_, _) >> issueDTO
        resultFailure.statusCode.is2xxSuccessful()
        JSONObject exceptionInfo = JSONObject.parse(resultFailure.body)
        assert exceptionInfo.get("failed").toString() == "true"
        assert exceptionInfo.get("code").toString() == "error.db.duplicateKey"

    }

    def "InsertRelationship"() {
        given:
        TestIssueFolderRelDTO testIssueFolderRelDTO1 = new TestIssueFolderRelDTO()
        testIssueFolderRelDTO1.setProjectId(projectId)
        testIssueFolderRelDTO1.setIssueId(22L)
        testIssueFolderRelDTO1.setFolderId(foldersId[0])
        testIssueFolderRelDTO1.setVersionId(versionId)

        List<TestIssueFolderRelDTO> testIssueFolderRelDTOS = new ArrayList<>()
        testIssueFolderRelDTOS.add(testIssueFolderRelDTO1)

        TestIssueFolderRelDTO testIssueFolderRelDTO2 = new TestIssueFolderRelDTO()
        testIssueFolderRelDTO2.setProjectId(projectId)
        testIssueFolderRelDTO2.setIssueId(33L)
        testIssueFolderRelDTO2.setFolderId(1L)
        testIssueFolderRelDTO2.setVersionId(versionId)
        testIssueFolderRelDTOS.add(testIssueFolderRelDTO2)

        when: '向issueFolderRel的插入创建接口发请求'
        def entities = restTemplate.postForEntity('/v1/projects/{project_id}/issueFolderRel', testIssueFolderRelDTOS, List, projectId)
        then:
        entities.body.size() > 1
    }

    def "QueryIssuesById"() {
        given:
        IssueInfosDTO issueInfosDTO = new IssueInfosDTO()
        issueInfosDTO.setIssueId(11L)
        IssueInfosDTO issueInfosDTO1 = new IssueInfosDTO()
        issueInfosDTO.setIssueId(22L)
        Long[] issues = new Long[2]
        issues[0] = 11L
        issues[1] = 22L
        Map map = new HashMap()
        map.put(11L, issueInfosDTO)
        map.put(22L, issueInfosDTO1)

        Long[] exceptionIssues = new Long[2]
        exceptionIssues[0] = 11111L
        exceptionIssues[1] = 22222L

        when: '向查询testIssueFolderRel的接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issueFolderRel/query/by/issueId?folder_id={folderId}&version_id={versionId}', issues, Page.class, projectId, foldersId[0], versionId)

        then: '返回值'
        1 * testCaseService.getIssueInfoMap(_, _, _) >> map
        entity.statusCode.is2xxSuccessful()
        List detailFolderRelDTOS = entity.body

        expect: "设置期望值"
        detailFolderRelDTOS.size() == 2

        when: '向testIssueFolderRel的查询接口发请求'
        def resultFailure = restTemplate.postForEntity('/v1/projects/{project_id}/issueFolderRel/query/by/issueId?folder_id={folderId}&version_id={versionId}', exceptionIssues, Page.class, projectId, foldersId[0], versionId)
        then: '返回值'
        1 * testCaseService.getIssueInfoMap(_, _, _) >> map
        resultFailure.statusCode.is2xxSuccessful()
        assert resultFailure.body.isEmpty()

        when: '向testIssueFolderRel的查询接口发请求'
        resultFailure = restTemplate.postForEntity('/v1/projects/{project_id}/issueFolderRel/query/by/issueId?folder_id={folderId}&version_id={versionId}', issues, Page.class, projectId, foldersId[0], versionId)
        then: '返回值'
        1 * testCaseService.getIssueInfoMap(_, _, _) >> new HashMap<>()
        resultFailure.statusCode.is2xxSuccessful()
        assert resultFailure.body.isEmpty()
    }

    def "QueryIssuesByParameter"() {
        given:
        SearchDTO searchDTO = new SearchDTO()
        IssueInfosDTO issueInfosDTO = new IssueInfosDTO()
        issueInfosDTO.setIssueId(11L)
        IssueInfosDTO issueInfosDTO1 = new IssueInfosDTO()
        issueInfosDTO.setIssueId(22L)
        Long[] issues = new Long[2]
        issues[0] = 11L
        issues[1] = 22L
        Map map = new HashMap()
        map.put(11L, issueInfosDTO)
        map.put(22L, issueInfosDTO1)

        when: '向查询issueFolderRel的接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issueFolderRel/query?folder_id={folderId}&version_id={versionId}&page={page}&size={size}', searchDTO, Page.class, projectId, foldersId[0], versionId, 1, 1)

        then: '返回值'
        1 * testCaseService.queryIssueIdsByOptions(_, _) >> issues
        1 * testCaseService.getIssueInfoMap(_, _, _) >> map
        entity.statusCode.is2xxSuccessful()
        List detailFolderRelDTOS = entity.body

        expect: "设置期望值"
        detailFolderRelDTOS.size() == 1

        when: '向testIssueFolderRel的查询接口发请求'
        def resultFailure = restTemplate.postForEntity('/v1/projects/{project_id}/issueFolderRel/query?folder_id={folderId}&version_id={versionId}&page={page}&size={size}', searchDTO, Page.class, projectId, null, null,null,null)
        then: '返回值'
        1 * testCaseService.queryIssueIdsByOptions(_, _) >>  new ArrayList<>()
        0 * testCaseService.getIssueInfoMap(_, _, _) >> map
        resultFailure.statusCode.is2xxSuccessful()
        assert resultFailure.body.isEmpty()
    }

    def "MoveIssue"() {
        given:
        //新文件夹2L
        foldersId.add(2L)
        IssueInfosDTO issueInfosDTO1 = new IssueInfosDTO()
        issueInfosDTO1.setIssueId(11L)
        issueInfosDTO1.setObjectVersionNumber(1L)
        IssueInfosDTO issueInfosDTO2 = new IssueInfosDTO()
        issueInfosDTO2.setIssueId(22L)
        issueInfosDTO2.setObjectVersionNumber(1L)
        List issueInfos = new ArrayList()
        issueInfos.add(issueInfosDTO1)
        issueInfos.add(issueInfosDTO2)
        TestIssueFolderRelDO origin = new TestIssueFolderRelDO()
        origin.setFolderId(foldersId[0])
        TestIssueFolderRelDO target = new TestIssueFolderRelDO()
        target.setFolderId(foldersId[1])

        IssueInfosDTO issueInfosDTO3 = new IssueInfosDTO()
        issueInfosDTO3.setIssueId(11L)
        issueInfosDTO3.setObjectVersionNumber(1L)
        List exceptionIssueInfos = new ArrayList()
        exceptionIssueInfos.add(issueInfosDTO3)

        when: '向查询testIssueFolderRel的接口发请求，将folder[0]的值移动到folder[1]中'
        restTemplate.put('/v1/projects/{project_id}/issueFolderRel/move?folder_id={folderId}&version_id={versionId}', issueInfos, projectId, foldersId[1], versionId)

        then: '返回值'
        1 * testCaseService.batchIssueToVersionTest(_, _, _)
        List originIFR = testIssueFolderRelMapper.select(origin)
        List targetIFR = testIssueFolderRelMapper.select(target)

        and:
        println("ori" + originIFR.get(0).getIssueId())
        println("tar" + targetIFR.get(0).getIssueId() + "|||" + targetIFR.get(1).getIssueId())

        expect: '期望值'
        originIFR.size() < 2
        targetIFR.size() > 1
    }

    def "CopyIssue"() {
        given:
        IssueInfosDTO issueInfosDTO1 = new IssueInfosDTO()
        issueInfosDTO1.setIssueId(11L)
        IssueInfosDTO issueInfosDTO2 = new IssueInfosDTO()
        issueInfosDTO2.setIssueId(22L)
        List issueInfos = new ArrayList()
        issueInfos.add(issueInfosDTO1)
        issueInfos.add(issueInfosDTO2)
        TestIssueFolderRelDO origin = new TestIssueFolderRelDO()
        origin.setFolderId(foldersId[1])
        TestIssueFolderRelDO target = new TestIssueFolderRelDO()
        target.setFolderId(foldersId[0])
        List issues = new ArrayList()
        issues.add(44L)
        issues.add(55L)

        when: '向查询issues的接口发请求,将folder[1]的值复制到folder[0]中'
        restTemplate.put('/v1/projects/{project_id}/issueFolderRel/copy?folder_id={folderId}&version_id={versionId}', issueInfos, projectId, foldersId[0], versionId)

        then: '返回值'
        testCaseService.batchCloneIssue(_, _, _) >> issues
        List originIFR = testIssueFolderRelMapper.select(origin)
        List targetIFR = testIssueFolderRelMapper.select(target)

        expect: '期望值'
        originIFR.size() < 4
        targetIFR.size() > 1
    }

    def "Delete"() {
        given:
        TestIssueFolderRelDO testIssueFolderRelDO1 = new TestIssueFolderRelDO()
        testIssueFolderRelDO1.setIssueId(11L)
        TestIssueFolderRelDO testIssueFolderRelDO2 = new TestIssueFolderRelDO()
        testIssueFolderRelDO2.setIssueId(22L)
        TestIssueFolderRelDO testIssueFolderRelDO3 = new TestIssueFolderRelDO()
        testIssueFolderRelDO3.setIssueId(33L)
        TestIssueFolderRelDO testIssueFolderRelDO4 = new TestIssueFolderRelDO()
        testIssueFolderRelDO4.setIssueId(44L)
        TestIssueFolderRelDO testIssueFolderRelDO5 = new TestIssueFolderRelDO()
        testIssueFolderRelDO5.setIssueId(55L)
        List issues = new ArrayList()
        issues.add(11L)
        issues.add(22L)
        issues.add(33L)
        issues.add(44L)
        issues.add(55L)
        when: '执行方法'
        restTemplate.put('/v1/projects/{project_id}/issueFolderRel/delete', issues, projectId)

        then: '返回值'
        def result1 = testIssueFolderRelMapper.selectOne(testIssueFolderRelDO1)
        def result2 = testIssueFolderRelMapper.selectOne(testIssueFolderRelDO2)
        def result3 = testIssueFolderRelMapper.selectOne(testIssueFolderRelDO3)
        def result4 = testIssueFolderRelMapper.selectOne(testIssueFolderRelDO4)
        def result5 = testIssueFolderRelMapper.selectOne(testIssueFolderRelDO5)

        expect: '期望值'
        result1 == null
        result2 == null
        result3 == null
        result4 == null
        result5 == null
    }

}