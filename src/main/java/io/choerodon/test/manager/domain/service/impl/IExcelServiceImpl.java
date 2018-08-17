package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.agile.api.dto.ComponentIssueRelDTO;
import io.choerodon.agile.api.dto.LabelIssueRelDTO;
import io.choerodon.agile.api.dto.UserDO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseStepDTO;
import io.choerodon.test.manager.api.dto.TestCycleDTO;
import io.choerodon.test.manager.domain.service.IExcelService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Created by 842767365@qq.com on 8/9/18.
 */

@Service
public class IExcelServiceImpl implements IExcelService {
	Log log = LogFactory.getLog(this.getClass());

	private enum CycleCaseHeader {
		COLUMN1("文件夹"), COLUMN2("用例编号"), COLUMN3("用例概要"), COLUMN4("执行状态"), COLUMN5("说明"), COLUMN6("缺陷编号概要"), COLUMN7("模块"), COLUMN8("标签"), COLUMN9("执行方"),
		COLUMN10("执行时间"), COLUMN11("测试步骤"), COLUMN12("测试数据"), COLUMN13("预期结果"), COLUMN14("步骤状态"), COLUMN15("步骤注释"), COLUMN16("步骤缺陷编号概要");
		private String chinese;
		private String us;

		CycleCaseHeader(String chinese) {
			this.chinese = chinese;
		}

		public String getValue(String type) {
			switch (type) {
				case "zn_ch":
					return chinese;
				case "us_":
					return us;
				default:
					return chinese;
			}
		}
	}

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public int populateBody(Sheet sheet, int column, List<TestCycleCaseDTO> cycleCases, Queue<CellStyle> rowStyles) {

		for (TestCycleCaseDTO cycleCase : cycleCases) {
			CellStyle style;
			if (ObjectUtils.isEmpty(rowStyles)) {
				style = null;
			} else {
				style = rowStyles.poll();
				rowStyles.offer(style);
			}

			column = populateCycleCase(sheet, column, cycleCase, style);
		}
		return column;
	}


	public int populateCycleCase(Sheet sheet, int columnNum, TestCycleCaseDTO cycleCase, CellStyle rowStyles) {
		Row row = WorkBookFactory.createRow(sheet, columnNum, rowStyles);
		Optional.ofNullable(cycleCase.getFolderName()).ifPresent(v -> WorkBookFactory.createCell(row, 0, WorkBookFactory.CellType.TEXT, v));
		Optional.ofNullable(cycleCase.getIssueInfosDTO().getIssueName()).ifPresent(v -> WorkBookFactory.createCell(row, 1, WorkBookFactory.CellType.TEXT, v));
		Optional.ofNullable(cycleCase.getIssueInfosDTO().getSummary()).ifPresent(v -> WorkBookFactory.createCell(row, 2, WorkBookFactory.CellType.TEXT, v));
		Optional.ofNullable(cycleCase.getExecutionStatusName()).ifPresent(v -> WorkBookFactory.createCell(row, 3, WorkBookFactory.CellType.TEXT, v));
		Optional.ofNullable(cycleCase.getComment()).ifPresent(v -> WorkBookFactory.createCell(row, 4, WorkBookFactory.CellType.TEXT, v));
		Optional.ofNullable(getDefectsCell(cycleCase.getDefects(), TestCycleCaseDefectRelE.CYCLE_CASE)).ifPresent(v -> WorkBookFactory.createCell(row, 5, WorkBookFactory.CellType.TEXT, v));
		Optional.ofNullable(getLabelCell(cycleCase.getIssueInfosDTO().getLabelIssueRelDTOList())).ifPresent(v -> WorkBookFactory.createCell(row, 6, WorkBookFactory.CellType.TEXT, v));
		Optional.ofNullable(getModuleCell(cycleCase.getIssueInfosDTO().getComponentIssueRelDTOList())).ifPresent(v -> WorkBookFactory.createCell(row, 7, WorkBookFactory.CellType.TEXT, v));

		Optional.ofNullable(cycleCase.getAssigneeUser()).ifPresent(v -> WorkBookFactory.createCell(row, 8, WorkBookFactory.CellType.TEXT, v.getRealName()));
		Optional.ofNullable(cycleCase.getLastUpdateDate()).ifPresent(v -> WorkBookFactory.createCell(row, 9, WorkBookFactory.CellType.DATE, dateFormat.format(v)));

		return columnNum + populateCycleCaseStep(sheet, columnNum, cycleCase.getCycleCaseStep(), cycleCase.getDefects(), rowStyles) + 1;
	}

