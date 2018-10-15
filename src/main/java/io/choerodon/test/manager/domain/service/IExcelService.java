package io.choerodon.test.manager.domain.service;

import io.choerodon.test.manager.infra.common.utils.ExcelUtil;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;
import java.util.Queue;

/**
 * Created by 842767365@qq.com on 8/9/18.
 */
public interface IExcelService<T,R> {
	int populateVersionHeader(Sheet sheet, String projectName, String versionName, CellStyle rowStyle);

	int populateHeader(Sheet sheet, int rowNum, T cycle, CellStyle rowStyle);

	void populateSheetStyle(Sheet sheet);

	int populateBody(Sheet sheet, int column, List<R> cycleCases, Queue<CellStyle> rowStyles);

	Workbook getWorkBook(ExcelUtil.Mode mode);
}
