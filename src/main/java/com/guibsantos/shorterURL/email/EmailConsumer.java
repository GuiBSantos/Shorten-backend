package com.guibsantos.shorterURL.email;

import com.guibsantos.shorterURL.config.RabbitMQConfig;
import com.guibsantos.shorterURL.controller.dto.EmailDto;
import com.guibsantos.shorterURL.service.GmailApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private final GmailApiService gmailApiService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void listen(@Payload EmailDto emailDto) {
        try {
            gmailApiService.sendEmail(emailDto.to(), emailDto.subject(), emailDto.body(), emailDto.emailType());
        } catch (Exception e) {
            System.err.println("Erro ao processar e-mail da fila: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
