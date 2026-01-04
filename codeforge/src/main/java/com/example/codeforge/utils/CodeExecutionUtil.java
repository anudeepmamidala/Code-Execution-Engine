package com.example.codeforge.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class CodeExecutionUtil {

    private static final long TIMEOUT_SECONDS = 2;
    private static final int MAX_OUTPUT_CHARS = 10_000;
    private static final double FLOAT_EPS = 1e-5;
    private static final String PYTHON_CMD = "python";

    private CodeExecutionUtil() {}

    // ---------- RESULT ----------
    public record ExecutionResult(
            String stdout,
            String stderr,
            boolean passed,
            String verdict   // PASSED | FAILED | ERROR | TIMEOUT
    ) {}

    public static ExecutionResult executeAndJudgePython(
            String userCode,
            String input,
            String expectedOutput
    ) {

        if (userCode == null || userCode.isBlank()) {
            return new ExecutionResult("", "User code cannot be empty", false, "ERROR");
        }

        if (expectedOutput == null || expectedOutput.isBlank()) {
            return new ExecutionResult("", "Expected output not configured", false, "ERROR");
        }

        Path tempDir = null;

        try {
            tempDir = Files.createTempDirectory("codeforge_");
            Path codeFile = tempDir.resolve("solution.py");
            Files.writeString(codeFile, userCode, StandardCharsets.UTF_8);

            Process process;
            try {
                process = new ProcessBuilder(
                        PYTHON_CMD,
                        codeFile.toAbsolutePath().toString()
                )
                        .directory(tempDir.toFile())
                        .redirectErrorStream(false)
                        .start();
            } catch (IOException e) {
                return new ExecutionResult(
                        "",
                        "Python interpreter not found",
                        false,
                        "ERROR"
                );
            }

            // ✅ FIXED: stdin handling with proper newline conversion
            try (BufferedWriter writer =
                         new BufferedWriter(
                                 new OutputStreamWriter(
                                         process.getOutputStream(),
                                         StandardCharsets.UTF_8))) {

                if (input != null && !input.isBlank()) {
                    // ✅ Convert literal \n to actual newlines
                    String processedInput = input.replace("\\n", "\n");
                    writer.write(processedInput);
                    
                    // ✅ Ensure input ends with newline for proper EOF
                    if (!processedInput.endsWith("\n")) {
                        writer.write("\n");
                    }
                    writer.flush();
                }
                // ✅ Close stdin to signal EOF to Python
            } // auto-closes writer, which closes the output stream

            boolean finished =
                    process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                return new ExecutionResult(
                        "",
                        "Time Limit Exceeded (" + TIMEOUT_SECONDS + "s)",
                        false,
                        "TIMEOUT"
                );
            }

            String stdout = readStream(process.getInputStream());
            String stderr = readStream(process.getErrorStream());
            int exitCode = process.exitValue();



            // ❗ Correct runtime error detection
            if (exitCode != 0) {
                return new ExecutionResult(
                        stdout,
                        stderr,
                        false,
                        "ERROR"
                );
            }

            boolean passed = judge(stdout, expectedOutput);

            return new ExecutionResult(
                    stdout,
                    stderr,
                    passed,
                    passed ? "PASSED" : "FAILED"
            );

        } catch (Exception e) {
            return new ExecutionResult(
                    "",
                    "Execution error: " + e.getMessage(),
                    false,
                    "ERROR"
            );
        } finally {
            cleanup(tempDir);
        }
    }

    // ---------- JUDGING ----------

    

    private static boolean judge(String stdout, String expected) {
        String out = normalize(stdout);
        String exp = normalize(expected);

        // FLOAT compare (needed for Median problem)
        if (looksNumeric(out) && looksNumeric(exp)) {
            try {
                double a = Double.parseDouble(out);
                double b = Double.parseDouble(exp);
                return Math.abs(a - b) <= FLOAT_EPS;
            } catch (NumberFormatException ignored) {}
        }

        // fallback exact match
        return out.equals(exp);
    }

    private static boolean looksNumeric(String s) {
        // ✅ IMPROVED: Handle scientific notation and edge cases
        return s.matches("[-+]?(?:\\d+\\.?\\d*|\\.\\d+)(?:[eE][-+]?\\d+)?");
    }

    private static String normalize(String s) {
        return s == null ? "" : s.trim();
    }

    // ---------- STREAM ----------

    private static String readStream(InputStream stream) throws IOException {
        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(stream, StandardCharsets.UTF_8))) {

            StringBuilder sb = new StringBuilder();
            String line;
            boolean first = true;

            while ((line = reader.readLine()) != null) {
                int nextLen = sb.length() + line.length() + 1;
                if (nextLen > MAX_OUTPUT_CHARS) {
                    sb.append("\n[Output truncated]");
                    break;
                }
                if (!first) sb.append("\n");
                sb.append(line);
                first = false;
            }
            return sb.toString();
        }
    }

    // ---------- CLEANUP ----------

    private static void cleanup(Path tempDir) {
        if (tempDir == null) return;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        deleteDirectory(tempDir.toFile());
    }

    private static void deleteDirectory(File dir) {
        if (!dir.exists()) return;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) deleteDirectory(f);
                else if (!f.delete()) f.deleteOnExit();
            }
        }
        if (!dir.delete()) dir.deleteOnExit();
    }
}