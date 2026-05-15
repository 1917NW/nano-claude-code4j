package com.lxy.team;

import lombok.Data;

@Data
public class MessageEnvelope {
    private String type;
    private String from;
    private String content;
    private Long timestamp;
}
