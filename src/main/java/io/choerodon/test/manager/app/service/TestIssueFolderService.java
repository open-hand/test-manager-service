package io.choerodon.test.manager.app.service;

import java.util.List;

import io.choerodon.test.manager.api.vo.TestIssueFolderVO;
import io.choerodon.test.manager.api.vo.TestIssueFolderWithVersionNameVO;
import io.choerodon.test.manager.api.vo.TestTreeIssueFolderVO;
import io.choerodon.test.manager.infra.dto.TestIssueFolderDTO;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
public interface TestIssueFolderService {

    TestIssueFolderVO create(Long projectId,TestIssueFolderVO testIssueFolderVO);

    void delete(Long projectId, Long folderId);

    TestIssueFolderVO update(TestIssueFolderVO testIssueFolderVO);

    Long getDefaultFolderId(Long projectId, Long versionId);

    void copyFolder(Long projectId, Long targetFolderId, Long[] folderIds);

    void moveFolder(Long projectId, Long targetFolderId,TestIssueFolderVO issueFolderVO);

    List<TestIssueFolderVO> queryByParameter(Long projectId, Long versionId);

    List<TestIssueFolderWithVersionNameVO> queryByParameterWithVersionName(Long projectId, Long versionId);

    TestTreeIssueFolderVO queryTreeFolder(Long projectId);

    /**
     * 根据某一级文件夹查询下面所有的最低层文件夹
     * @param folderId
     * @return
     */
    List<TestIssueFolderDTO> queryChildFolder(Long folderId);

    /**
     * 查询所有项目id
     * @return
     */
    List<Long> queryProjectIdList();

    /**
     * 根据projectId查询项目下所有folder
     * @param projectId
     * @return
     */
    List<TestIssueFolderVO> queryListByProjectId(Long projectId);

    List<TestIssueFolderDTO> listFolderByFolderIds(List<Long> folderIds);
}
