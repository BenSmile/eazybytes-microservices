package com.bkafirongo.message.functions;


import com.bkafirongo.message.dto.AccountsMsgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class MessageFunction {

    private static final Logger log = LoggerFactory.getLogger(MessageFunction.class);

    @Bean
    public Function<AccountsMsgDto, AccountsMsgDto> email() {
        return accountsMsgDto -> {
            log.info("Sending email with details : {}", accountsMsgDto);
            return accountsMsgDto;
        };
    }

    @Bean
    public Function<AccountsMsgDto, Long> sms() {
        return accountsMsgDto -> {
            log.info("Sending sms with details : {}", accountsMsgDto);
            return accountsMsgDto.accountNumber();
        };
    }
}