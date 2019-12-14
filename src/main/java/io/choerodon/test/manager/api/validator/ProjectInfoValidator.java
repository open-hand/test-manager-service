package io.choerodon.test.manager.api.validator;

import io.choerodon.test.manager.api.vo.agile.ProjectInfoVO;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/8.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class ProjectInfoValidator {

    public void verifyUpdateData(ProjectInfoVO projectInfoVO) {
        if (projectInfoVO.getInfoId() == null) {
            throw new CommonException("error.infoId.isNull");
        }
        if (projectInfoVO.getProjectId() == null) {
            throw new CommonException("error.projectId.isNull");
        }
        if (projectInfoVO.getProjectCode() == null) {
            throw new CommonException("error.projectCode.isNull");
        }
        if (projectInfoVO.getObjectVersionNumber() == null) {
            throw new CommonException("error.objectVersionNumber.isNull");
        }
    }

}
