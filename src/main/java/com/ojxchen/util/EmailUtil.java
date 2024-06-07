package com.ojxchen.util;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {

//    public JavaMailSenderImpl JavaMailSender() {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost("smtp.qq.com");
//        mailSender.setUsername("1758754717@qq.com");
//        mailSender.setPassword("sokfmheipdvqdiad");
//        return mailSender;
//    }

    public JavaMailSenderImpl JavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.qq.com");
        mailSender.setUsername("1546087412@qq.com");
        mailSender.setPassword("juluvpmtotvkjbbc");
        return mailSender;
    }

    private final String  username = "1546087412@qq.com";

    public void sendEmail(String email, String code) {

        JavaMailSenderImpl javaMailSender = JavaMailSender();

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom(username);

        simpleMailMessage.setTo(email);

        simpleMailMessage.setSubject("验证码，有效期为5分钟，请尽快使用！");

        simpleMailMessage.setText(code);

        javaMailSender.send(simpleMailMessage);
    }
}