package com.lxy.tools.impl;

import com.lxy.common.CurrentEnvironment;
import com.lxy.tools.annoation.FunctionCall;
import com.lxy.tools.annoation.ParamProperty;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class BashTool {

    private static final List<String> dangerousCommands;

    static {
        dangerousCommands = new ArrayList<>();
        dangerousCommands.add("rm -rf /");
        dangerousCommands.add("sudo");
        dangerousCommands.add("shutdown");
        dangerousCommands.add("reboot");
        dangerousCommands.add("> /dev/");
    }
    @FunctionCall(name = "run_bash", description = "执行一条shell命令")
    public String runBash(@ParamProperty(description = "shell命令") String command) {
        if(dangerousCommands.contains(command) || command.contains("rm") || command.contains("sudo")){
            return String.format("Error: 危险的命令:%s，已经被拦截", command);
        }

        try {

            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
            processBuilder.directory(new File(CurrentEnvironment.WORK_DIR));
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("命令执行失败，exitCode=" + exitCode + "\n" + output);
            }

            return output.toString();

        } catch (Exception e){
            log.error("runBash error", e);
            return String.format("RunBash Error: %s", e.getMessage());
        }
    }

    public static void main(String[] args) {
        String echoHello = new BashTool().runBash("echo hello");
        System.out.println(echoHello);
    }
}
