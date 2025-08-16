package com.idea_l.livecoder.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
    FRIEND_REQUEST("friend_request"),
    BATTLE_INVITE("battle_invite"),
    COMMENT("comment"),
    LIKE("like"),
    SYSTEM("system");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

}