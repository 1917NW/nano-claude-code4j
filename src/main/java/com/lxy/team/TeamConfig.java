package com.lxy.team;

import lombok.Data;

import java.util.List;

@Data
public class TeamConfig {
    private String teamName;
    private List<TeamMember> members;
}
