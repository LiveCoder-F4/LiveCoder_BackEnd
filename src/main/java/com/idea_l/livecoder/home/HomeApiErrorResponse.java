package com.idea_l.livecoder.home;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HomeApiErrorResponse {
    private boolean success;
    private ErrorBody error;

    @Getter
    @AllArgsConstructor
    public static class ErrorBody {
        private int code;
        private String message;
    }

    public static HomeApiErrorResponse of(int code, String message) {
        return new HomeApiErrorResponse(false, new ErrorBody(code, message));
    }
}
