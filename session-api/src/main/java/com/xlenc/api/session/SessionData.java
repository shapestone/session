package com.xlenc.api.session;

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

    private String id;
    private String partyId;
    private String applicationId;
    private Long createdOn;
    private Long lastActiveOn;
    private Long expiredOn;
    private String token;
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
