package com.xlenc.api.session;

import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Property;
import com.google.code.morphia.annotations.Version;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

/**
 * User: Michael Williams
 * Date: 1/16/14
 * Time: 8:01 AM
 */

@ToString
public @Data class SessionData {

    @Id
    private String id;
    @Property("created")
    private long created;
    @Property("last_request")
    private long lastRequest;
    @Property("ended")
    private long ended;
    @Property("data")
    private Map<String, Object> data;
    @Version
    private long version;

    public SessionData(String id) {
        this.id = id;
    }

}
