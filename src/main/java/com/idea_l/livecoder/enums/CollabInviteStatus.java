package com.idea_l.livecoder.enums;

import lombok.Getter;

@Getter
public enum CollabInviteStatus {
    PENDING("pending"),
    ACCEPTED("accepted"),
    DECLINED("declined"),
    CANCELED("canceled");

    private final String value;

    CollabInviteStatus(String value) {
        this.value = value;
    }

}
