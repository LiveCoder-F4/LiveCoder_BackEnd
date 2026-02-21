package com.idea_l.livecoder.problem.docker.java;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JavaJudgeService {

    public JudgeResult judge(String code, String input, int memoryLimitMB) throws Exception {

        Path workDir = Files.createTempDirectory("java-judge");

        Files.writeString(workDir.resolve("Main.java"), code);

        String memoryLimit = memoryLimitMB + "m";

        // time 명령어를 사용하여 메모리(KB)와 시간(초) 측정
        // 포맷: MEMORY:최대메모리(KB) TIME:경과시간(초)
        ProcessBuilder pb = new ProcessBuilder(
                "docker", "run", "--rm",
                "-i",
                "--memory", memoryLimit,
                "--memory-swap", memoryLimit,
                "-v", workDir.toAbsolutePath() + ":/app",
                "java-judge",
                "sh", "-c",
                "cd /app && javac Main.java && /usr/bin/time -f \"MEMORY:%M TIME:%e\" java -Xmx" + memoryLimit + " Main"
        );


        Process process = pb.start();

        try (BufferedWriter writer =
                     new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
            writer.write(input);
            writer.newLine();
            writer.flush();
        }

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));

        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }

        BufferedReader errorReader =
                new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder errorOutput = new StringBuilder();
        
        long memoryUsage = 0;
        double executionTime = 0.0;

        while ((line = errorReader.readLine()) != null) {
            if (line.contains("MEMORY:") && line.contains("TIME:")) {
                try {
                    Pattern pattern = Pattern.compile("MEMORY:(\\d+) TIME:([\\d.]+)");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        memoryUsage = Long.parseLong(matcher.group(1));
                        executionTime = Double.parseDouble(matcher.group(2));
                    }
                } catch (Exception e) {
                    // ignore
                }
            } else {
                errorOutput.append(line).append("\n");
            }
        }


        boolean finished = process.waitFor(2, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            return new JudgeResult("", false, "Time Limit Exceeded", 0, 0);
        }

        int exitCode = process.exitValue();
        if (exitCode != 0) {
            String errorString = errorOutput.toString();
            if (exitCode == 137 || errorString.contains("OutOfMemoryError")) {
                return new JudgeResult("", false, "Memory Limit Exceeded", memoryUsage, (long)(executionTime * 1000));
            }
            
            // 컴파일 에러 확인
            if (errorString.contains("error:") && errorString.contains("Main.java")) {
                 return new JudgeResult("", false, "Compile Error: " + errorString, memoryUsage, (long)(executionTime * 1000));
            }

            return new JudgeResult("", false, "Runtime Error: " + errorString, memoryUsage, (long)(executionTime * 1000));
        }

        return new JudgeResult(output.toString(), true, null, memoryUsage, (long)(executionTime * 1000));
    }

    public static class JudgeResult {
        public String output;
        public boolean success;
        public String error;
        public long memoryUsage; // KB
        public long executionTime; // ms

        public JudgeResult(String output, boolean success, String error, long memoryUsage, long executionTime) {
            this.output = output;
            this.success = success;
            this.error = error;
            this.memoryUsage = memoryUsage;
            this.executionTime = executionTime;
        }
    }
}
