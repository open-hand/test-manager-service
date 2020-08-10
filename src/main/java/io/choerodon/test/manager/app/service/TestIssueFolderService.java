package io.choerodon.test.manager.app.service;

import java.util.List;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.test.manager.api.vo.TestIssueFolderVO;
import io.choerodon.test.manager.api.vo.TestTreeIssueFolderVO;
import io.choerodon.test.manager.api.vo.event.ProjectEvent;
import io.choerodon.test.manager.infra.dto.TestIssueFolderDTO;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
public interface TestIssueFolderService {

    TestIssueFolderVO create(Long projectId, TestIssueFolderVO testIssueFolderVO);

    void delete(Long projectId, Long folderId);

    TestIssueFolderVO update(TestIssueFolderVO testIssueFolderVO);

    String moveFolder(Long projectId, Long targetFolderId, TestIssueFolderVO issueFolderVO);

    TestTreeIssueFolderVO queryTreeFolder(Long projectId);

    /**
     * 根据某一级文件夹查询下面所有的最低层文件夹
     *
     * @param folderId
     * @return
     */
    List<TestIssueFolderDTO> queryChildFolder(Long folderId);

    /**
     * 查询所有项目id
     *
     * @return
     */
    List<Long> queryProjectIdList();

    List<TestIssueFolderDTO> listFolderByFolderIds(Long projectId, List<Long> folderIds);

    /**
     * 创建项目时,用例库初始化文件夹
     *
     * @param projectEvent
     */
    void initializationFolderInfo(ProjectEvent projectEvent);

    List<TestIssueFolderDTO> listByProject(Long projectId);

    /**
     * 复制测试用例文件夹
     *
     * @param projectId 项目id
     * @param folderId  文件夹id
     * @return 复制后的文件夹对象
     */
    TestIssueFolderDTO cloneFolder(Long projectId, Long folderId);

    TestIssueFolderDTO cloneCurrentFolder(Long projectId, Long folderId);

    void wrapCloneFolder(String payload);

    void cloneChildrenFolderAndCase(Long projectId, TestIssueFolderDTO newFolder);
}
