package io.choerodon.test.manager.infra.mapper;

import io.choerodon.test.manager.infra.dataobject.TestCycleCaseDefectRelDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseDefectRelMapper extends BaseMapper<TestCycleCaseDefectRelDO> {
	List<TestCycleCaseDefectRelDO> queryInIssues(@Param("issues") Long[] issues);
}
