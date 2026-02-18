package com.idea_l.livecoder.problem.docker.java;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Service
public class JavaJudgeService {

    public JudgeResult judge(String code, String input, int memoryLimitMB) throws Exception {

        Path workDir = Files.createTempDirectory("java-judge");

        Files.writeString(workDir.resolve("Main.java"), code);

        // 메모리 제한 설정 (Docker --memory 옵션 사용)
        String memoryLimit = memoryLimitMB + "m";

        ProcessBuilder pb = new ProcessBuilder(
                "docker", "run", "--rm",
                "-i",
                "--memory", memoryLimit, // 메모리 제한 추가
                "--memory-swap", memoryLimit, // 스왑 메모리도 동일하게 제한하여 하드 리미트 설정
                "-v", workDir.toAbsolutePath() + ":/app",
                "java-judge",
                "sh", "-c",
                "cd /app && javac Main.java && java -Xmx" + memoryLimit + " Main" // JVM 힙 메모리도 제한
        );


        Process process = pb.start();

        // 입력 전달
        try (BufferedWriter writer =
                     new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
            writer.write(input);
            writer.newLine();
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

        // 에러 출력 읽기 (메모리 초과 시 에러 메시지 확인용)
        BufferedReader errorReader =
                new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder errorOutput = new StringBuilder();
        while ((line = errorReader.readLine()) != null) {
            errorOutput.append(line);
        }


        boolean finished = process.waitFor(2, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            return new JudgeResult("", false, "Time Limit Exceeded");
        }

        int exitCode = process.exitValue();
        if (exitCode != 0) {
            String errorString = errorOutput.toString();
            // Docker OOM Killer (exit code 137) 또는 JVM OutOfMemoryError 확인
            if (exitCode == 137 || errorString.contains("OutOfMemoryError")) {
                return new JudgeResult("", false, "Memory Limit Exceeded");
            }
            return new JudgeResult("", false, "Runtime Error"/* + errorString*/);
        }

        return new JudgeResult(output.toString(), true, null);
    }

    public static class JudgeResult {
        public String output;
        public boolean success;
        public String error;

        public JudgeResult(String output, boolean success, String error) {
            this.output = output;
            this.success = success;
            this.error = error;
        }
    }
}
