package com.xlenc.session;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

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
