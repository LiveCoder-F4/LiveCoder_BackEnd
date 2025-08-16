package com.idea_l.livecoder.enums;

import lombok.Getter;

@Getter
public enum RequestStatus {
    PENDING("pending"),
    ACCEPTED("accepted"),
    DECLINED("declined");

    private final String value;

    RequestStatus(String value) {
        this.value = value;
    }

}