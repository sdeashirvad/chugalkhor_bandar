package com.chugalkhorbandar.domain.chronicle.ports;

import com.chugalkhorbandar.application.chronicle.Chronicle;
import com.chugalkhorbandar.application.chronicle.ChronicleCategory;
import com.chugalkhorbandar.application.chronicle.ChronicleVisibility;
import java.util.List;
import java.util.Optional;

public interface ChronicleRepository {

    Chronicle save(Chronicle chronicle);

    Optional<Chronicle> findById(String id);

    Optional<Chronicle> findLatestByCandidateId(String candidateId);

    int countByCandidateId(String candidateId);

    List<Chronicle> findAllOrderByCreatedAtDesc();

    List<Chronicle> findByCategoryOrderByCreatedAtDesc(ChronicleCategory category);

    List<Chronicle> findByVisibilityOrderByCreatedAtDesc(ChronicleVisibility visibility);
}
