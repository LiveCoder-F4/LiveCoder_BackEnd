package com.idea_l.livecoder.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RequestStatusConverter implements AttributeConverter<RequestStatus, String> {

    @Override
    public String convertToDatabaseColumn(RequestStatus status) {
        if (status == null) {
            return null;
        }
        return status.getValue();
    }

    @Override
    public RequestStatus convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }
        for (RequestStatus status : RequestStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown RequestStatus value: " + value);
    }
}
