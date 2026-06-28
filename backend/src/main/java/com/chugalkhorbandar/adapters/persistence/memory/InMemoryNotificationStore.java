package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.application.notification.Notification;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryNotificationStore {

    private final ConcurrentHashMap<String, Notification> notificationsById = new ConcurrentHashMap<>();

    public List<Notification> findByRecipientCharacterId(String recipientCharacterId) {
        return notificationsById.values().stream()
                .filter(notification -> recipientCharacterId.equals(notification.recipientCharacterId()))
                .sorted(Comparator.comparing(Notification::createdAt).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Optional<Notification> findById(String id) {
        return Optional.ofNullable(notificationsById.get(id));
    }

    public Notification save(Notification notification) {
        notificationsById.put(notification.id(), notification);
        return notification;
    }

    public long countUnreadByRecipientCharacterId(String recipientCharacterId) {
        return notificationsById.values().stream()
                .filter(notification -> recipientCharacterId.equals(notification.recipientCharacterId()))
                .filter(notification -> notification.status() == com.chugalkhorbandar.application.notification.NotificationStatus.DELIVERED)
                .count();
    }
}
