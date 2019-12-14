package io.choerodon.test.manager.app.service.impl;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import io.choerodon.test.manager.api.vo.agile.ComponentIssueRelVO;
import io.choerodon.test.manager.api.vo.agile.LabelIssueRelVO;
import io.choerodon.test.manager.api.vo.agile.UserDO;
import io.choerodon.test.manager.api.vo.TestCycleCaseVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseDefectRelVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseStepVO;
import io.choerodon.test.manager.api.vo.TestCycleVO;
import io.choerodon.test.manager.infra.util.ExcelUtil;

/**
 * Created by 842767365@qq.com on 8/9/18.
 */

@Service
public class CycleCaseExcelExportServiceImpl extends AbstarctExcelExportServiceImpl<TestCycleVO, TestCycleCaseVO> {

    public static final String CASE_STEP = "CASE_STEP";
    public static final String CYCLE_CASE = "CYCLE_CASE";

    private enum CycleCaseHeader {
        COLUMN1("测试阶段"), COLUMN2("用例编号"), COLUMN3("用例概要"), COLUMN4("执行状态"), COLUMN5("说明"), COLUMN6("缺陷编号概要"), COLUMN7("模块"), COLUMN8("标签"), COLUMN9("执行方"),
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

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public int populateBody(Sheet sheet, int column, List<TestCycleCaseVO> cycleCases, Queue<CellStyle> rowStyles) {

        for (TestCycleCaseVO cycleCase : cycleCases) {
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


    public int populateCycleCase(Sheet sheet, int columnNum, TestCycleCaseVO cycleCase, CellStyle rowStyles) {
        Row row = ExcelUtil.createRow(sheet, columnNum, rowStyles);
        Optional.ofNullable(cycleCase.getFolderName()).ifPresent(v -> ExcelUtil.createCell(row, 0, ExcelUtil.CellType.TEXT, v));
        if (!ObjectUtils.isEmpty(cycleCase.getIssueInfosVO())) {
            Optional.ofNullable(cycleCase.getIssueInfosVO().getIssueName()).ifPresent(v -> ExcelUtil.createCell(row, 1, ExcelUtil.CellType.TEXT, v));
            Optional.ofNullable(cycleCase.getIssueInfosVO().getSummary()).ifPresent(v -> ExcelUtil.createCell(row, 2, ExcelUtil.CellType.TEXT, v));
            Optional.ofNullable(getLabelCell(cycleCase.getIssueInfosVO().getLabelIssueRelVOList())).ifPresent(v -> ExcelUtil.createCell(row, 6, ExcelUtil.CellType.TEXT, v));
            Optional.ofNullable(getModuleCell(cycleCase.getIssueInfosVO().getComponentIssueRelVOList())).ifPresent(v -> ExcelUtil.createCell(row, 7, ExcelUtil.CellType.TEXT, v));
        }
        Optional.ofNullable(cycleCase.getExecutionStatusName()).ifPresent(v -> ExcelUtil.createCell(row, 3, ExcelUtil.CellType.TEXT, v));
        Optional.ofNullable(cycleCase.getComment()).ifPresent(v -> ExcelUtil.createCell(row, 4, ExcelUtil.CellType.TEXT, ExcelUtil.getColumnWithoutRichText(v)));
        Optional.ofNullable(getDefectsCell(cycleCase.getDefects(), CYCLE_CASE)).ifPresent(v -> ExcelUtil.createCell(row, 5, ExcelUtil.CellType.TEXT, v));
        Optional.ofNullable(cycleCase.getAssigneeUser()).ifPresent(v -> ExcelUtil.createCell(row, 8, ExcelUtil.CellType.TEXT, v.getRealName()));
        Optional.ofNullable(cycleCase.getLastUpdateDate()).ifPresent(v -> ExcelUtil.createCell(row, 9, ExcelUtil.CellType.DATE, dateFormat.format(v)));

        return columnNum + populateCycleCaseStep(sheet, columnNum, cycleCase.getCycleCaseStep(), cycleCase.getDefects(), rowStyles) + 1;
    }

    public int populateCycleCaseStep(Sheet sheet, int column, List<TestCycleCaseStepVO> cycleCaseStep, List<TestCycleCaseDefectRelVO> defects, CellStyle rowStyles) {
        if (cycleCaseStep == null || cycleCaseStep.isEmpty()) {
            return 0;
        }
        for (int i = 0; i < cycleCaseStep.size(); i++) {
            Row row;
            if (i == 0) {
                row = sheet.getRow(column);
            } else {
                row = ExcelUtil.createRow(sheet, column, rowStyles);
            }
            doPopulateCycleCaseStep(row, cycleCaseStep.get(i), defects);
            column++;
        }

        return cycleCaseStep.size() - 1;
    }

    public void doPopulateCycleCaseStep(Row row, TestCycleCaseStepVO cycleCaseStep, List<TestCycleCaseDefectRelVO> defects) {

        Optional.ofNullable(cycleCaseStep.getTestStep()).ifPresent(v -> ExcelUtil.createCell(row, 10, ExcelUtil.CellType.TEXT, v));
        Optional.ofNullable(cycleCaseStep.getTestData()).ifPresent(v -> ExcelUtil.createCell(row, 11, ExcelUtil.CellType.TEXT, v));
        Optional.ofNullable(cycleCaseStep.getExpectedResult()).ifPresent(v -> ExcelUtil.createCell(row, 12, ExcelUtil.CellType.TEXT, v));
        Optional.ofNullable(cycleCaseStep.getStatusName()).ifPresent(v -> ExcelUtil.createCell(row, 13, ExcelUtil.CellType.TEXT, v));
        Optional.ofNullable(cycleCaseStep.getDescription()).ifPresent(v -> ExcelUtil.createCell(row, 14, ExcelUtil.CellType.TEXT, v));
        Optional.ofNullable(getDefectsCell(defects, CASE_STEP)).ifPresent(v -> ExcelUtil.createCell(row, 15, ExcelUtil.CellType.TEXT, v));

    }

    private String getDefectsCell(List<TestCycleCaseDefectRelVO> list, String type) {
        if (ObjectUtils.isEmpty(list)) {
            return null;
        }

        if ("CASE_STEP".equals(type)) {
            list = list.stream().filter(v -> v.getDefectType().equals(CASE_STEP)).collect(Collectors.toList());

        } else if ("CYCLE_CASE".equals(type)) {
            list = list.stream().filter(v -> v.getDefectType().equals(CYCLE_CASE)).collect(Collectors.toList());

        }
        if (list.isEmpty()) {
            return null;
        }
        String name = "";
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) {
                name += ",\n";
            }
            name += list.get(i).getIssueInfosVO().getIssueName() + ":" + list.get(i).getIssueInfosVO().getSummary();

        }
        return name;
    }

