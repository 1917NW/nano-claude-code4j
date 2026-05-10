package com.lxy.permisson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecisionResult {
    public BehaviorEnum behavior;

    public String reason;
}
