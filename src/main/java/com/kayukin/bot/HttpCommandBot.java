package com.kayukin.bot;

import com.kayukin.model.Request;
import com.kayukin.service.RequestExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class HttpCommandBot extends BasePollingBot {
    private final RequestExecutionService requestExecutionService;
    private final Set<Integer> allowedUsers;
    private final Map<String, Request> requestMap;

    public HttpCommandBot(RequestExecutionService requestExecutionService,
                          String username, String token,
                          Set<Integer> allowedUsers,
                          Map<String, Request> requestMap) {
        super(username, token);
        this.requestExecutionService = requestExecutionService;
        this.allowedUsers = allowedUsers;
        this.requestMap = requestMap;
    }

    @Override
    public String handleUpdate(Update update) {
        if (!isAuthorized(update)) {
            return "You are not authorized";
        }
        String message = stripSlash(update.getMessage().getText());
        Request request = Optional.ofNullable(requestMap.get(message))
                .orElseThrow(() -> new IllegalArgumentException("Unknown command: " + message));
        requestExecutionService.execute(request);
        return request.getSuccessResponse();
    }

    private String stripSlash(String message) {
        return message.startsWith("/") ? message.substring(1) : message;
    }

    private boolean isAuthorized(Update update) {
        Integer userId = update.getMessage().getFrom().getId();
        return allowedUsers.contains(userId);
    }


}