package com.chugalkhorbandar.bootstrap.document;

import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class BootstrapDocumentRepository {

    private final Map<String, BootstrapDocument> byId = new HashMap<>();
    private final List<BootstrapDocument> all = new ArrayList<>();

    public void store(BootstrapDocument document) {
        byId.put(document.metadata().id(), document);
        all.add(document);
    }

    public void storeAll(List<BootstrapDocument> documents) {
        documents.forEach(this::store);
    }

    public Optional<BootstrapDocument> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    public List<BootstrapDocument> findByType(DocumentType type) {
        return all.stream().filter(document -> document.documentType() == type).toList();
    }

    public List<BootstrapDocument> findAll() {
        return List.copyOf(all);
    }

    public int countByType(DocumentType type) {
        return (int) all.stream().filter(document -> document.documentType() == type).count();
    }

    public int countAll() {
        return all.size();
    }
}
