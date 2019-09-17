package com.kayukin.service;

import com.kayukin.model.Request;
import org.apache.commons.codec.Charsets;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;

import java.io.IOException;

public class RequestExecutionService {
    private final HttpClient httpClient;

    public RequestExecutionService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void execute(Request request) {
        HttpUriRequest httpUriRequest = RequestBuilder.post(request.getUrl())
                .setEntity(new StringEntity(request.getBody(), Charsets.UTF_8))
                .build();
        try {
            httpClient.execute(httpUriRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
