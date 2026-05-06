package com.lxy.permisson;

import lombok.Data;

@Data
public class PermissionRule {

    String tool;

    BehaviorEnum behavior;

    String path;

    String content;
}
