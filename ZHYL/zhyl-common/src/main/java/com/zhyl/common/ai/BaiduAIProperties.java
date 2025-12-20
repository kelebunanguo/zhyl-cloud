package com.zhyl.common.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "baidu.qianfan")
/**
 *拿到application.yml中的信息，指定配置项
 * baidu.qianfan
 **/
public class BaiduAIProperties {
    private String apiKey;
    private String baseUrl;
    private String model;
}
