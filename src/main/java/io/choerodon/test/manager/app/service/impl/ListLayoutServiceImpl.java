package io.choerodon.test.manager.app.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.test.manager.api.vo.IssuePersonalSortVO;
import io.choerodon.test.manager.api.vo.ListLayoutColumnRelVO;
import io.choerodon.test.manager.api.vo.ListLayoutVO;
import io.choerodon.test.manager.app.service.ListLayoutService;
import io.choerodon.test.manager.infra.dto.TestPersonalSortDTO;
import io.choerodon.test.manager.infra.dto.ListLayoutColumnRelDTO;
import io.choerodon.test.manager.infra.dto.ListLayoutDTO;
import io.choerodon.test.manager.infra.enums.TestFieldMapping;
import io.choerodon.test.manager.infra.mapper.IssuePersonalSortMapper;
import io.choerodon.test.manager.infra.mapper.ListLayoutColumnRelMapper;
import io.choerodon.test.manager.infra.mapper.ListLayoutMapper;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaotianxin
 * @date 2021-05-07 14:20
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ListLayoutServiceImpl implements ListLayoutService {
    @Autowired
    private ListLayoutMapper listLayoutMapper;
    @Autowired
    private ListLayoutColumnRelMapper listLayoutColumnRelMapper;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IssuePersonalSortMapper issuePersonalSortMapper;

    @Override
    public ListLayoutVO save(Long organizationId, Long projectId, ListLayoutVO listLayoutVO) {
        if (ObjectUtils.isEmpty(listLayoutVO.getApplyType())) {
            throw new CommonException("error.list.layout.apply.type.null");
        }
        Long userId = DetailsHelper.getUserDetails().getUserId();
        ListLayoutDTO layoutDTO = new ListLayoutDTO(listLayoutVO.getApplyType(), userId, projectId, organizationId);
        List<ListLayoutDTO> layoutDTOS = listLayoutMapper.select(layoutDTO);
        if (CollectionUtils.isEmpty(layoutDTOS)) {
            baseInsert(layoutDTO);
        } else {
            layoutDTO = layoutDTOS.get(0);
        }
        saveColumnRel(organizationId, projectId, layoutDTO.getId(), listLayoutVO.getListLayoutColumnRelVOS());
        return queryByApplyType(organizationId, projectId, listLayoutVO.getApplyType());
    }

    private void saveColumnRel(Long organizationId, Long projectId, Long layoutId, List<ListLayoutColumnRelVO> listLayoutColumnRelVOS) {
        ListLayoutColumnRelDTO listLayoutColumnRelDTO = new ListLayoutColumnRelDTO();
        listLayoutColumnRelDTO.setProjectId(projectId);
        listLayoutColumnRelDTO.setOrganizationId(organizationId);
        listLayoutColumnRelDTO.setLayoutId(layoutId);
        List<ListLayoutColumnRelDTO> layoutColumnRelDTOS = listLayoutColumnRelMapper.select(listLayoutColumnRelDTO);
        if (!Objects.equals(projectId, 0L)) {
            //项目层
            deleteIssuePersonalSortIfExisted(layoutColumnRelDTOS, listLayoutColumnRelVOS, projectId, organizationId);
        }
        if (!CollectionUtils.isEmpty(layoutColumnRelDTOS)) {
            listLayoutColumnRelMapper.delete(listLayoutColumnRelDTO);
        }
        listLayoutColumnRelVOS.forEach(v -> {
            ListLayoutColumnRelDTO layoutColumnRelDTO = modelMapper.map(v, ListLayoutColumnRelDTO.class);
            layoutColumnRelDTO.setOrganizationId(organizationId);
            layoutColumnRelDTO.setLayoutId(layoutId);
            layoutColumnRelDTO.setProjectId(projectId);
            if (listLayoutColumnRelMapper.insertSelective(layoutColumnRelDTO) != 1) {
                throw new CommonException("error.list.layout.column.rel.insert");
            }
        });
    }

    private void deleteIssuePersonalSortIfExisted(List<ListLayoutColumnRelDTO> existedLayoutColumnRelList,
                                                  List<ListLayoutColumnRelVO> inputLayoutColumnRelList,
                                                  Long projectId,
                                                  Long organizationId) {
        Set<String> inputDisplayCodes =
                inputLayoutColumnRelList.stream()
                        .filter(x -> Boolean.TRUE.equals(x.getDisplay()))
                        .map(ListLayoutColumnRelVO::getColumnCode)
                        .collect(Collectors.toSet());
        Set<String> deleteCodes = new HashSet<>();
        existedLayoutColumnRelList.forEach(rel -> {
            String columnCode = rel.getColumnCode();
            boolean display = rel.getDisplay();
            if (!inputDisplayCodes.contains(columnCode) && display) {
                deleteCodes.add(columnCode);
            }
        });
        String projectCodePrefix = "pro_";
        String organizationCodePrefix = "org_";
        String customFieldSortPrefix = "foundation.";
        if (!deleteCodes.isEmpty()) {
            TestPersonalSortDTO dto = new TestPersonalSortDTO();
            dto.setOrganizationId(organizationId);
            dto.setProjectId(projectId);
            dto.setUserId(DetailsHelper.getUserDetails().getUserId());
            dto.setBusinessType("gantt");
            List<TestPersonalSortDTO> sorts = issuePersonalSortMapper.select(dto);
            if (sorts.isEmpty()) {
                return;
            }
            TestPersonalSortDTO sort = sorts.get(0);
            String sortJson = sort.getSortJson();
            if (StringUtils.isEmpty(sortJson)) {
                return;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, IssuePersonalSortVO> sortMap = new LinkedHashMap<>();
            try {
                List<IssuePersonalSortVO> issuePersonalSorts =
                        objectMapper.readValue(sortJson, new TypeReference<List<IssuePersonalSortVO>>() {});
                issuePersonalSorts.forEach(s -> sortMap.put(s.getProperty(), s));
            } catch (IOException e) {
                throw new CommonException("error.gantt.sortJson.deserialization", e);
            }
            deleteCodes.forEach(code -> {
                String property;
                if (code.startsWith(projectCodePrefix) || code.startsWith(organizationCodePrefix)) {
                    property = customFieldSortPrefix + code;
                } else {
                    property = TestFieldMapping.getSortFieldByCode(code);
                    if (property == null) {
                        property = code;
                    }
                }
                if (sortMap.containsKey(property)) {
                    sortMap.remove(property);
                }
            });
            List<IssuePersonalSortVO> sortList = new ArrayList<>(sortMap.values());
            try {
                sortJson = objectMapper.writeValueAsString(sortList);
                sort.setSortJson(sortJson);
                if (issuePersonalSortMapper.updateByPrimaryKey(sort) != 1) {
                    throw new CommonException("error.gantt.sort.save");
                }
            } catch (JsonProcessingException e) {
                throw new CommonException("error.gantt.sortJson.serialization", e);
            }
        }
    }

    private ListLayoutDTO baseInsert(ListLayoutDTO layoutDTO) {
        if (listLayoutMapper.insertSelective(layoutDTO) != 1) {
            throw new CommonException("error.list.layout.insert");
        }
        return listLayoutMapper.selectByPrimaryKey(layoutDTO.getId());
    }

    @Override
    public ListLayoutVO queryByApplyType(Long organizationId, Long projectId, String applyType) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        ListLayoutDTO listLayoutDTO = new ListLayoutDTO(applyType, userId, projectId, organizationId);
        List<ListLayoutDTO> listLayoutDTOS = listLayoutMapper.select(listLayoutDTO);
        if (CollectionUtils.isEmpty(listLayoutDTOS)) {
            return null;
        }
        ListLayoutDTO layoutDTO = listLayoutDTOS.get(0);
        ListLayoutVO layoutVO = modelMapper.map(layoutDTO, ListLayoutVO.class);
        ListLayoutColumnRelDTO listLayoutColumnRelDTO = new ListLayoutColumnRelDTO(layoutVO.getId(), projectId, organizationId);
        List<ListLayoutColumnRelDTO> list = listLayoutColumnRelMapper.select(listLayoutColumnRelDTO);
        if (!CollectionUtils.isEmpty(list)) {
            layoutVO.setListLayoutColumnRelVOS(modelMapper.map(list, new TypeToken<List<ListLayoutColumnRelVO>>() {
            }.getType()));
        }
        return layoutVO;
    }
}
