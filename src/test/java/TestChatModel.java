import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lxy.message.impl.UserMessage;
import com.lxy.model.ChatModel;
import com.lxy.tools.Tool;
import com.lxy.tools.ToolManager;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TestChatModel {

    @Test
    public void testChatModel() {
        String model = "deepseek-chat";
        String baseUrl = "https://api.deepseek.com/chat/completions";
        String apiKey = "sk-67003ab0ca6542efb8a488da3de79874";
        ChatModel chatModel = new ChatModel(model, baseUrl, apiKey);

        UserMessage userMessage = new UserMessage("今天北京的天气怎么样");
        JSONObject result = chatModel.chat(Collections.singletonList(userMessage), null);
        System.out.println(JSONUtil.toJsonStr(result));

    }

    @Test
    public void testTool() {
        List<Tool> tools = ToolManager.getTools();
        for(Tool tool : tools){
            System.out.println(JSONUtil.toJsonStr(tool));
        }

    }
}
