package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.MemoryInboxItemEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoryInboxItemJpaRepository extends JpaRepository<MemoryInboxItemEntity, String> {

    List<MemoryInboxItemEntity> findByOwnerCharacterIdOrderByCreatedAtDesc(String ownerCharacterId);

    Optional<MemoryInboxItemEntity> findByOwnerCharacterIdAndSourceAndSourceId(
            String ownerCharacterId, String source, String sourceId);

    List<MemoryInboxItemEntity> findAllByOrderByCreatedAtDesc();
}
