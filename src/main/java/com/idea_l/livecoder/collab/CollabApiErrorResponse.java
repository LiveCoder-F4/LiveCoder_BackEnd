package com.idea_l.livecoder.collab;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CollabApiErrorResponse {
    private boolean success;
    private ErrorBody error;

    @Getter
    @AllArgsConstructor
    public static class ErrorBody {
        private int code;
        private String message;
    }

    public static CollabApiErrorResponse of(int code, String message) {
        return new CollabApiErrorResponse(false, new ErrorBody(code, message));
    }
}
