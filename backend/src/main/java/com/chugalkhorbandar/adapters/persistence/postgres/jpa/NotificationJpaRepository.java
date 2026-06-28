package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.NotificationEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, String> {

    List<NotificationEntity> findByRecipientCharacterIdOrderByCreatedAtDesc(String recipientCharacterId);

    long countByRecipientCharacterIdAndStatus(String recipientCharacterId, String status);
}
