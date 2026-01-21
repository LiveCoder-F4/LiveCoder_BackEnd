package com.idea_l.livecoder.common;

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
