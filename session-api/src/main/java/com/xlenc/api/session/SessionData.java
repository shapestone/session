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
    @Property("last_active")
    private Long lastActive;
    @Property("expired")
    private Long expired;
    @Property("token")
    private String token;
    @Property("data")
    private Map<String, Object> data;

    public SessionData() {
    }

    public SessionData(String id, Map<String, Object> data) {
        this.id = id;
        this.data = data;
    }

    public SessionData(String id) {
        this.id = id;
    }

}
