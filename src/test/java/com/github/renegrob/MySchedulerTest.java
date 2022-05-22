package com.github.renegrob;

import java.util.Map;
import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;

import static com.github.renegrob.LogCorrelationIdAssertion.assertCorrelationIdLogged;

@QuarkusTest
@TestProfile(MySchedulerTest.SchedulerProfile.class)
public class MySchedulerTest {

    public static class SchedulerProfile implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("quarkus.scheduler.enabled", "true");
        }
    }

    private LogTestReader logReader;

    @BeforeEach
    void setUp() {
        logReader = new LogTestReader();
    }

    @Inject
    MyScheduler myScheduler;

    @Test
    public void testMyScheduledTask() throws Exception {
        Thread.sleep(1000);
        assertCorrelationIdLogged(logReader);
    }
}
