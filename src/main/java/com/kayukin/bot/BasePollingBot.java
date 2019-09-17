package com.kayukin.bot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.Optional;

import static java.util.function.Predicate.not;

@Slf4j
public abstract class BasePollingBot extends TelegramLongPollingBot {
    private final String username;
    private final String token;

    public BasePollingBot(String username, String token) {
        this.username = username;
        this.token = token;
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) {
        try {
            return super.execute(method);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Received update {}", update);
        try {
            String response = handleUpdate(update);
            SendMessage sendMessage = new SendMessage()
                    .setChatId(update.getMessage().getChatId())
                    .setText(response);
            execute(sendMessage);
        } catch (Exception e) {
            SendMessage sendMessage = new SendMessage()
                    .setChatId(update.getMessage().getChatId())
                    .setText(getMessage(e));
            execute(sendMessage);
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    public abstract String handleUpdate(Update update);

    private String getMessage(Exception ex) {
        return Optional.ofNullable(ex.getMessage())
                .filter(not(String::isEmpty))
                .orElse("Unknown error");
    }
}
