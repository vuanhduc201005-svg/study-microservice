package com.dducwsjvbe.notification_service.event;

import com.dducwsjvbe.notification_service.service.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;


@Slf4j(topic = "Event-Consumer")
@Component
@RequiredArgsConstructor
public class EventConsumer {
    private final MailService mailService;

    @RetryableTopic(
            attempts = "4", //3 topic retry+1 topic dlq
            backOff = @BackOff(delay = 1000, multiplier = 2), //lần đầu retry sau 1s và nhân 2 lên cho các lần sau
            autoCreateTopics = "true", //từ động retry
            dltStrategy = DltStrategy.FAIL_ON_ERROR, //fail và thêm vào dlq sẽ không retry tiếp
            include = {RuntimeException.class} //chỉ retry khi gặp những lỗi sau
    )
    @KafkaListener(topics = "email", groupId = "email-group", concurrency = "3")
    public void sendEmail(String message) throws MessagingException, UnsupportedEncodingException {
        log.info("sending mail ...");
        String[] arr = message.split(",");
        String emailTo = arr[0].substring(arr[0].indexOf('=') + 1);
        String subject = arr[1].substring(arr[1].indexOf('=') + 1);
        String content = arr[2].substring(arr[2].indexOf('=') + 1);
//        String secretCode = arr[3].substring(arr[3].indexOf('=') + 1);
        mailService.sendMail(emailTo, subject, content, null);
        log.info("send mail successfully,emailTo={}", emailTo);
    }

    @DltHandler
    void processDltMessage(@Payload String message) {
        log.info("DLT receive message: {}", message);
    }

}
/*
lưu ý các nội dụng không được chứa dấu phẩy nếu ko sẽ bị tách
 */