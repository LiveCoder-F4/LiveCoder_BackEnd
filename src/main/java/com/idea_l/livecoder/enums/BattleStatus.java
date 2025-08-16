package com.idea_l.livecoder.enums;

import lombok.Getter;

@Getter
public enum BattleStatus {
    WAITING("waiting"),
    IN_PROGRESS("in_progress"),
    COMPLETED("completed"),
    ABANDONED("abandoned");

    private final String value;

    BattleStatus(String value) {
        this.value = value;
    }

}