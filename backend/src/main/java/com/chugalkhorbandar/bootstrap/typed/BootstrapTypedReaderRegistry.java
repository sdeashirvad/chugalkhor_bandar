package com.chugalkhorbandar.bootstrap.typed;

import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import com.chugalkhorbandar.bootstrap.typed.reader.BootstrapTypedReader;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class BootstrapTypedReaderRegistry {

    private final Map<DocumentType, BootstrapTypedReader<?>> readersByType;

    public BootstrapTypedReaderRegistry(List<BootstrapTypedReader<?>> readers) {
        Map<DocumentType, BootstrapTypedReader<?>> map = new EnumMap<>(DocumentType.class);
        for (BootstrapTypedReader<?> reader : readers) {
            for (DocumentType type : DocumentType.values()) {
                if (reader.supports(type)) {
                    map.put(type, reader);
                }
            }
        }
        this.readersByType = Map.copyOf(map);
    }

    public boolean isSupported(DocumentType type) {
        return readersByType.containsKey(type);
    }

    public Optional<BootstrapTypedReader<?>> findReader(DocumentType type) {
        return Optional.ofNullable(readersByType.get(type));
    }

    @SuppressWarnings("unchecked")
    public <T> T read(BootstrapDocument document) {
        BootstrapTypedReader<?> reader = readersByType.get(document.documentType());
        if (reader == null) {
            throw new TypedReaderException(
                    "No typed reader registered for document type: " + document.documentType(),
                    document.metadata().id(),
                    document.sourcePath());
        }
        return (T) reader.read(document);
    }
}
