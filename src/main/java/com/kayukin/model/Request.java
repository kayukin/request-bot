package com.kayukin.model;

import lombok.Data;

@Data
public class Request {
    private String url;
    private String body;
    private String successResponse;
}
