package com.lxy.mcp;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * A small wrapper around a child process that communicates through stdin/stdout.
 * This is a good fit for MCP stdio transport.
 */
@Slf4j
public class SubprocessChannel implements Closeable {

    private final Process process;
    private final BufferedWriter writer;
    private final BufferedReader reader;
    private final BufferedReader errorReader;

    private SubprocessChannel(Process process) {
        this.process = process;
        this.writer = new BufferedWriter(
                new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8)
        );
        this.reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
        );
        this.errorReader = new BufferedReader(
                new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8)
        );
    }

    public static SubprocessChannel start(List<String> command) {
        return start(command, null);
    }

    public static SubprocessChannel start(List<String> command, Map<String, String> env) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(false);

            if (env != null && !env.isEmpty()) {
                processBuilder.environment().putAll(env);
            }

            Process process = processBuilder.start();
            return new SubprocessChannel(process);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public void sendMessage(String message)  {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        }catch (Exception e){
            log.error(e.getMessage());
            return;
        }
    }

    public String readLine() {
        try {
            return reader.readLine();
        }catch (Exception e){
            log.error(e.getMessage());
            return StrUtil.EMPTY;
        }
    }

    public JSONObject readJsonMessage() {
        String line = readLine();
        if (StrUtil.isBlank(line)) {
            return null;
        }

        try {
            return JSONUtil.parseObj(line);
        } catch (Exception e) {
            log.error("Failed to parse MCP JSON message: {}", line, e);
            return null;
        }
    }

    public String readErrorLine() throws IOException {
        return errorReader.readLine();
    }

    public boolean isAlive() {
        return process.isAlive();
    }

    public int waitFor() throws InterruptedException {
        return process.waitFor();
    }

    public void destroy() {
        process.destroy();
    }

    @Override
    public void close() {
        try {
            IOException closeException = null;
            try {
                writer.close();
            } catch (IOException e) {
                closeException = e;
            }

            try {
                reader.close();
            } catch (IOException e) {
                if (closeException == null) {
                    closeException = e;
                }
            }

            try {
                errorReader.close();
            } catch (IOException e) {
                if (closeException == null) {
                    closeException = e;
                }
            }

            process.destroy();

            if (closeException != null) {
                throw closeException;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        List<String> command = java.util.Arrays.asList(
                "python3",
                "-u",
                "-c",
                "import sys; " +
                        "print('child ready', flush=True); " +
                        "for line in sys.stdin: " +
                        " text=line.strip(); " +
                        " print('echo:' + text, flush=True)"
        );

        try (SubprocessChannel channel = SubprocessChannel.start(command)) {
            System.out.println(channel.readLine());

            channel.sendMessage("hello");
            System.out.println(channel.readLine());

            channel.sendMessage("mcp");
            System.out.println(channel.readLine());

            channel.destroy();
        }
    }
}
