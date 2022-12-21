package com.thoughtworks.db.migration.verify.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseVariable {
    @JsonAlias("Variable_name")
    private String name;
    @JsonAlias("Value")
    private String value;
}
