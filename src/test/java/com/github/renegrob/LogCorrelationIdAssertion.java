package com.github.renegrob;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogCorrelationIdAssertion {
    private final static Pattern NEW_MDC_PATTERN = Pattern.compile("Created new correlationId\\: ([\\w\\-]+)");
    private final static Pattern MDC_PATTERN = Pattern.compile("MDC\\:\\{correlationId=([\\-\\w]+)\\}");

    private static String extract(Pattern pattern, String line) {
        final Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static void assertCorrelationIdLogged(LogTestReader logReader) {
        String correlationId = null;
        for (String line: logReader.logLines())  {
            if (correlationId == null) {
                correlationId = extract(NEW_MDC_PATTERN, line);
            } else {
                assertEquals(correlationId, extract(MDC_PATTERN, line),
                        String.format("CorrelationId (%s) not found in line: %s.", correlationId, line));
            }
        }
    }
}