    private String getLabelCell(List<LabelIssueRelVO> list) {
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

    private String getModuleCell(List<ComponentIssueRelVO> list) {
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


    public int populateVersionHeader(Sheet sheet, String projectName, TestCycleVO cycle, CellStyle rowStyle) {

        Row row1 = ExcelUtil.createRow(sheet, 0, rowStyle);
        ExcelUtil.createCell(row1, 0, ExcelUtil.CellType.TEXT, "项目：" + projectName);
        ExcelUtil.createCell(row1, 1, ExcelUtil.CellType.TEXT, "版本：" + cycle.getVersionName());
        return 2;
    }

    public int populateCycleCaseHeader(Sheet sheet, int rowNum, CellStyle rowStyle) {
        Row row = ExcelUtil.createRow(sheet, rowNum, rowStyle);
        int i = 0;
        for (CycleCaseHeader value : CycleCaseHeader.values()) {
            ExcelUtil.createCell(row, i++, ExcelUtil.CellType.TEXT, value.getValue("zn_ch"));
        }
        return rowNum + 1;
    }

    public int populateHeader(Sheet sheet, int rowNum, TestCycleVO cycle, CellStyle rowStyle) {
        Assert.notNull(cycle, "error.cycle.are.not.exist");
        Assert.notNull(sheet, "error.sheet.are.be.null");
        Row row = ExcelUtil.createRow(sheet, rowNum, rowStyle);
        ExcelUtil.createCell(row, 0, ExcelUtil.CellType.TEXT, "循环: " + Optional.ofNullable(cycle.getCycleName()).orElse(""));
        String date = "";
        if (!ObjectUtils.isEmpty(cycle.getFromDate())) {
            date = dateFormat.format(cycle.getFromDate());
        }
        ExcelUtil.createCell(row, 1, ExcelUtil.CellType.TEXT, "开始时间: " + date);
        if (!ObjectUtils.isEmpty(cycle.getToDate())) {
            date = dateFormat.format(cycle.getToDate());
        }
        ExcelUtil.createCell(row, 2, ExcelUtil.CellType.TEXT, "结束时间: " + date);
        ExcelUtil.createCell(row, 3, ExcelUtil.CellType.TEXT, "构建号: " + Optional.ofNullable(cycle.getBuild()).orElse(""));
        ExcelUtil.createCell(row, 4, ExcelUtil.CellType.TEXT, "环境: " + Optional.ofNullable(cycle.getEnvironment()).orElse(""));
        ExcelUtil.createCell(row, 5, ExcelUtil.CellType.TEXT, "创建人:" + Optional.ofNullable(cycle.getCreatedUser()).map(UserDO::getRealName).orElse(""));
        ExcelUtil.createCell(row, 6, ExcelUtil.CellType.TEXT, "说明: " + Optional.ofNullable(cycle.getDescription()).orElse(""));
        return populateCycleCaseHeader(sheet, rowNum + 2, rowStyle);
    }


}
