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
    @Property("party_id")
    private String partyId;
    @Property("application_id")
    private String applicationId;
    @Property("created")
    private Long created;
    @Property("last_request")
    private Long lastRequest;
    @Property("ended")
    private Long ended;
    @Property("data")
    private Map<String, Object> data;
    @Version
    private Long version;

    public SessionData(String id, Map<String, Object> data) {
    }

    public SessionData(String id) {
        this.id = id;
    }

}
