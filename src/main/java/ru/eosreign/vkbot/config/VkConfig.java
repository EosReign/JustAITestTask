package ru.eosreign.vkbot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class VkConfig {

    @Value("${vk.confirmation.code}")
    private String confirmationCode;

    @Value("${vk.callbackapi.version}")
    private String callbackApiVersion;

    @Value("${vk.access.token}")
    private String accessToken;

}