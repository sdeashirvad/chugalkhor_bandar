package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.application.notification.Notification;
import com.chugalkhorbandar.domain.notification.ports.NotificationRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!postgres-dev")
public class InMemoryNotificationRepository implements NotificationRepository {

    private final InMemoryNotificationStore store;

    public InMemoryNotificationRepository(InMemoryNotificationStore store) {
        this.store = store;
    }

    @Override
    public List<Notification> findByRecipientCharacterId(String recipientCharacterId) {
        return store.findByRecipientCharacterId(recipientCharacterId);
    }

    @Override
    public Optional<Notification> findById(String id) {
        return store.findById(id);
    }

    @Override
    public Notification save(Notification notification) {
        return store.save(notification);
    }

    @Override
    public long countUnreadByRecipientCharacterId(String recipientCharacterId) {
        return store.countUnreadByRecipientCharacterId(recipientCharacterId);
    }
}
