package com.lxy.mcp;

import cn.hutool.json.JSONObject;
import lombok.Data;

public class JsonRpcProtocol {

    @Data
    static class BaseMessage{
        private String jsonrpc = "2.0";
        private String id;
    }

    @Data
    static class CallMessage extends BaseMessage{
        private String method;
        private JSONObject params;
    }

    static class ResponseMessage extends BaseMessage{

    }

}
