package io.choerodon.test.manager.infra.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Map;

/**
 * @author shinan.chen
 * @since 2019/8/16
 */
public class ExcelTitleUtil {
    private Map<String, Integer> headerLocationMap;

    public ExcelTitleUtil(Map<String, Integer> headerLocationMap) {
        this.headerLocationMap = headerLocationMap;
    }

    public Cell getCell(String titleName, Row row) {
        Integer location = headerLocationMap.get(titleName);
        if (location != null) {
            row.getCell(location);
        }
        return null;
    }
}
