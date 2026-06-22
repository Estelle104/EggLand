package com.app.eggland.service;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    public void creer(String type) {
        //pour creer une notification exemple:stock_faible
        System.out.println("Notification créée : " + type);
    }
}
