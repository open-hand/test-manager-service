package io.choerodon.test.manager.domain.service.impl

import io.choerodon.core.exception.CommonException
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.app.service.FileService
import io.choerodon.test.manager.domain.service.ITestCycleCaseAttachmentRelService
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE
import io.choerodon.test.manager.infra.feign.FileFeignClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cglib.core.ReflectUtils
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.multipart.commons.CommonsMultipartFile
import spock.lang.Specification
import sun.reflect.Reflection

import java.lang.reflect.Field

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by 842767365@qq.com on 8/22/18.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ITestCycleCaseAttachmentRelServiceImplSpec extends Specification {
    @Autowired
    ITestCycleCaseAttachmentRelService attachmentRelService

    @Autowired
    FileService client

    def "Delete"() {
        given:
        client.uploadFile(_, _, _) >> new ResponseEntity("url", HttpStatus.OK)
        TestCycleCaseAttachmentRelE attachmentRelE = attachmentRelService.upload("buck", "fileName3", null, 1L, "CYCLE_CASE", "comment")
        when:
        attachmentRelService.delete("buck", attachmentRelE.getId())
        then:
        1 * client.deleteFile(_, _) >> new ResponseEntity("url", HttpStatus.OK)
    }

    def "Upload"() {
        when:
        TestCycleCaseAttachmentRelE attachmentRelE = attachmentRelService.upload("buck", "fileName1", null, 1L, "CYCLE_CASE", "comment")
        then:
        1 * client.uploadFile(_, _, _) >> new ResponseEntity("url", HttpStatus.OK)
        attachmentRelE.getId() != null
        when:
        attachmentRelService.upload("buck", "fileName2", null, 1L, "CYCLE_CASE", "comment")
        then:
        1 * client.uploadFile(_, _, _) >> new ResponseEntity("url", HttpStatus.BAD_REQUEST)
        thrown(CommonException)

    }
}