	public int populateCycleCaseStep(Sheet sheet, int column, List<TestCycleCaseStepDTO> cycleCaseStep, List<TestCycleCaseDefectRelDTO> defects, CellStyle rowStyles) {
		if (cycleCaseStep == null || cycleCaseStep.isEmpty()) {
			return 0;
		}
		for (int i = 0; i < cycleCaseStep.size(); i++) {
			Row row;
			if (i == 0) {
				row = sheet.getRow(column);
			} else {
				row = WorkBookFactory.createRow(sheet, column, rowStyles);
			}
			doPopulateCycleCaseStep(row, cycleCaseStep.get(i), defects);
			column++;
		}

		return cycleCaseStep.size() - 1;
	}

	public void doPopulateCycleCaseStep(Row row, TestCycleCaseStepDTO cycleCaseStep, List<TestCycleCaseDefectRelDTO> defects) {

		Optional.ofNullable(cycleCaseStep.getTestStep()).ifPresent(v -> WorkBookFactory.createCell(row, 10, WorkBookFactory.CellType.TEXT, v));
		Optional.ofNullable(cycleCaseStep.getTestData()).ifPresent(v -> WorkBookFactory.createCell(row, 11, WorkBookFactory.CellType.TEXT, v));
		Optional.ofNullable(cycleCaseStep.getExpectedResult()).ifPresent(v -> WorkBookFactory.createCell(row, 12, WorkBookFactory.CellType.TEXT, v));
		Optional.ofNullable(cycleCaseStep.getStatusName()).ifPresent(v -> WorkBookFactory.createCell(row, 13, WorkBookFactory.CellType.TEXT, v));
		Optional.ofNullable(cycleCaseStep.getComment()).ifPresent(v -> WorkBookFactory.createCell(row, 14, WorkBookFactory.CellType.TEXT, v));
		Optional.ofNullable(getDefectsCell(defects, TestCycleCaseDefectRelE.CASE_STEP)).ifPresent(v -> WorkBookFactory.createCell(row, 15, WorkBookFactory.CellType.TEXT, v));

	}

	private String getDefectsCell(List<TestCycleCaseDefectRelDTO> list, String type) {
		if (ObjectUtils.isEmpty(list)) {
			return null;
		}

		if ("CASE_STEP".equals(type)) {
			list = list.stream().filter(v -> v.getDefectType().equals(TestCycleCaseDefectRelE.CASE_STEP)).collect(Collectors.toList());

		} else if ("CYCLE_CASE".equals(type)) {
			list = list.stream().filter(v -> v.getDefectType().equals(TestCycleCaseDefectRelE.CYCLE_CASE)).collect(Collectors.toList());

		}
		if (list.isEmpty()) {
			return null;
		}
		String name = "";
		for (int i = 0; i < list.size(); i++) {
			if (i != 0) {
				name += ",\n";
			}
			name += list.get(i).getIssueInfosDTO().getIssueName() + ":" + list.get(i).getIssueInfosDTO().getSummary();

		}
		return name;
	}

	private String getLabelCell(List<LabelIssueRelDTO> list) {
		if (ObjectUtils.isEmpty(list)) {
			return null;
		}
		String name = "";
		for (int i = 0; i < list.size(); i++) {
			if (i != 0) {
				name += ",\n";
			}
			name += list.get(i).getLabelName();

		}
		return name;
	}

	private String getModuleCell(List<ComponentIssueRelDTO> list) {
		if (ObjectUtils.isEmpty(list)) {
			return null;
		}
		String name = "";
		for (int i = 0; i < list.size(); i++) {
			if (i != 0) {
				name += ",\n";
			}
			name += list.get(i).getName();

		}
		return name;
	}


	public int populateVersionHeader(Sheet sheet, String projectName, String versionName, CellStyle rowStyle) {

		Row row1 = WorkBookFactory.createRow(sheet, 0, rowStyle);
		WorkBookFactory.createCell(row1, 0, WorkBookFactory.CellType.TEXT, "项目：" + projectName);
		WorkBookFactory.createCell(row1, 1, WorkBookFactory.CellType.TEXT, "版本：" + versionName);
		return 2;
	}

