package io.choerodon.test.manager.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonFactory;
import io.choerodon.agile.api.dto.ProductVersionDTO;
import io.choerodon.agile.api.dto.UserDO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.dto.TestCycleDTO;
import io.choerodon.test.manager.app.service.TestCycleService;
import io.choerodon.test.manager.app.service.UserService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
import io.choerodon.test.manager.domain.service.ITestCycleService;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleEFactory;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import io.choerodon.agile.api.dto.ProductVersionPageDTO;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCycleServiceImpl implements TestCycleService {
	@Autowired
	ITestCycleService iTestCycleService;

	@Autowired
	ProductionVersionClient productionVersionClient;

	@Autowired
	UserService userService;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public TestCycleDTO insert(TestCycleDTO testCycleDTO) {
		return ConvertHelper.convert(iTestCycleService.insert(ConvertHelper.convert(testCycleDTO, TestCycleE.class)), TestCycleDTO.class);

	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void delete(TestCycleDTO testCycleDTO) {
		iTestCycleService.delete(ConvertHelper.convert(testCycleDTO, TestCycleE.class));

	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public TestCycleDTO update(TestCycleDTO testCycleDTO) {
		TestCycleE temp = TestCycleEFactory.create();
		temp.setCycleId(testCycleDTO.getCycleId());
		TestCycleE temp1 = temp.queryOne();
		if (temp1.getType().equals(TestCycleE.FOLDER)) {
			temp1.setCycleName(testCycleDTO.getCycleName());
			temp1.setObjectVersionNumber(testCycleDTO.getObjectVersionNumber());
		} else if (temp1.getType().equals(TestCycleE.CYCLE)) {
			Optional.ofNullable(testCycleDTO.getBuild()).ifPresent(v -> temp1.setBuild(v));
			Optional.ofNullable(testCycleDTO.getCycleName()).ifPresent(v -> temp1.setCycleName(v));
			Optional.ofNullable(testCycleDTO.getDescription()).ifPresent(v -> temp1.setDescription(v));
			Optional.ofNullable(testCycleDTO.getEnvironment()).ifPresent(v -> temp1.setEnvironment(v));
			Optional.ofNullable(testCycleDTO.getFromDate()).ifPresent(v -> temp1.setFromDate(v));
			Optional.ofNullable(testCycleDTO.getToDate()).ifPresent(v -> temp1.setToDate(v));
			Optional.ofNullable(testCycleDTO.getObjectVersionNumber()).ifPresent(v -> temp1.setObjectVersionNumber(v));
		}
		return ConvertHelper.convert(iTestCycleService.update(temp1), TestCycleDTO.class);

	}

	public TestCycleDTO getOneCycle(Long cycleId) {
		TestCycleE testCycleE = TestCycleEFactory.create();
		testCycleE.setCycleId(cycleId);
		testCycleE.querySelf();
		return ConvertHelper.convert(testCycleE.queryOne(), TestCycleDTO.class);
	}

	@Override
	public JSONObject getTestCycle(Long projectId) {
		ResponseEntity<List<ProductVersionDTO>> dto = productionVersionClient.listByProjectId(projectId);
		List<ProductVersionDTO> versions = dto.getBody();
//		List<ProductVersionDTO> versions = new ArrayList<>();
//		ProductVersionDTO v1 = new ProductVersionDTO();
//		v1.setStatusName("111");
//		v1.setName("1111");
//		v1.setVersionId(new Long(167));
//		versions.add(v1);
		if (versions.size() == 0) {
			return new JSONObject();
		}
		JSONObject root = new JSONObject();
		JSONArray versionStatus = new JSONArray();
		root.put("versions", versionStatus);

		List<TestCycleDTO> cycles = ConvertHelper.convertList(iTestCycleService.queryCycleWithBar(versions.stream().map(v -> v.getVersionId()).toArray(Long[]::new)), TestCycleDTO.class);
		Long[] usersId = cycles.stream().map(v -> v.getCreatedBy()).toArray(Long[]::new);
		Map users = userService.query(usersId);
		setUsers(users, cycles);
		initVersionTree(versionStatus, versions, cycles);

		return root;
	}

	private void setUsers(Map<Long, UserDO> users, List<TestCycleDTO> dtos) {
		dtos.forEach(v -> {
			if (v.getCreatedBy() != null && v.getCreatedBy().longValue() != 0) {
				UserDO u = users.get(v.getCreatedBy());
				v.setCreatedName(u.getLoginName() + " " + u.getRealName());
			}
		});
	}

	private void initVersionTree(JSONArray versionStatus, List<ProductVersionDTO> versionDTOList, List<TestCycleDTO> cycleDTOList) {
		Map<String, JSONObject> versionsMap = new HashMap<>();

		for (ProductVersionDTO versionDTO : versionDTOList) {
			JSONObject version;
			if (!versionsMap.containsKey(versionDTO.getStatusName())) {
				version = createVersionNode(versionDTO.getStatusName(), "0-" + String.valueOf(versionsMap.size()));
				versionStatus.add(version);
				versionsMap.put(versionDTO.getStatusName(), version);
			} else {
				version = versionsMap.get(versionDTO.getStatusName());
			}

			JSONArray versionNames = version.getJSONArray("children");

			String nowStatusHeight = version.get("key").toString();
			String nowNamesHeight = String.valueOf(versionNames.size());
			JSONObject versionName = createVersionNode(versionDTO.getName(), nowStatusHeight + "-" + nowNamesHeight);
			versionNames.add(versionName);

			initCycleTree(versionName.getJSONArray("children"), versionName.get("key").toString(), versionDTO.getVersionId(), cycleDTOList);
		}
	}


	private JSONObject createVersionNode(String title, String height) {
		JSONObject version = new JSONObject();
		version.put("title", title);
		version.put("key", height);
		JSONArray versionNames = new JSONArray();
		version.put("children", versionNames);
		return version;
	}

	private JSONObject createCycle(TestCycleDTO testCycleDTO, String height) {
		JSONObject version = new JSONObject();
		version.put("title", testCycleDTO.getCycleName());
		version.put("environment", testCycleDTO.getEnvironment());
		version.put("description", testCycleDTO.getDescription());
		version.put("build", testCycleDTO.getBuild());
		version.put("type", testCycleDTO.getType());
		version.put("versionId", testCycleDTO.getVersionId());
		version.put("cycleId", testCycleDTO.getCycleId());
		version.put("createdName", testCycleDTO.getCreatedName());
		version.put("toDate", testCycleDTO.getToDate());
		version.put("fromDate", testCycleDTO.getFromDate());
		version.put("cycleCaseList", testCycleDTO.getCycleCaseList());
		version.put("objectVersionNumber", testCycleDTO.getObjectVersionNumber());
		version.put("key", height);
		JSONArray versionNames = new JSONArray();
		version.put("children", versionNames);
		return version;
	}


	private void initCycleTree(JSONArray cycles, String height, Long versionId, List<TestCycleDTO> cycleDTOList) {

		cycleDTOList.stream().filter(cycleDTO -> cycleDTO.getVersionId().equals(versionId) && StringUtils.equals(cycleDTO.getType(), TestCycleE.CYCLE))
				.forEach(v -> {
					JSONObject cycle = createCycle(v, height + "-" + cycles.size());
					cycles.add(cycle);
					initCycleFolderTree(cycle.getJSONArray("children"), cycle.get("key").toString(), v.getCycleId(), cycleDTOList);
				});

		cycleDTOList.stream().filter(cycleDTO -> cycleDTO.getVersionId().equals(versionId) && StringUtils.equals(cycleDTO.getType(), TestCycleE.TEMP))
				.forEach(v -> {
					JSONObject cycle = createCycle(v, height + "-" + cycles.size());
					cycles.add(cycle);
				});
	}


	private void initCycleFolderTree(JSONArray folders, String height, Long parentId, List<TestCycleDTO> cycleDTOList) {
		cycleDTOList.stream().filter(v -> v.getParentCycleId() == parentId).forEach(u ->
				folders.add(createCycle(u, height + "-" + folders.size()))
		);
	}



	@Override
	public List<TestCycleDTO> filterCycleWithBar(String filter) {

		JSONObject object = JSON.parseObject(filter);
		ResponseEntity<List<ProductVersionDTO>> dto = productionVersionClient.listByProjectId(object.getLong("projectId"));
		List<ProductVersionDTO> versions = dto.getBody();

		if (versions.size() == 0) {
			return new ArrayList<>();
		}
		List<TestCycleDTO> cycles = ConvertHelper.convertList(iTestCycleService.filterCycleWithBar(object.getString("parameter"),
				versions.stream().map(v -> v.getVersionId()).toArray(Long[]::new)), TestCycleDTO.class);
		cycles.forEach(v -> {
			for (ProductVersionDTO u : versions) {
				if (v.getVersionId().equals(u.getVersionId())) {
					v.setVersionName(u.getName());
					v.setVersionStatusName(u.getStatusName());
					break;
				}
			}
		});
		return cycles;
	}

	@Override
	public ResponseEntity<Page<ProductVersionPageDTO>> getTestCycleVersion(Long projectId, Map<String, Object> searchParamMap) {
		return productionVersionClient.listByOptions(projectId, searchParamMap);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public TestCycleDTO cloneCycle(Long cycleId, String cycleName, Long projectId) {
		TestCycleE testCycleE = TestCycleEFactory.create();
		testCycleE.setCycleId(cycleId);
		List<TestCycleE> list = iTestCycleService.querySubCycle(testCycleE);
		if (!(list.size() == 1 && list.get(0).getCycleName() != cycleName)) {
			throw new CommonException("error.test.cycle.clone.duplicate.name");
		}
		TestCycleE newTestCycleE = TestCycleEFactory.create();
		newTestCycleE.setCycleName(cycleName);
		newTestCycleE.setType(TestCycleE.CYCLE);
		return ConvertHelper.convert(iTestCycleService.cloneCycle(list.get(0), newTestCycleE, projectId), TestCycleDTO.class);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public TestCycleDTO cloneFolder(Long cycleId, TestCycleDTO testCycleDTO, Long projectId) {
		TestCycleE testCycleE = TestCycleEFactory.create();
		testCycleE.setCycleId(cycleId);
		List<TestCycleE> list = iTestCycleService.querySubCycle(testCycleE);
		if (list.size() != 1) {
			throw new CommonException("error.test.cycle.clone.");
		}

		return ConvertHelper.convert(iTestCycleService.cloneFolder(list.get(0), ConvertHelper.convert(testCycleDTO, TestCycleE.class), projectId), TestCycleDTO.class);
	}

	@Override
	public List<TestCycleDTO> getCyclesByVersionId(Long versionId) {
		TestCycleE testCycleE = TestCycleEFactory.create();
		testCycleE.setVersionId(versionId);
		return ConvertHelper.convertList(testCycleE.getCyclesByVersionId(), TestCycleDTO.class);
	}

	@Override
	public List<TestCycleDTO> getFolderByCycleId(Long cycleId) {
		TestCycleE testCycleE = TestCycleEFactory.create();
		testCycleE.setCycleId(cycleId);
		return ConvertHelper.convertList(testCycleE.getChildFolder(), TestCycleDTO.class);
	}
}
