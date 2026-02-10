package com.idea_l.livecoder.collab;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.idea_l.livecoder.collab")
public class CollabExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CollabApiErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        String msg = (e.getMessage() == null) ? "" : e.getMessage();

        if (msg.contains("존재하지") || msg.contains("not found")) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(CollabApiErrorResponse.of(404, msg));
        }

        if (msg.contains("권한") || msg.contains("forbidden")) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(CollabApiErrorResponse.of(403, msg));
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CollabApiErrorResponse.of(400, msg));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CollabApiErrorResponse> handleServerError(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CollabApiErrorResponse.of(500, "서버 오류"));
    }
}
