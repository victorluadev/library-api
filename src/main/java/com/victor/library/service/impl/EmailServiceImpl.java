package com.victor.library.service.impl;

import com.victor.library.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${application.mail.default.remetent}")
    private String remetent;

    private JavaMailSender javaMailSender;

    @Override
    public void sendMails(List<Object> mailsList, String message) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(remetent);
        mailMessage.setSubject("Livro com empréstimo atrasado");
        mailMessage.setText(message);
        mailMessage.setTo(mailsList.toArray(new String[mailsList.size()]));

        javaMailSender.send(mailMessage);
    }
}
