package com.thoughtworks.db.migration.verify;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Setter
@AllArgsConstructor
public class TableBuilder {

    public static final String CREATE_TABLE_TEMPLATE = "CREATE TABLE `%s`\n" +
            "(\n" +
            "    `id`           int(11)                                 NOT NULL AUTO_INCREMENT COMMENT '卡券id',\n" +
            "    `name`         varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '卡券名称',\n" +
            "    `reduce_price` decimal(11, 2)                                   DEFAULT NULL COMMENT '优惠金额',\n" +
            "    `deleted`      tinyint(1)                              NOT NULL DEFAULT '0' COMMENT '是否软删',\n" +
            "    PRIMARY KEY (`id`)\n" +
            ") ENGINE = %s\n" +
            "  DEFAULT CHARSET = utf8mb4\n" +
            "  COLLATE = %s COMMENT = '%s';";
    public static final String CREATE_TABLE_TEMPLATE_WITHOUT_PRIMARY_KEY = "CREATE TABLE `%s`\n" +
            "(\n" +
            "    `name`         varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '卡券名称',\n" +
            "    `reduce_price` decimal(11, 2)                                   DEFAULT NULL COMMENT '优惠金额',\n" +
            "    `deleted`      tinyint(1)                              NOT NULL DEFAULT '0' COMMENT '是否软删'\n" +
            ") ENGINE = %s\n" +
            "  DEFAULT CHARSET = utf8mb4\n" +
            "  COLLATE = %s COMMENT = '%s';";
    public static final String CREATE_TABLE_TEMPLATE_WITH_NON_AUTO_INCR_PRIMARY_KEY = "CREATE TABLE `%s`\n" +
            "(\n" +
            "  `mobile` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,\n" +
            "  `user_id` int(11) NOT NULL,\n" +
            "  PRIMARY KEY (`mobile`)\n" +
            ") ENGINE = %s\n" +
            "  DEFAULT CHARSET = utf8mb4\n" +
            "  COLLATE = %s COMMENT = '%s';";
    private final String tableName;

    private String engine;
    private String collation;
    private String comment;

    public static TableBuilder withDefault(String tableName) {
        return new TableBuilder(tableName, "InnoDB", "utf8mb4_general_ci", "卡券");
    }


    public Table build() {
        String createTableSql = String.format(
                CREATE_TABLE_TEMPLATE,
                tableName, engine, collation, comment);
        return new Table(createTableSql, tableName);
    }

    public Table buildWithoutPrimaryKey() {
        String createTableSql = String.format(
                CREATE_TABLE_TEMPLATE_WITHOUT_PRIMARY_KEY,
                tableName, engine, collation, comment);
        return new Table(createTableSql, tableName);
    }

    public Table buildWithNonAutoIncrPrimaryKey() {
        String createTableSql = String.format(
                CREATE_TABLE_TEMPLATE_WITH_NON_AUTO_INCR_PRIMARY_KEY,
                tableName, engine, collation, comment);
        return new Table(createTableSql, tableName);
    }

    @Data
    public static class Table {
        private final String tableName;
        private String ddl;

        public Table(String ddl, String tableName) {
            this.ddl = ddl;
            this.tableName = tableName;
        }

        public String withRow(String key, String value) {
            return "INSERT INTO " + tableName + " (" + key + ") VALUES (" + format(value) + ");";
        }

        public String addRow(String mobile, String name) {
            return "INSERT INTO " + tableName + " (mobile, name) VALUES (" + mobile +" ," + name + ");";

        }


    }

    private static String format(String value) {
        return "'" + value + "'";
    }
}
