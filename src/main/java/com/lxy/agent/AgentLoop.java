package com.lxy.agent;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lxy.background.BackgroundManager;
import com.lxy.background.Notification;
import com.lxy.common.CurrentEnvironment;
import com.lxy.hook.HookEvent;
import com.lxy.hook.HookExitCodeEnum;
import com.lxy.hook.HookResult;
import com.lxy.hook.HookRunner;
import com.lxy.http.FinishReasonEnum;
import com.lxy.memory.MemoryManager;
import com.lxy.message.impl.AssistantMessage;
import com.lxy.message.impl.ToolMessage;
import com.lxy.message.impl.UserMessage;
import com.lxy.model.ChatModel;
import com.lxy.http.NonStreamChatResponse;
import com.lxy.prompt.SystemPromptBuilder;
import com.lxy.recovery.RecoveryConstant;
import com.lxy.recovery.RecoveryDecision;
import com.lxy.recovery.RecoveryKindEnum;
import com.lxy.recovery.RecoveryState;
import com.lxy.skills.SkillRegistry;
import com.lxy.state.ChatState;
import com.lxy.tools.ToolManager;
import com.lxy.utils.CompactUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class AgentLoop {

    public static final int THRESHOLD = 50000;

    public static String SYSTEM_PROMPT;

    static {
        SystemPromptBuilder systemPromptBuilder = new SystemPromptBuilder();
        String core = String.format("你是一个工作在%s目录下的Agent，要求如下：\n" +
                "1.你需要对用户的任务进行规划，并且使用todo_write及时新增或更新规划的状态，记得在开始之前使用Todo Tool将要执行的步骤标记为IN_PROGRESS，当完成该步骤后，使用todo_write Tool将该步骤标记为COMPLETED。\n" +
                "2.对于某个任务，如果你没有对应的tool去执行，你可以把tool的执行使用run_subagent工具委托给子代理去做，其中子代理的工具集为%s\n"+
                "3.使用load_skill Tool来获取特定任务的知识\n" +
                "4.你应该在特定时候保存记忆：\n" +
                "- 用户表达了一个偏好（“我喜欢用 tab 缩进”“总是使用 pytest”）-> 保存的记忆类型为：user\n" +
                "- 用户纠正了你（“不要这样做 X”“刚才那样是错的，因为……”）-> 保存的记忆类型为：feedback\n" +
                "- 你了解到一个无法仅从当前代码中轻易推断出的项目事实（例如：某条规则存在是出于合规要求，或者某个遗留模块因为业务原因不能修改）-> 保存的记忆类型为：project\n" +
                "- 你知道了某个外部资源的位置（工单看板、仪表盘、文档 URL）-> 保存的记忆类型为：reference\n" +
                "你不应该保存的记忆：\n" +
                "- 任何可以很容易从代码中推导出来的信息（函数签名、文件结构、目录布局）\n" +
                "- 临时性的任务状态（当前分支、打开的 PR 编号、当前待办事项）\n" +
                "- 密钥或凭证（API key、密码）\n"+
                "5.尽量使用工具执行，而不是文字说明。", CurrentEnvironment.WORK_DIR, JSONUtil.toJsonStr(ToolManager.getSubToolInfoList()));

        SYSTEM_PROMPT = systemPromptBuilder
                .core(core)
                .tools(ToolManager.getParentToolInfoList())
                .skills(SkillRegistry.getSkillMetaInfo())
                .memories(MemoryManager.loadMemoryList())
                .build();

        System.out.println(SYSTEM_PROMPT);
    }


    public static void agentLoop(ChatState chatState){
        int rounds_since_todo = 0;
        RecoveryState recoveryState = chatState.getRecoveryState();
        while(true) {
            List<Notification> notifications = BackgroundManager.instance.drainNotifications();
            if(CollectionUtil.isNotEmpty(notifications)){
                List<String> lines = new ArrayList<>();
                for(Notification notification : notifications){
                    lines.add(String.format("后台任务 taskId:%s status:%s result:%s", notification.getTaskId(), notification.getStatus(), notification.getResult()));
                }
                String completedBackgroundRunTask = String.join("\n", lines);
                chatState.addMessage(new UserMessage(String.format("<background-result>\n%s\n</background-result>", completedBackgroundRunTask)));
            }

            RecoveryDecision recoveryDecision = null;
            try {
                CompactUtils.microCompact(chatState.getMessageList());

                if (CompactUtils.estimateTokens(chatState.getMessageList()) > THRESHOLD) {
                    chatState.setMessageList(CompactUtils.compactContext(chatState.getMessageList()));
                }

                NonStreamChatResponse chatResponse = ChatModel.instance.chat(SYSTEM_PROMPT, chatState.getMessageList(), ToolManager.getParentTools(), 8000);
                AssistantMessage assistantMessage = chatResponse.getAssistantMessage();
                chatState.addMessage(assistantMessage);

                if (CurrentEnvironment.log) {
                    System.out.printf("Assistant:%s%n", JSONUtil.toJsonStr(assistantMessage));
                }


                String finishReason = chatResponse.finishReason();
                if (FinishReasonEnum.STOP.isEqual(finishReason)) {
                    recoveryState.refresh();
                    return;
                }

                if (FinishReasonEnum.TOOL_CALL.isEqual(finishReason)) {
                    recoveryState.refresh();
                    List<AssistantMessage.ToolCall> toolCalls = assistantMessage.getToolCalls();
                    if (CollectionUtil.isNotEmpty(toolCalls)) {
                        for (AssistantMessage.ToolCall toolCall : toolCalls) {
                            AssistantMessage.Function tool = toolCall.getFunction();

                            if ("todo_write".equals(tool.getName())) {
                                rounds_since_todo = 0;
                            } else {
                                rounds_since_todo++;
                            }

                            // PreToolUse
                            boolean blocked = preToolUse(toolCall, chatState);
                            if (blocked) {
                                continue;
                            }

                            ToolMessage toolMessage = new ToolMessage(toolCall.getId(), ToolManager.executeToolCall(toolCall));
                            if (CurrentEnvironment.log) {
                                System.out.printf("Tool:%s%n", JSONUtil.toJsonStr(toolMessage));
                            }
                            chatState.addMessage(toolMessage);

                            // PostToolUse
                            postToolUse(toolCall, chatState, toolMessage);
                        }
                    }

                    if (rounds_since_todo >= 3) {
                        chatState.addMessage(new UserMessage("<reminder>记得更新你的todo计划</reminder>"));
                    }
                }
                recoveryDecision = chooseRecovery(finishReason, null);

                chatState.increaseTurnCount();
                chatState.setTransitionReason(finishReason);
            } catch (Exception e) {
                String errorMessage = e.getMessage();
                if(StrUtil.isBlank(errorMessage)){
                    errorMessage = StrUtil.EMPTY;
                }
                recoveryDecision = chooseRecovery(null, errorMessage.toLowerCase());
            }

            if(Objects.isNull(recoveryDecision)){
                recoveryState.refresh();
                continue;
            }

            switch (recoveryDecision.getKind()) {
                case CONTINUE:
                    chatState.addMessage(new UserMessage(RecoveryConstant.CONTINUE_MESSAGE));
                    continue;
                case COMPACT:
                    chatState.setMessageList(CompactUtils.compactContext(chatState.getMessageList()));
                    continue;
                case BACKOFF:
                    try {
                        Thread.sleep(recoveryState.getTransportAttempts() * 1000);
                        continue;
                    } catch (InterruptedException e) {
                        log.error(e.getMessage());
                    }
                case FAIL:
                    return;
            }
        }
    }

    public static boolean preToolUse(AssistantMessage.ToolCall toolCall, ChatState chatState){
        JSONObject hookPayLoad = new JSONObject();
        AssistantMessage.Function tool = toolCall.getFunction();
        hookPayLoad.set("tool_name", tool.getName());
        hookPayLoad.set("input", tool.getArguments());
        HookResult hookResult = HookRunner.runHooks(new HookEvent("PreToolUse", hookPayLoad));
        if(HookExitCodeEnum.STOP.equals(hookResult.getExitCode())){
            chatState.addMessage(new ToolMessage(toolCall.getId(), "工具执行被拦截，原因:" + hookResult.getMessage()));
            return true;
        }

        if(HookExitCodeEnum.ADD_MESSAGE.equals(hookResult.getExitCode())){
            chatState.addMessage(new UserMessage(hookResult.getMessage()));
        }

        return false;
    }

    public static void postToolUse(AssistantMessage.ToolCall toolCall, ChatState chatState, ToolMessage toolMessage){
        JSONObject hookPayLoad = new JSONObject();
        AssistantMessage.Function tool = toolCall.getFunction();
        hookPayLoad.set("tool_name", tool.getName());
        hookPayLoad.set("input", tool.getArguments());
        hookPayLoad.set("output", toolMessage.getContent());

        HookRunner.runHooks(new HookEvent("PostToolUse", hookPayLoad));
    }


    public static RecoveryDecision chooseRecovery(String finishReason, String errorText){
        if(Objects.isNull(finishReason) && Objects.isNull(errorText)){
            return null;
        }

        if(FinishReasonEnum.LENGTH.isEqual(finishReason)){
            return new RecoveryDecision(RecoveryKindEnum.CONTINUE, "输出被截断");
        }

        if(StrUtil.isNotBlank(errorText) && errorText.contains("prompt") && errorText.contains("long")){
            return new RecoveryDecision(RecoveryKindEnum.COMPACT, "上下文太长");
        }

        if(StrUtil.isNotBlank(errorText) && (errorText.contains("timeout") || errorText.contains("rate") || errorText.contains("unavailable") || errorText.contains("connection"))){
            return new RecoveryDecision(RecoveryKindEnum.BACKOFF, "网络层连接异常");
        }

        return new RecoveryDecision(RecoveryKindEnum.FAIL, "未知异常或无法恢复的错误");
    }


    public static Double backOffDelay(Integer attempts){
        return Math.min(1*Math.pow(2, attempts), 30) + RandomUtil.randomInt(0,1);
    }






}
