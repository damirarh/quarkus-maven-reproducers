package com.github.renegrob;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.logging.Logger;

import io.smallrye.common.annotation.Blocking;

@Singleton
public class ExampleService {

    private int counter = 1;

    private final Logger logger;

    @Inject
    public ExampleService(Logger logger) {
        this.logger = logger;
    }

    public synchronized String getBlocking() {
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        final String result = Integer.toString(counter++);
        logger.infof("Creating result: %s", result);
        return result;
    }
}
