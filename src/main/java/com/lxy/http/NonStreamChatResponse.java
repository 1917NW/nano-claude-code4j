package com.lxy.http;

import cn.hutool.core.collection.CollectionUtil;
import com.lxy.message.impl.AssistantMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NonStreamChatResponse {

    private String id;
    private String object;
    private Long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;
    private String systemFingerprint;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Choice {
        private Integer index;
        private AssistantMessage message;
        private String finishReason;
    }



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Usage {
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer totalTokens;
        private PromptTokensDetails promptTokensDetails;
        private Integer promptCacheHitTokens;
        private Integer promptCacheMissTokens;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PromptTokensDetails {
        private Integer cachedTokens;
    }

    public AssistantMessage getAssistantMessage(){
        if(CollectionUtil.isNotEmpty(choices)){
            return (AssistantMessage) choices.get(0).getMessage();
        }

        return null;
    }

    public String finishReason(){
        if(CollectionUtil.isNotEmpty(choices)){
            return choices.get(0).getFinishReason();
        }

        return null;
    }
}
