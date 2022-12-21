package com.ftms.db.migration.verify.utils;

import com.opencsv.CSVReaderBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.FileReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.thoughtworks.db.migration.verify.utils.CsvReportFactory.generateReportFilePath;

public class CsvReportHelper {
    @SneakyThrows
    public static ReportContent readReportContent(String executionId) {
        try (var reader = new CSVReaderBuilder(new FileReader(generateReportFilePath(executionId))).build()) {
            var lines = reader.readAll()
                              .stream()
                              .map(strings -> String.join(",", strings))
                              .map(ReportRow::new)
                              .collect(Collectors.toList());
            return new ReportContent(lines);
        }
    }

    @Getter
    public static class ReportContent {
        private final int rowCount;
        private final List<ReportRow> rows;

        public ReportContent(List<ReportRow> rows) {
            this.rows = rows;
            this.rowCount = rows.size();
        }

        public ReportRow read(int rowIndex) {
            return this.rows.get(rowIndex - 1);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ReportRow {
        private String content;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ReportRow reportRow = (ReportRow) o;
            return content.equals(reportRow.content);
        }

        @Override
        public int hashCode() {
            return Objects.hash(content);
        }
    }
}
