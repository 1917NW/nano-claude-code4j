package com.lxy.skills;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class SkillRegistry {

    public static Map<String, SkillDetail> skills = new HashMap<>();

    // 项目级Skill加载，工作目录/.claude/skills
    private static final String PROJECT_SKILLS_RESOURCE_DIR = "/.claude/skills";

    // TODO: 系统级Skill加载

    // TODO: 插件Skill加载

    private static final String SKILL_FILE_NAME = "SKILL.md";

    static {
        skills = loadSkills();
    }

    public static List<SkillMetaInfo> getSkillMetaInfo(){
        return skills.values().stream()
                .map(SkillDetail::getMetaInfo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static String getSkillDescription(){
        List<SkillMetaInfo> skillMetaInfos = getSkillMetaInfo();
        if(CollectionUtil.isEmpty(skillMetaInfos)){
            return "没有可用的Skills";
        }

        List<String> lines = new ArrayList<>();
        for(SkillMetaInfo skillMetaInfo: skillMetaInfos){
            lines.add(String.format("  - %s: %s", skillMetaInfo.getName(), skillMetaInfo.getDescription()));
        }

        return String.join("\n", lines);
    }

    public static String getSkillBody(String skillName){
        SkillDetail skillDetail = skills.get(skillName);
        return skillDetail == null ? null : skillDetail.getSkillBody();
    }

    public static Map<String, SkillDetail> loadSkills() {
        Map<String, SkillDetail> loadedSkills = new HashMap<>();
        List<Path> skillFiles = findSkillFiles();
        if(CollectionUtil.isEmpty(skillFiles)){
            return loadedSkills;
        }

        for (Path skillPath : skillFiles) {
            SkillDetail detail = parseSkillFile(skillPath);
            SkillMetaInfo metaInfo = detail.getMetaInfo();
            if (metaInfo != null && metaInfo.getName() != null && !metaInfo.getName().trim().isEmpty()) {
                loadedSkills.put(metaInfo.getName(), detail);
            }
        }
        return loadedSkills;
    }

    private static List<Path> findSkillFiles() {
        Path skillsDir = Paths.get(System.getProperty("user.dir") + PROJECT_SKILLS_RESOURCE_DIR);

        try (Stream<Path> stream = Files.walk(skillsDir)) {

            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> SKILL_FILE_NAME.equals(path.getFileName().toString()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.info("查找Skill文件失败, e:{}", e.getMessage());
        }
        return null;

    }

    private static SkillDetail parseSkillFile(Path skillPath) {
        try {
            String markdown = new String(Files.readAllBytes(skillPath), StandardCharsets.UTF_8);
            return SkillMarkdownParser.parse(markdown);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read skill file: " + skillPath, e);
        }
    }
}
