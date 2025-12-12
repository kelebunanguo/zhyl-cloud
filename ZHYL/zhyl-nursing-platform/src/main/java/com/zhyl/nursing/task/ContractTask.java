package com.zhyl.nursing.task;

import com.zhyl.nursing.service.IContractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class ContractTask {
    @Autowired
    private IContractService contractService;
    public void updateContractStatusTask() {
        contractService.updateContractStatus();
        log.info("定时更新合同状态成功：{}", LocalDateTime.now());

    }
}
