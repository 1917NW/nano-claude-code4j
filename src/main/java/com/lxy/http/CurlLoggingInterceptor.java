package com.lxy.http;

import okhttp3.*;
import okio.Buffer;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class CurlLoggingInterceptor implements Interceptor {
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        
        // 构建 curl 命令
        StringBuilder curlCommand = new StringBuilder("curl -X ");
        curlCommand.append(request.method());
        
        // 添加 URL
        curlCommand.append(" '").append(request.url()).append("'");
        
        // 添加请求头
        Headers headers = request.headers();
        for (int i = 0; i < headers.size(); i++) {
            String name = headers.name(i);
            String value = headers.value(i);
            curlCommand.append(" \\\n  -H '").append(name).append(": ").append(value).append("'");
        }
        
        // 添加请求体（如果有）
        RequestBody requestBody = request.body();
        if (requestBody != null) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = StandardCharsets.UTF_8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(StandardCharsets.UTF_8);
            }
            curlCommand.append(" \\\n  -d '").append(buffer.readString(charset)).append("'");
        }
        
        // 打印 curl 命令
        System.out.println("CURL COMMAND:");
        System.out.println(curlCommand.toString());
        System.out.println();
        
        return chain.proceed(request);
    }
}