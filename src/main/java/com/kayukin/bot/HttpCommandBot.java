package com.kayukin.bot;

import com.kayukin.model.Request;
import com.kayukin.service.RequestExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;
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
    public SendMessage handleUpdate(Update update) {
        if (!isAuthorized(update)) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("You are not authorized");
            return sendMessage;
        }
        if (update.getMessage().isCommand() && update.getMessage().getText().equals("/start")) {
            SendMessage sendMessage = keyboardMessage();
            sendMessage.setText("Started");
            return sendMessage;
        }
        SendMessage sendMessage = new SendMessage();
        String message = stripSlash(update.getMessage().getText());
        Request request = Optional.ofNullable(requestMap.get(message))
                .orElseThrow(() -> new IllegalArgumentException("Unknown command: " + message));
        requestExecutionService.execute(request);
        sendMessage.setText(request.getSuccessResponse());
        sendMessage.setReplyToMessageId(update.getMessage().getMessageId());
        return sendMessage;
    }

    private String stripSlash(String message) {
        return message.startsWith("/") ? message.substring(1) : message;
    }

    private boolean isAuthorized(Update update) {
        Integer userId = update.getMessage().getFrom().getId();
        return allowedUsers.contains(userId);
    }

    private SendMessage keyboardMessage() {

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(false);

        KeyboardRow row = new KeyboardRow();
        requestMap.keySet().forEach(row::add);
        markup.setKeyboard(List.of(row));

        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setReplyMarkup(markup);
        return message;
    }
}