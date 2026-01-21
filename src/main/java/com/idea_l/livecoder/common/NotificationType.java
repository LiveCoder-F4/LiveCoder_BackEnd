package com.idea_l.livecoder.common;

import lombok.Getter;

@Getter
public enum NotificationType {
    FRIEND_REQUEST("friend_request"),
    COMMENT("comment"),
    LIKE("like"),
    SYSTEM("system");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

}
