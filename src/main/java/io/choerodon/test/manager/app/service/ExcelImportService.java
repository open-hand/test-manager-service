package io.choerodon.test.manager.app.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.hzero.starter.keyencrypt.core.EncryptType;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface ExcelImportService {

    boolean cancelFileUpload(Long historyId);

    void downloadImportTemp(HttpServletRequest request, HttpServletResponse response, Long organizationId, Long projectId);

    void importIssueByExcel(Long projectId, Long userId, InputStream inputStream,
                            EncryptType encryptType, RequestAttributes requestAttributes);

    Workbook buildImportTemp(Long organizationId, Long projectId);

    void validateFileSize(MultipartFile excelFile);
}
