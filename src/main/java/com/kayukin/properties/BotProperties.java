package com.kayukin.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@ConfigurationProperties("bot")
@Component
@Data
public class BotProperties {
    private String username;

    private String token;

    private Set<Integer> allowedUsers;

    private Map<String, RequestProperties> commandsMapping;

    @Data
    public static class RequestProperties {
        private String url;
        private Resource file;
        private String successResponse;
    }
}
