package com.xlenc.session;

import com.xlenc.api.session.Result;
import com.xlenc.api.session.ResultError;
import com.xlenc.api.session.SessionData;
import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
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
    @Consumes("application/json")
    @Produces("application/json")
    @Timed
    //@ApiOperation(value = "Creates a question", response = QuestionData.class)
    public Response addSession(SessionData sessionData) {
        final Result<SessionData, ResultError> newSessionData = sessionService.createSession(sessionData);
        return Response.ok(newSessionData).build();
    }

    public Response validateSession(SessionData sessionData) {
        final Result<SessionData, ResultError> validSession = sessionService.validateSession(sessionData);
        return Response.ok(validSession).build();
    }

    @GET
    @Consumes("application/json")
    @Produces("application/json")
    @Timed
    //@ApiOperation("Retrieves a list of questions")
    public Response readSession(@PathParam("id") String id) {
        final Result<SessionData, ResultError> questionData = sessionService.readSession(id);
        return Response.ok(questionData).build();
    }

    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    @Timed
    @Path("/{id}")
    //@ApiOperation("Delete a question")
    public Response updateSession(@PathParam("id") String id, Map<String, Object> data) {
        final Result<SessionData, ResultError> result = sessionService.updateSession(new SessionData(id, data));
        return Response.ok(result).build();
    }

    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    @Timed
    @Path("/{id}/expire")
    //@ApiOperation("Delete a question")
    public Response expireSession(@PathParam("id") String id) {
        final Result<SessionData, ResultError> result = sessionService.expireSession(new SessionData(id));
        return Response.ok(result).build();
    }

}
