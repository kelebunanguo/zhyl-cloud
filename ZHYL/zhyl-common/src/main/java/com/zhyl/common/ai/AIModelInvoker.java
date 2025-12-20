package com.zhyl.common.ai;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ResponseFormatJsonObject;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
/**
 * 调用千帆大模型
 */
public class AIModelInvoker {
    @Autowired
    private BaiduAIProperties baiduAIProperties;

    public String qianfanInvoker(String prompt) {
        OpenAIClient client = OpenAIOkHttpClient.builder()
                .apiKey(baiduAIProperties.getApiKey())
                .baseUrl(baiduAIProperties.getBaseUrl())
                .build();

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage(prompt)
                .model(baiduAIProperties.getModel())
                .responseFormat(ChatCompletionCreateParams.ResponseFormat.ofJsonObject(ResponseFormatJsonObject.builder().build()))
                .build();

        ChatCompletion chatCompletion = client.chat().completions().create(params);
        return chatCompletion.choices().get(0).message().content().orElseGet(() -> "");
    }
}
