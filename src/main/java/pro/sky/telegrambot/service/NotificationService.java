package pro.sky.telegrambot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service
public class NotificationService {
    private final NotificationTaskRepository notificationTaskRepository;

    @Autowired
    public NotificationService(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    public NotificationTask parseAndSaveReminder(String message,Long chatId) {
        Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
        Matcher matcher = pattern.matcher(message);

        if (matcher.matches()) {
            String dateTimeStr = matcher.group(1);
            String text = matcher.group(3);

            LocalDateTime executionTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

            NotificationTask notificationTask = new NotificationTask();
            notificationTask.setChatId(chatId);
            notificationTask.setMessageText(text);
            notificationTask.setScheduledTime(executionTime);

            notificationTaskRepository.save(notificationTask);
            return notificationTask;
        } else {
            throw new IllegalArgumentException("Неверный формат сообщения");
        }
    }
}
