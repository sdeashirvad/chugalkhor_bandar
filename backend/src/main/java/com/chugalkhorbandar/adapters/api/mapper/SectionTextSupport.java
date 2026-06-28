package com.chugalkhorbandar.adapters.api.mapper;

import com.chugalkhorbandar.application.query.TextSectionSupport;
import java.util.List;

final class SectionTextSupport {

    private SectionTextSupport() {}

    static List<String> parseListItems(String sectionText) {
        return TextSectionSupport.parseListItems(sectionText);
    }

    static String extractSpecies(java.util.Map<String, String> sections) {
        return TextSectionSupport.extractSpecies(sections);
    }

    static List<String> extractPublicFacts(java.util.Map<String, String> sections) {
        return TextSectionSupport.extractPublicFacts(sections);
    }

    static java.util.Map<String, String> publicSections(java.util.Map<String, String> sections) {
        return TextSectionSupport.publicSections(sections);
    }
}
