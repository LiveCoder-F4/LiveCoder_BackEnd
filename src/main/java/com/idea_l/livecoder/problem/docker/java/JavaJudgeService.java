package com.idea_l.livecoder.problem.docker.java;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Service
public class JavaJudgeService {

    public String judge(String code, String input) throws Exception {

        Path workDir = Files.createTempDirectory("java-judge");

        Files.writeString(workDir.resolve("Main.java"), code);

        ProcessBuilder pb = new ProcessBuilder(
                "docker", "run", "--rm", "-i",
                "-v", workDir.toAbsolutePath() + ":/app",
                "java-judge"
        );

        Process process = pb.start();

        // 입력 전달
        try (BufferedWriter writer =
                     new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
            writer.write(input);
            writer.flush();
        }

        // 출력 읽기
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));

        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }

        boolean finished = process.waitFor(2, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("Time Limit Exceeded");
        }

        return output.toString();
    }
}
