package com.lxy.permisson.rules.allow;

import com.lxy.permisson.PermissionRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class AllowRule extends PermissionRule {
    String path;
    String content;

}
