package com.idea_l.livecoder.common;

import lombok.Getter;

@Getter
public enum CollabVisibility {
    PUBLIC("public"),
    PRIVATE("private");

    private final String value;

    CollabVisibility(String value) {
        this.value = value;
    }

}
