package com.idea_l.livecoder.enums;

import lombok.Getter;

@Getter
public enum CollabRole {
    OWNER("owner"),
    MEMBER("member");

    private final String value;

    CollabRole(String value) {
        this.value = value;
    }

}
