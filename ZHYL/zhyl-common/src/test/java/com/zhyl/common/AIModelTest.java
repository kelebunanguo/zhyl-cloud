package com.zhyl.common;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

public class AIModelTest {
    public static void main(String[] args) {
        OpenAIClient client = OpenAIOkHttpClient.builder()
                .apiKey("bce-v3/ALTAK-e0qYh6pKedLZBYpLZNBhK/bb45811e90e32ecd84eb171bc24117dce22c6cbc") //将your_APIKey替换为真实值，如何获取API Key请查看：https://console.bce.baidu.com/iam/#/iam/apikey/list
                .baseUrl("https://qianfan.baidubce.com/v2/") //千帆ModelBuilder平台地址
                .build();

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage("你好") // 对话messages信息
                .model("ernie-5.0-thinking-preview") // 模型对应的model值，请查看支持的模型列表：https://cloud.baidu.com/doc/qianfan-docs/s/7m95lyy43
                .build();

        ChatCompletion chatCompletion = client.chat().completions().create(params);
        System.out.println(chatCompletion.choices().get(0).message().content());
    }
}
