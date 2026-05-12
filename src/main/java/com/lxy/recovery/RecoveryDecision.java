package com.lxy.recovery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecoveryDecision {
    private RecoveryKindEnum kind;
    private String reason;
}
