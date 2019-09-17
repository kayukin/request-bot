package com.kayukin.config;

import com.kayukin.bot.HttpCommandBot;
import com.kayukin.model.Request;
import com.kayukin.properties.BotProperties;
import com.kayukin.service.RequestExecutionService;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.httpclient.LogbookHttpRequestInterceptor;
import org.zalando.logbook.httpclient.LogbookHttpResponseInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class BotConfig {
    @Bean
    public HttpCommandBot bot(RequestExecutionService requestExecutionService,
                              BotProperties properties) {
        Map<String, Request> requestMap = properties.getCommandsMapping().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> convertRequest(entry.getValue())));
        return new HttpCommandBot(requestExecutionService, properties.getUsername(), properties.getToken(),
                properties.getAllowedUsers(), requestMap);
    }

    private Request convertRequest(BotProperties.RequestProperties requestProperties) {
        Request request = new Request();
        request.setUrl(requestProperties.getUrl());
        request.setSuccessResponse(requestProperties.getSuccessResponse());
        try {
            request.setBody(IOUtils.toString(requestProperties.getFile().getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return request;
    }

    @Bean
    public HttpClient httpClient(LogbookHttpRequestInterceptor httpRequestInterceptor,
                                 LogbookHttpResponseInterceptor httpResponseInterceptor) {
        return HttpClientBuilder.create()
                .addInterceptorFirst(httpRequestInterceptor)
                .addInterceptorFirst(httpResponseInterceptor)
                .build();
    }

    @Bean
    public LogbookHttpRequestInterceptor logbookHttpRequestInterceptor(final Logbook logbook) {
        return new LogbookHttpRequestInterceptor(logbook);
    }

    @Bean
    public LogbookHttpResponseInterceptor logbookHttpResponseInterceptor() {
        return new LogbookHttpResponseInterceptor();
    }

    @Bean
    public Logbook logbook() {
        return Logbook.create();
    }

    @Bean
    public RequestExecutionService requestExecutionService(HttpClient httpClient) {
        return new RequestExecutionService(httpClient);
    }
}
