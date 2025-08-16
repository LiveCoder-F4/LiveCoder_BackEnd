package com.idea_l.livecoder.enums;

import lombok.Getter;

@Getter
public enum Language {
    CPP("C++"),
    JAVA("JAVA"),
    PYTHON("python");

    private final String value;

    Language(String value) {
        this.value = value;
    }

}
