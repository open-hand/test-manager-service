package io.choerodon.test.manager.app.service.impl

import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import org.springframework.aop.framework.AopContext
import spock.lang.Specification

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Sputnik.class)
@PrepareForTest([AopContext.class])
class FileServiceImplSpec extends Specification {

    def setup(){
        PowerMockito.mock(file)
    }

    def "UploadFile"() {
    }

    def "DeleteFile"() {
    }
}
