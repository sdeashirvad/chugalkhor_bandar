package com.chugalkhorbandar.adapters.persistence.postgres.mapper;

import com.chugalkhorbandar.adapters.persistence.JsonSerialization;
import com.chugalkhorbandar.adapters.persistence.postgres.entity.OrganizationEntity;
import com.chugalkhorbandar.domain.world.runtime.RuntimeOrganization;

public final class OrganizationMapper {

    private OrganizationMapper() {}

    public static OrganizationEntity toEntity(RuntimeOrganization runtime) {
        OrganizationEntity entity = new OrganizationEntity();
        entity.setId(runtime.id());
        entity.setTitle(runtime.title());
        entity.setSections(JsonSerialization.toJson(runtime.sections()));
        entity.setRoles(JsonSerialization.toJson(runtime.roles()));
        return entity;
    }

    public static RuntimeOrganization toRuntime(OrganizationEntity entity) {
        return new RuntimeOrganization(
                entity.getId(),
                entity.getTitle(),
                JsonSerialization.toMap(entity.getSections()),
                JsonSerialization.toMap(entity.getRoles()));
    }
}
