package com.idea_l.livecoder.collab;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CollabApiResponse<T> {
    private boolean success;
    private T data;
    private String message;

    public static <T> CollabApiResponse<T> ok(T data) {
        return new CollabApiResponse<>(true, data, "OK");
    }

    public static <T> CollabApiResponse<T> ok(T data, String message) {
        return new CollabApiResponse<>(true, data, message);
    }
}
