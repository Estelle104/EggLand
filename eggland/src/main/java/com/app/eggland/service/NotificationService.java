package com.app.eggland.service;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    public void creer(String type) {
        //pour creer une notification
        System.out.println("Notification créée : " + type);
    }
}
