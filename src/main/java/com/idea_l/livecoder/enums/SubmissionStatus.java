package com.idea_l.livecoder.enums;

import lombok.Getter;

@Getter
public enum SubmissionStatus {
    CORRECT("정답"),
    WRONG("틀림"),
    COMPILE_ERROR("컴파일 에러"),
    RUNTIME_ERROR("런타임 에러"),
    TIME_LIMIT_EXCEEDED("시간 초과");

    private final String value;

    SubmissionStatus(String value) {
        this.value = value;
    }

}