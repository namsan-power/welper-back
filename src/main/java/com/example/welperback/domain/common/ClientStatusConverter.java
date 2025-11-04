package com.example.welperback.domain.common;

import com.example.welperback.domain.client.ClientStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ClientStatusConverter implements AttributeConverter<ClientStatus, String> {

    @Override
    public String convertToDatabaseColumn(ClientStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDatabaseValue();
    }

    @Override
    public ClientStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return ClientStatus.fromDatabaseValue(dbData);
    }
}
