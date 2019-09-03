package io.choerodon.test.manager.api.controller.v1

import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.vo.TestCycleCaseAttachmentRelVO
import io.choerodon.test.manager.app.service.FileService
import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService
import io.choerodon.test.manager.infra.dto.TestCycleCaseAttachmentRelDTO
import io.choerodon.test.manager.infra.feign.FileFeignClient
import io.choerodon.test.manager.infra.mapper.TestCycleCaseAttachmentRelMapper
import org.mockito.Matchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Qualifier

import static org.mockito.Matchers.anyString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 User: wangxiang
 Date: 2019/8/28
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestAttachmentControllerSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    FileService fileService

    @Autowired
    TestCycleCaseAttachmentRelMapper attachmentRelMapper

    @Autowired
    @Qualifier("fileFeignClient")
    private FileFeignClient fileFeignClient

    @Shared
    Long projectId = 1L


    def "UploadFile"() {
        given: '上传附件'
        FileSystemResource resource = new FileSystemResource(new File("D:\\test1.txt"))
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>()
        param.add("file", resource)
        param.add("fileName", "test1.txt")
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(param, null)
        when: '发送请求'
        def entity = restTemplate.exchange("/v1/projects/{project_id}/test/case/attachment?bucket_name={bucket_name}&attachmentLinkId={attachmentLinkId}&attachmentType={attachmentType}",
                HttpMethod.POST, httpEntity, List, projectId, "bucket_name", 5650L, "CASE_STEP")

        then:
        1 * fileService.uploadFile(_, _, _) >> new ResponseEntity<String>(
                "https://minio.choerodon.com.cn/agile-service/file_56a005f56a584047b538d5bf84b17d70_blob.png", HttpStatus.OK)
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<TestCycleCaseAttachmentRelVO> list = entity.body

        expect: '期望值'
        list.size() == 1
    }

    def "RemoveAttachment"() {
        given: '删除附件'
        List<TestCycleCaseAttachmentRelDTO> list = attachmentRelMapper.selectAll()
        Mockito.when(fileFeignClient.deleteFile(anyString(), anyString())).thenReturn(new ResponseEntity<>(HttpStatus.OK))
        when: '发送请求'
        try {
            def entity = restTemplate.exchange("/v1/projects/{project_id}/test/case/attachment/delete/bucket/{bucketName}/attach/{attachId}",
                    HttpMethod.DELETE,
                    null,
                    ResponseEntity.class,
                    projectId,
                    "bucket_name",
                    5650L)
        } catch (Exception e) {
            expectObject = e
        }

        then:
        if (expectObject != null) {
            attachmentRelMapper.selectByPrimaryKey(issueAttachmentId) == expectObject
        }

        where: '期望值'
        issueAttachmentId | expectObject
        5650L             | null
        0L                | Exception
    }
}
