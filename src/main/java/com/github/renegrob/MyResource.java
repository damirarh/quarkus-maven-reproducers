package com.github.renegrob;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.mutiny.core.Vertx;

@Path("/v1")
public class MyResource {

    @Inject
    Logger logger;

    @Inject
    ExampleService service;

    @GET
    @Path("hello")
    @Produces(MediaType.TEXT_PLAIN)
    @Blocking
    public String hello() {
        logger.info("hello endpoint");
        return "Hello from RESTEasy Reactive";
    }

    @GET
    @Path("annotation/blocking")
    @Produces(MediaType.TEXT_PLAIN)
    @Blocking
    public String atBlockingCall() {
        logger.info("hello endpoint");
        return service.getBlocking();
    }

    @GET
    @Path("worker-pool/blocking")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> blockingCall() {
        return Uni.createFrom().item(service::getBlocking)
                .runSubscriptionOn(Infrastructure.getDefaultExecutor());
    }

    @GET
    @Path("vertx/blocking")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> vertxBlocking() {
        return Vertx.currentContext().executeBlocking(Uni.createFrom().item(service::getBlocking));
    }
}
