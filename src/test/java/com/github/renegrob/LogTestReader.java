package com.github.renegrob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LogTestReader {
    private int skipLines;

    public LogTestReader() {
        skipLines = readFullLog().size();
    }

    private final List<String> readFullLog() {
        try {
            return Files.readAllLines(Path.of("target/quarkus.log"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String>  logLines() {
        List<String> allLInes = readFullLog();
        return allLInes.subList(skipLines, allLInes.size());
    }
}
