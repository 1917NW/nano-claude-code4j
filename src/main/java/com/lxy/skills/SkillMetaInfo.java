package com.lxy.skills;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class SkillMetaInfo {

    private String name;
    private String description;
    private Map<String, String> attributes = new LinkedHashMap<>();
}
