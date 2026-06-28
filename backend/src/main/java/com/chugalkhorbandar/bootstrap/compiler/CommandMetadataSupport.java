package com.chugalkhorbandar.bootstrap.compiler;

import com.chugalkhorbandar.bootstrap.typed.spec.BootstrapTypedSpec;
import java.util.LinkedHashMap;
import java.util.Map;

final class CommandMetadataSupport {

    private CommandMetadataSupport() {}

    static Map<String, String> metadata(BootstrapTypedSpec spec) {
        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("status", spec.status());
        metadata.put("version", spec.version());
        metadata.put("documentType", spec.documentType().name());
        return Map.copyOf(metadata);
    }

    static Map<String, String> sections(Map<String, String> sections) {
        Map<String, String> filtered = new LinkedHashMap<>();
        sections.forEach((key, value) -> {
            if (value != null) {
                filtered.put(key, value);
            }
        });
        return Map.copyOf(filtered);
    }
}
