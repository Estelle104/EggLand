package com.app.eggland.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.eggland.model.Notification;
import com.app.eggland.repository.NotificationRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification creer(String type) {
        return creer(type, genererMessage(type));
    }

    public Notification creer(String type, String message) {
        Notification notification = Notification.builder()
                .type(type)
                .message(message)
                .dateCreation(LocalDateTime.now())
                .lu(false)
                .build();
        return notificationRepository.save(notification);
    }

    public List<Notification> listerToutes() {
        return notificationRepository.findAllByOrderByDateCreationDesc();
    }

    public List<Notification> listerNonLues() {
        return notificationRepository.findByLuFalseOrderByDateCreationDesc();
    }

    public long compterNonLues() {
        return notificationRepository.countByLuFalse();
    }

    @Transactional
    public void marquerCommeLu(Integer id) {
        notificationRepository.marquerCommeLu(id);
    }

    @Transactional
    public void toutMarquerCommeLu() {
        List<Notification> nonLues = notificationRepository.findByLuFalseOrderByDateCreationDesc();
        for (Notification n : nonLues) {
            n.setLu(true);
            notificationRepository.save(n);
        }
    }

    private String genererMessage(String type) {
        switch (type) {
            case "STOCK_FAIBLE":
                return "Le stock d'une nourriture est en dessous du seuil d'alerte";
            default:
                return "Notification : " + type;
        }
    }
}
