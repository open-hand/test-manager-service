package io.choerodon.test.manager.domain.service;

import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleDTO;
import io.choerodon.test.manager.domain.service.impl.IExcelServiceImpl;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;
import java.util.Queue;

/**
 * Created by 842767365@qq.com on 8/9/18.
 */
public interface IExcelService {
	int populateVersionHeader(Sheet sheet, String projectName, String versionName, CellStyle rowStyle);

	int populateCycleHeader(Sheet sheet, int rowNum, TestCycleDTO cycle, CellStyle rowStyle);

	void populateSheetStyle(Sheet sheet);

	int populateBody(Sheet sheet, int column, List<TestCycleCaseDTO> cycleCases, Queue<CellStyle> rowStyles);

	int populateCycleCaseHeader(Sheet sheet, int rowNum, CellStyle rowStyle);

	Workbook getWorkBook(IExcelServiceImpl.WorkBookFactory.Mode mode);
}
