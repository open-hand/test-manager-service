package com.test.devops.app.service;

import com.test.devops.api.dto.TestStatusDTO;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/25/18.
 */
public interface TestStatusService {
	List<TestStatusDTO> query(TestStatusDTO testStatusDTO);

	TestStatusDTO insert(TestStatusDTO testStatusDTO);

	void delete(TestStatusDTO testStatusDTO);

	TestStatusDTO update(TestStatusDTO testStatusDTO);
}
