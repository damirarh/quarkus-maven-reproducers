package com.github.renegrob;

import io.quarkus.vertx.http.runtime.filters.Filters;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

@Singleton
public class CorrelationIdFilter {

  private static final List<String> CORRELATION_ID_HEADER_NAMES = List.of("X-Correlation-ID", "X-Request-ID", "trace-id", "X-Trace-Id");

  public static final String CORRELATION_ID_MDC_KEY = "correlationId";

  private final Logger logger;

  @Inject
  public CorrelationIdFilter(Logger logger) {
    this.logger = logger;
  }

  public void init(@Observes Filters filters) {
    filters.register(this::filter, 1);
  }

  public void filter(RoutingContext routingContext) {
    associateCorrelationIdWithRequest(routingContext);
    logRequest(routingContext);
    routingContext.addBodyEndHandler(_void -> logResponse(routingContext));
    routingContext.next();
  }

  private void associateCorrelationIdWithRequest(RoutingContext routingContext) {
    String correlationId = null;
    for (String headerName : CORRELATION_ID_HEADER_NAMES) {
      correlationId = routingContext.request().getHeader(headerName);
      if (correlationId != null) {
        logger.debugf("Extracted %s: %s from header %s.", CORRELATION_ID_MDC_KEY, correlationId, headerName);
        break;
      }
    }
    if (correlationId == null) {
      correlationId = UUID.randomUUID().toString();
      logger.debugf("Created new %s: %s.", CORRELATION_ID_MDC_KEY, correlationId);
    }
    MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
    routingContext.put(CORRELATION_ID_MDC_KEY, correlationId);
  }

  private void logRequest(RoutingContext routingContext) {
    String correlationId = routingContext.get(CORRELATION_ID_MDC_KEY);
    String method = routingContext.request().method().toString();
    String uri = routingContext.request().uri();
    logger.infof("%s: %s %s", correlationId, method, uri);
  }

  private void logResponse(RoutingContext routingContext) {
    String correlationId = routingContext.get(CORRELATION_ID_MDC_KEY);
    HttpServerResponse response = routingContext.response();
    logger.infof("%s: %d %s", correlationId, response.getStatusCode(), response.getStatusMessage());
    routingContext.remove(CORRELATION_ID_MDC_KEY);
    MDC.remove(CORRELATION_ID_MDC_KEY);
  }
}
