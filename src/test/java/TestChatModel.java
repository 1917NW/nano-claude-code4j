import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lxy.common.CurrentEnvironment;
import com.lxy.model.ChatModel;
import com.lxy.tools.ToolExecuteRequest;
import com.lxy.tools.ToolManager;
import org.junit.Test;

import java.util.*;

public class TestChatModel {

    @Test
    public void testChatModel() {
        String model = "deepseek-chat";
        String baseUrl = "https://api.deepseek.com/chat/completions";
        String apiKey = System.getProperty("api.key");
        ChatModel chatModel = new ChatModel(model, baseUrl, apiKey);

//        UserMessage userMessage = new UserMessage("今天北京的天气怎么样");
//        NonStreamChatResponse result = chatModel.chat(Collections.singletonList(userMessage), ToolManager.getSubTools());
//        List<NonStreamChatResponse.Choice> choices = result.getChoices();
//        if(Objects.nonNull(choices)){
//            choices.forEach(choice -> {
//                AssistantMessage message = choice.getMessage();
//                List<AssistantMessage.ToolCall> toolCalls = message.getToolCalls();
//                for(AssistantMessage.ToolCall toolCall : toolCalls){
//                    Object toolRes = ToolManager.executeTool(new ToolExecuteRequest(toolCall.getFunction().getName(), JSONUtil.parseObj(toolCall.getFunction().getArguments())));
//                    System.out.println(toolRes);
//                }
//            });
//        }

    }

    @Test
    public void testTool() {
        List<JSONObject> tools = ToolManager.getSubTools();
        for(JSONObject tool : tools){
            System.out.println(JSONUtil.toJsonStr(tool));
        }

    }

    @Test
    public void testAgentLoop(){
        CurrentEnvironment.init();

        String json = "  {\"memory\": {\"name\": \"偏好-喜欢苹果\", \"description\": \"用户表达喜欢苹果\", \"type\": \"user\", \"content\": \"用户喜欢苹果，表达了对苹果的偏好。\"}}";
        ToolExecuteRequest request = new ToolExecuteRequest();
        request.setFunctionParam(JSONUtil.parseObj(json));
        request.setToolName("save_memory");
        System.out.println(ToolManager.executeTool(request));
        System.out.println(1);
    }
}
