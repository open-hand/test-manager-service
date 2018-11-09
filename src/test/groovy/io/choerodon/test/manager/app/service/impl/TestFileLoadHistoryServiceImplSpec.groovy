package io.choerodon.test.manager.app.service.impl

import io.choerodon.core.convertor.ConvertHelper
import io.choerodon.core.oauth.CustomUserDetails
import io.choerodon.core.oauth.DetailsHelper
import io.choerodon.test.manager.api.dto.TestFileLoadHistoryDTO
import io.choerodon.test.manager.app.service.TestFileLoadHistoryService
import io.choerodon.test.manager.domain.service.ITestFileLoadHistoryService
import io.choerodon.test.manager.domain.test.manager.entity.TestFileLoadHistoryE
import io.choerodon.test.manager.infra.common.utils.SpringUtil
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import org.springframework.context.ApplicationContext
import spock.lang.Specification

@PrepareForTest([DetailsHelper, SpringUtil, ConvertHelper])
@RunWith(PowerMockRunner)
@PowerMockRunnerDelegate(Sputnik)
class TestFileLoadHistoryServiceImplSpec extends Specification {

    def "queryLatestImportIssueHistory"() {
        given:
        CustomUserDetails userDetails = new CustomUserDetails("test", "12345678", Collections.emptyList())
        userDetails.userId = 0L

        TestFileLoadHistoryDTO testFileLoadHistoryDTO = new TestFileLoadHistoryDTO()
        TestFileLoadHistoryE testFileLoadHistoryE = new TestFileLoadHistoryE()

        ApplicationContext ctx = PowerMockito.mock(ApplicationContext)
        PowerMockito.when(ctx.getBean(TestFileLoadHistoryE)).thenReturn(testFileLoadHistoryE)
        PowerMockito.mockStatic(DetailsHelper)
        PowerMockito.when(DetailsHelper.getUserDetails()).thenReturn(userDetails)
        PowerMockito.mockStatic(SpringUtil)
        PowerMockito.when(SpringUtil.getApplicationContext()).thenReturn(ctx)
        PowerMockito.mockStatic(ConvertHelper)
        PowerMockito.when(ConvertHelper.convert(testFileLoadHistoryE, TestFileLoadHistoryDTO)).thenReturn(testFileLoadHistoryDTO)

        ITestFileLoadHistoryService iTestFileLoadHistoryService = PowerMockito.mock(ITestFileLoadHistoryService)
        PowerMockito.when(iTestFileLoadHistoryService.queryLatestImportIssueHistory(testFileLoadHistoryE)).thenReturn(testFileLoadHistoryE)

        TestFileLoadHistoryService loadHistoryService = new TestFileLoadHistoryServiceImpl(iTestFileLoadHistoryService: iTestFileLoadHistoryService)

        when:
        TestFileLoadHistoryDTO loadHistoryDTO = loadHistoryService.queryLatestImportIssueHistory()
        then:
        Objects.equals(loadHistoryDTO, testFileLoadHistoryDTO)

        when:
        PowerMockito.when(iTestFileLoadHistoryService.queryLatestImportIssueHistory(testFileLoadHistoryE)).thenReturn(null)
        loadHistoryDTO = loadHistoryService.queryLatestImportIssueHistory()
        then:
        loadHistoryDTO == null

    }
}
