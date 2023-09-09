package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.NotificationService;

import javax.annotation.PostConstruct;
import java.util.List;
@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationTaskRepository notificationTaskRepository;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(this::processUpdate);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void processUpdate(Update update) {
        logger.info("Processing update: {}", update);
        Message message = update.message();
        if (message != null && message.text() != null) {
            long chatId = message.chat().id();

            if ("/start".equals(message.text())) {
                logger.info("Received /start command from chat ID: {}", chatId);
                sendWelcomeMessage(chatId);
            } else {
                processNonStartMessage(message, chatId);
            }
        }
    }

    private void sendWelcomeMessage(long chatId) {
        String responseText = "Привет! Добро пожаловать! Я ботище, готовый отвечать на ваши команды.";
        telegramBot.execute(new SendMessage(chatId, responseText));
    }

    private void processNonStartMessage(Message message, long chatId) {
        String text = message.text();
        if(isValidFormat(text)) {
            NotificationTask notificationTask = notificationService.parseAndSaveReminder(text, chatId);

            if (notificationTask != null) {
                notificationTaskRepository.save(notificationTask);
                sendNotificationMessage(chatId, notificationTask.getMessageText());
            } else {
                sendUnknownMessageResponse(chatId);
            }
        }else {
            sendUnknownMessageResponse(chatId);
        }
    }

    private void sendNotificationMessage(long chatId, String notificationText) {
        String messageText = "Уведомление: " + notificationText;
        telegramBot.execute(new SendMessage(chatId, messageText));
    }

    private void sendUnknownMessageResponse(long chatId) {
        String responseText = "Извините, я не понимаю ваше сообщение или оно не соответствует формату.";
        telegramBot.execute(new SendMessage(chatId, responseText));
    }
    private boolean isValidFormat(String text) {
        // Разделите текст сообщения на две части с помощью символа ":"
        String[] parts = text.split(":");


        // Проверьте, что получены две части и обе части не пусты
        if (parts.length == 2 && !parts[0].trim().isEmpty() && !parts[1].trim().isEmpty()) {
            return true; // Формат корректен
        } else {
            return false; // Формат некорректен
        }
    }
    private void sendInvalidFormatResponse(long chatId) {
        String responseText = "Извините, сообщение не соответствует ожидаемому формату.";
        telegramBot.execute(new SendMessage(chatId, responseText));
    }
}
