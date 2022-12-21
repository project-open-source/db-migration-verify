package com.thoughtworks.db.migration.verify.utils;

import com.thoughtworks.db.migration.verify.exception.ReportFileCreateFailedException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.Optional;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CsvReportFactory {
    public static final String RESOURCE_PATH = Optional.of(CsvReportFactory.class)
                                                       .map(o -> o.getResource("."))
                                                       .map(URL::getFile)
                                                       .orElse(".");
    public static final String REPORT_FILE_DIRECTORY = RESOURCE_PATH + "/reports/";

    public static File createReportFile(String fileName) {
        var saveFilePath = generateReportFilePath(fileName);
        log.info("report file storage path: {}", saveFilePath);

        var reportDirectory = new File(REPORT_FILE_DIRECTORY);
        if (reportDirectory.exists() || reportDirectory.mkdir()) {
            var reportFile = new File(saveFilePath);
            log.info("successfully create file report with file name: {}", fileName);
            return reportFile;
        }

        throw new ReportFileCreateFailedException("创建报表文件失败", String.format("cannot create file in specific path: %s", saveFilePath));
    }

    public static String generateReportFilePath(String fileName) {
        return REPORT_FILE_DIRECTORY + fileName + ".csv";
    }
}
