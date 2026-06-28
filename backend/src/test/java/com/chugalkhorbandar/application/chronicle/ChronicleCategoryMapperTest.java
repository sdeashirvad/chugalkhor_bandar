package com.chugalkhorbandar.application.chronicle;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ChronicleCategoryMapperTest {

    @Test
    void mapsKnownCandidateTypes() {
        assertThat(ChronicleCategoryMapper.fromCandidateType("PROMISE")).isEqualTo(ChronicleCategory.PROMISE);
        assertThat(ChronicleCategoryMapper.fromCandidateType("STORY_SEED")).isEqualTo(ChronicleCategory.STORY);
        assertThat(ChronicleCategoryMapper.fromCandidateType("PREFERENCE")).isEqualTo(ChronicleCategory.PREFERENCE);
    }

    @Test
    void mapsUnknownToCustom() {
        assertThat(ChronicleCategoryMapper.fromCandidateType("UNKNOWN")).isEqualTo(ChronicleCategory.CUSTOM);
    }
}
