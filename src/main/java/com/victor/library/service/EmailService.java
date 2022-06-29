package com.victor.library.service;

import java.util.List;

public interface EmailService {
    void sendMails(List<String> mailsList, String message);
}
