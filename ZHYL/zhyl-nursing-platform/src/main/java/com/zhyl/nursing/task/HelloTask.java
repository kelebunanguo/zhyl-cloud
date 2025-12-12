package com.zhyl.nursing.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class HelloTask {
    public void myTask() {
        log.info("定时任务启动：{}");
    }
}
