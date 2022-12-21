package com.thoughtworks.db.migration.verify.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Getter
@AllArgsConstructor
public class DatasourceRequest {
    private String ipAddress;
    private int port;
    private String database;
    private String username;
    private String password;

    public String getJdbcUrl() {
        return format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true", ipAddress, port, database);
    }

    public boolean isAllValidProperties() {
        return StringUtils.isNoneBlank(ipAddress) && isNotBlank(database) && isNotBlank(username);
    }
}
