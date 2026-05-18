package com.lxy.cladue;

import cn.hutool.core.io.FileUtil;
import com.lxy.common.CurrentEnvironment;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ClaudeMdLoader {

    public static String loadClaudeMdFile(){
        String fileName = CurrentEnvironment.WORK_DIR + "/CLAUDE.md";
        File file = new File(fileName);
        return FileUtil.readString(file, StandardCharsets.UTF_8);
    }
}
