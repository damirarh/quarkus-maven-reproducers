package com.github.renegrob;

import java.time.Duration;
import java.time.LocalTime;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.jboss.logging.Logger;
import org.slf4j.MDC;

import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;

import static com.github.renegrob.CorrelationIdFilter.CORRELATION_ID_MDC_KEY;

@Singleton
public class MyScheduler {

    @Inject
    ManagedExecutor managedExecutor;

    @Inject
    Logger logger;

    @Scheduled(every = "5s")
    void scheduledTask() {
        MDC.put(CORRELATION_ID_MDC_KEY, UUID.randomUUID().toString());
        try {
            logger.info("scheduledTask invoked");
            final String result = Uni.createFrom().item(LocalTime.now().toString()).onItem().delayIt().by(Duration.ofMillis(50))
                    .runSubscriptionOn(managedExecutor)
                    .onItem().invoke(text -> logger.infof("Getting: %s", text)).await().indefinitely();
            logger.info("result: " + result);
        } finally {
            MDC.remove(CORRELATION_ID_MDC_KEY);
        }
    }
}
