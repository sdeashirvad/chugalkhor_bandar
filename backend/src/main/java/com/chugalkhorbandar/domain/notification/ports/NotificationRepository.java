package com.chugalkhorbandar.domain.notification.ports;

import com.chugalkhorbandar.application.notification.Notification;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository {

    List<Notification> findByRecipientCharacterId(String recipientCharacterId);

    Optional<Notification> findById(String id);

    Notification save(Notification notification);

    long countUnreadByRecipientCharacterId(String recipientCharacterId);
}
