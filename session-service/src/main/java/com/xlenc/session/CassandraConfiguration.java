package com.xlenc.session;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * User: Michael Williams
 * Date: 1/13/14
 * Time: 10:07 PM
 */
public @Data
class CassandraConfiguration {

    @NotEmpty
    @JsonProperty
    private String host;

    @NotEmpty
    @JsonProperty
    private String keySpaceName;

    @NotEmpty
    @JsonProperty
    private String tableName;

}