	public int populateCycleCaseHeader(Sheet sheet, int rowNum, CellStyle rowStyle) {
		Row row = WorkBookFactory.createRow(sheet, rowNum, rowStyle);
		int i = 0;
		for (CycleCaseHeader value : CycleCaseHeader.values()) {
			WorkBookFactory.createCell(row, i++, WorkBookFactory.CellType.TEXT, value.getValue("zn_ch"));
		}
		return rowNum + 1;
	}

	public int populateCycleHeader(Sheet sheet, int rowNum, TestCycleDTO cycle, CellStyle rowStyle) {
		Assert.notNull(cycle, "error.cycle.are.not.exist");
		Assert.notNull(sheet, "error.sheet.are.be.null");
		Row row = WorkBookFactory.createRow(sheet, rowNum, rowStyle);
		WorkBookFactory.createCell(row, 0, WorkBookFactory.CellType.TEXT, "循环: " + Optional.ofNullable(cycle.getCycleName()).orElse(""));
		String date = "";
		if (!ObjectUtils.isEmpty(cycle.getFromDate())) {
			date = dateFormat.format(cycle.getFromDate());
		}
		WorkBookFactory.createCell(row, 1, WorkBookFactory.CellType.TEXT, "开始时间: " + date);
		if (!ObjectUtils.isEmpty(cycle.getToDate())) {
			date = dateFormat.format(cycle.getToDate());
		}
		WorkBookFactory.createCell(row, 2, WorkBookFactory.CellType.TEXT, "结束时间: " + date);
		WorkBookFactory.createCell(row, 3, WorkBookFactory.CellType.TEXT, "构建号: " + Optional.ofNullable(cycle.getBuild()).orElse(""));
		WorkBookFactory.createCell(row, 4, WorkBookFactory.CellType.TEXT, "环境: " + Optional.ofNullable(cycle.getEnvironment()).orElse(""));
		WorkBookFactory.createCell(row, 5, WorkBookFactory.CellType.TEXT, "创建人:" + Optional.ofNullable(cycle.getCreatedUser()).map(UserDO::getRealName).orElse(""));
		WorkBookFactory.createCell(row, 6, WorkBookFactory.CellType.TEXT, "说明: " + Optional.ofNullable(cycle.getDescription()).orElse(""));
		return rowNum + 2;
	}


	public void populateSheetStyle(Sheet sheet) {
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeight((short) 500);
	}

	public Workbook getWorkBook(WorkBookFactory.Mode mode) {
		Workbook workbook = WorkBookFactory.getWorkBook(mode);
		if (log.isDebugEnabled()) {
			log.debug("导出测试详情：创建workbook成功，类型" + mode.getValue());
		}
		return workbook;
	}


	public static class WorkBookFactory {
		public enum Mode {
			SXSSF("SXSSF"), HSSF("HSSF");
			private String value;

			Mode(String value) {
				this.value = value;
			}

			public String getValue() {
				return this.value;
			}
		}

		public enum CellType {
			NUMBER(0), TEXT(1), FORMULA(2), BLANK(3), BOOLEAN(4), ERROR(5), DATE(0);
			private int type;

			CellType(int type) {
				this.type = type;
			}

			public int getType() {
				return type;
			}

		}

		public static Row createRow(Sheet sheet, int column, CellStyle rowStyle) {
			Row row = sheet.createRow(column);
			Optional.ofNullable(rowStyle).ifPresent(row::setRowStyle);
			return row;
		}

		public static Cell createCell(Row row, int column, CellType type, Object value) {
			Cell cell = row.createCell(column, type.getType());
			cell.setCellStyle(row.getRowStyle());
			switch (type) {
				case TEXT:
					cell.setCellValue((String) value);
					break;
				case NUMBER:
					cell.setCellValue((Long) value);
					break;
				case DATE:
					cell.setCellValue((String) value);
					break;
				default:
					cell.setCellValue(value.toString());
			}
			return cell;
		}

		public static Workbook getWorkBook(Mode mode) {
			Workbook workbook;
			switch (mode) {
				case HSSF:
					workbook = new HSSFWorkbook();
					break;
				case SXSSF:
					workbook = new SXSSFWorkbook();
					break;
				default:
					workbook = new HSSFWorkbook();
			}
			return workbook;
		}

	}
}
