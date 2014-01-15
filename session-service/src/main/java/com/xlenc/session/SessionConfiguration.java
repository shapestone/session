package com.xlenc.session;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * User: Michael Williams
 * Date: 1/13/14
 * Time: 10:06 PM
 */
public @Data
class SessionConfiguration  extends Configuration {

    @Valid
    @NotNull
    @JsonProperty("mongoDatabase")
    private MongoDatabaseConfiguration mongoDatabaseConfiguration = new MongoDatabaseConfiguration();

}
