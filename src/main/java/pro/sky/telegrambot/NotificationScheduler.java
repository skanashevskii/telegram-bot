package pro.sky.telegrambot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Component
public class NotificationScheduler {
    private static final Logger logger = LoggerFactory.getLogger(NotificationScheduler.class);
    @Autowired
    private NotificationTaskRepository notificationTaskRepository;
    @Autowired
    private TelegramBot telegramBot;

    @Scheduled(cron = "0 0/1 * * * *")
    public void sendNotifications() {
        // Реализация метода
        // Получить текущее время без секунд
        LocalDateTime currentMinute = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        // Найдите записи в БД, у которых время совпадает с текущим минутным временем
        List<NotificationTask> tasksToNotify = notificationTaskRepository.findByScheduledTime(currentMinute);

        // Отправьте уведомления для найденных задач
        for (NotificationTask task : tasksToNotify) {
            long chatId = task.getChatId(); // Получите chatId из задачи

            String notificationText = task.getMessageText(); // Получите текст уведомления

            // Отправьте уведомление в чат
            telegramBot.execute(new SendMessage(chatId, notificationText));
        }
    }

}
