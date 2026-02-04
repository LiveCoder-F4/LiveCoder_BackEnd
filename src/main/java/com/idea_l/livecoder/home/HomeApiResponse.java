package com.idea_l.livecoder.home;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HomeApiResponse<T> {
    private boolean success;
    private T data;
    private String message;

    public static <T> HomeApiResponse<T> ok(T data) {
        return new HomeApiResponse<>(true, data, "OK");
    }

    public static <T> HomeApiResponse<T> ok(T data, String message) {
        return new HomeApiResponse<>(true, data, message);
    }
}
