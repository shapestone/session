package com.xlenc.session;

import com.xlenc.api.session.SessionData;
import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.HashMap;

/**
 * User: Michael Williams
 * Date: 1/16/14
 * Time: 8:00 AM
 */
public class SessionResource {

    private SessionService sessionService;

    public SessionResource(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @POST
    @Timed
    //@ApiOperation(value = "Creates a question", response = QuestionData.class)
    public Response addSession(SessionData sessionData) {
        sessionService.addSession(sessionData);
        final SessionData newSessionData = sessionService.addSession(sessionData);
        return Response.ok(newSessionData).build();
    }

    @GET
    @Timed
    //@ApiOperation("Retrieves a list of questions")
    public Response readSession(@PathParam("id") String id) {
        final SessionData questionData = sessionService.readSession(id);
        return Response.ok(questionData).build();
    }

    @DELETE
    @Timed
    @Path("/{id}")
    //@ApiOperation("Delete a question")
    public Response deleteSession(@PathParam("id") String id) {
        final int affected = sessionService.deleteSession(id);
        return Response.ok(
                new HashMap<String, Object>() {{
                    put("success", true);
                    put("affected", affected);
                }}
        ).build();
    }

}
