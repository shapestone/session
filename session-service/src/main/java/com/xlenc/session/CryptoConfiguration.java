package com.xlenc.session;

import lombok.Data;
import org.codehaus.jackson.annotate.*;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * User: Michael Williams
 * Date: 1/13/14
 * Time: 10:07 PM
 */
public @Data
class CryptoConfiguration {

    @NotEmpty
    @JsonProperty
    private String publicKeyFileName;

    @NotEmpty
    @JsonProperty
    private String privateKeyFileName;

}
