package com.openticket.admin.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Role role) {
        return role != null ? role.getCode() : null;
    }

    @Override
    public Role convertToEntityAttribute(Integer dbData) {
        return dbData != null ? Role.fromCode(dbData) : null;
    }
}
