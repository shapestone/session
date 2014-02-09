package com.xlenc.session;

import com.xlenc.api.session.SessionData;
import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Michael Williams
 * Date: 1/16/14
 * Time: 8:00 AM
 */
@Path("/sessions")
public class SessionResource {

    private SessionService sessionService;

    public SessionResource(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @POST
    @Timed
    //@ApiOperation(value = "Creates a question", response = QuestionData.class)
    public Response addSession(SessionData sessionData) {
        final Result<SessionData, SessionError> newSessionData = sessionService.addSession(sessionData);
        return Response.ok(newSessionData).build();
    }

    @GET
    @Timed
    //@ApiOperation("Retrieves a list of questions")
    public Response readSession(@PathParam("id") String id) {
        final Result<SessionData, SessionError> questionData = sessionService.readSession(id);
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

    @PUT
    @Timed
    @Path("/{id}")
    //@ApiOperation("Delete a question")
    public Response updateSession(@PathParam("id") String id, Map<String, Object> data) {
        final Result<SessionData, SessionError> result = sessionService.updateSession(new SessionData(id, data));
        return Response.ok(result).build();
    }
}
