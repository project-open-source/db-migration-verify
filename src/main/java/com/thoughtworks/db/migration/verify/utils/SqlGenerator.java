package com.thoughtworks.db.migration.verify.utils;

public class SqlGenerator {
    public static final String TEST_QUERY = "SELECT * FROM account";
    public static final String TABLE_QUERY = "SHOW TABLES";
    public static final String AMOUNT_COUNT = "SELECT COUNT(*) FROM ";


    public static String getDescCommandSql(String table) {
        return "DESC " + table;
    }

    public static String getInfomationSchemaSql(String tableName, String schema) {
        return String.format("SELECT * FROM information_schema.TABLES where TABLE_NAME = '%s' and TABLE_SCHEMA = '%s';", tableName, schema);
    }

    public static String getQueryMaxIdSql(String tableName, String id) {
        return String.format("SELECT max(%s) from %s;", id, tableName);
    }

    public static String getQueryMinIdSql(String tableName, String id) {
        return String.format("SELECT min(%s) from %s;", id, tableName);
    }

    public static String getRowWithPrimaryKey(String tableName, String primaryKeyColumnName,String id) {
        return String.format("SELECT * from %s where %s = '%s'", tableName, primaryKeyColumnName, id);
    }
}
