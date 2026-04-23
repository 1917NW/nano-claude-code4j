package com.lxy.tools.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.lxy.common.CurrentEnvironment;
import com.lxy.tools.annoation.FunctionCall;
import com.lxy.tools.annoation.ParamProperty;
import lombok.extern.slf4j.Slf4j;


import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Slf4j
public class LocalFileTool {

    private final int readLineLimits = 5000;

    @FunctionCall(name = "read_file", description = "读取某个文件的内容")
    public String readFile(@ParamProperty(description = "文件名，相对工作目录的路径") String fileName){
        String safePath = safePath(fileName);
        File file = new File(safePath);
        if (!file.exists()){
            return StrUtil.EMPTY;
        }

        String text = FileUtil.readString(file, StandardCharsets.UTF_8);
        String[] lines = text.split("\\n");

        if(lines.length > readLineLimits){
            lines = Arrays.copyOf(lines, readLineLimits);
        }

        return String.join("\n", lines);
    }

    @FunctionCall(name = "write_file", description = "写入内容到某个文件")
    public String writeFile(@ParamProperty(description = "文件名，相对工作目录的路径")String fileName,
                            @ParamProperty(description = "文件内容")String content){
        try {
            Path path = Paths.get(safePath(fileName));
            Files.write(path, content.getBytes(StandardCharsets.UTF_8));
            return "写入成功";
        }catch (Exception e){
            log.error(e.getMessage());
        }

        return "写入文件失败";
    }

    @FunctionCall(name = "edit_file", description = "编辑某个文件的内容")
    public String editFile(@ParamProperty(description = "文件名，相对工作目录的路径")String fileName,
                           @ParamProperty(description = "替换后的完整文本")String newText){
        try{
            String safePath = safePath(fileName);
            File file = new File(safePath);
            if (!file.exists()){
                return StrUtil.EMPTY;
            }

            Path path = Paths.get(safePath(fileName));
            Files.write(
                    path,
                    newText.getBytes(StandardCharsets.UTF_8)
            );

        }catch (Exception e){
            log.error(e.getMessage());
        }

        return "编辑文件失败";
    }


    private String safePath(String fileName) {
        return CurrentEnvironment.WORK_DIR + "/" + fileName;
    }
}
