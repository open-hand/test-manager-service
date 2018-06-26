//package io.choerodon.test.manager.domain.test.manager.convertor;
//
//import io.choerodon.test.manager.api.dto.TestCaseDTO;
//import io.choerodon.test.manager.domain.test.manager.factory.TestCaseEFactory;
//import io.choerodon.test.manager.domain.test.manager.entity.TestCaseE;
//import io.choerodon.test.manager.infra.dataobject.TestCaseDO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by 842767365@qq.com on 6/8/18.
// */
//
//@Component
//public class TestCaseConvertor implements ConvertorI<TestCaseE, TestCaseDO, TestCaseDTO> {
//
//	@Override
//	public TestCaseE doToEntity(TestCaseDO dataObject) {
//		TestCaseE testCaseE=TestCaseEFactory.create();
//		BeanUtils.copyProperties(dataObject,testCaseE);
//		return testCaseE;
//	}
//
//	@Override
//	public TestCaseDO entityToDo(TestCaseE entity) {
//		TestCaseDO taskDO = new TestCaseDO();
//		BeanUtils.copyProperties(entity, taskDO);
//		return taskDO;
//	}
//
//	@Override
//	public TestCaseE dtoToEntity(TestCaseDTO testCaseDTO) {
//		TestCaseE testCaseE=TestCaseEFactory.create();
//		BeanUtils.copyProperties(testCaseDTO,testCaseE);
//		return testCaseE;
//	}
//
//	@Override
//	public TestCaseDTO entityToDto(TestCaseE testCaseE) {
//		TestCaseDTO entity = new TestCaseDTO();
//		BeanUtils.copyProperties(testCaseE, entity);
//		return entity;
//	}
//
//}
