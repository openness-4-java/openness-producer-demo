package it.unimore.dipi.openness.producer.resources;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.jersey.errors.ErrorMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import it.unimore.dipi.openness.producer.model.EventDescriptor;
import it.unimore.dipi.openness.producer.services.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path("/api/tis/event")
@Api("Demo Traffic Information System Endpoint")
public class EventResource {

    final protected Logger logger = LoggerFactory.getLogger(EventResource.class);

    @SuppressWarnings("serial")
    public static class MissingKeyException extends Exception{}
    final AppConfig conf;

    public EventResource(AppConfig conf) {
        this.conf = conf;
    }

    @GET
    @Path("/")
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value="Get all registered Traffic Events")
    public Response getEvents(@Context ContainerRequestContext req) {

        try {

            logger.info("Loading all stored IoT Inventory Users ...");

            List<EventDescriptor> userList = this.conf.getTisDataManager().getEventList();

            if(userList == null)
                return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON_TYPE).entity(new ErrorMessage(Response.Status.NOT_FOUND.getStatusCode(),"Events Not Found !")).build();

            return Response.ok(userList).build();

        } catch (Exception e){
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).entity(new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),"Internal Server Error !")).build();
        }
    }

    @GET
    @Path("/{event_id}")
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value="Get User by Id")
    public Response getEventById(@Context ContainerRequestContext req,
                                @PathParam("event_id") String eventId) {

        try {

            logger.info("Loading Event Info for id: {}", eventId);

            //Check the request
            if(eventId == null)
                return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(new ErrorMessage(Response.Status.BAD_REQUEST.getStatusCode(),"Invalid Event Id Provided !")).build();

            Optional<EventDescriptor> eventDescriptorOpt = this.conf.getTisDataManager().getEventById(eventId);

            if(!eventDescriptorOpt.isPresent())
                return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON_TYPE).entity(new ErrorMessage(Response.Status.NOT_FOUND.getStatusCode(),"Event Not Found !")).build();

            return Response.ok(eventDescriptorOpt.get()).build();

        } catch (Exception e){
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).entity(new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),"Internal Server Error !")).build();
        }
    }

}

