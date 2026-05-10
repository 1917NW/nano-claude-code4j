package com.lxy.permisson.rules;

import cn.hutool.json.JSONObject;
import com.lxy.permisson.ModeEnum;
import com.lxy.permisson.PermissionRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class ModeRule extends PermissionRule {

    ModeEnum modeEnum;
}
