package com.idea_l.livecoder.home;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.idea_l.livecoder.home")
public class HomeExceptionHandler {
    //404: "존재하지"가 메시지에 포함되면 Not Found로 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<HomeApiErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        String msg = (e.getMessage() == null) ? "" : e.getMessage();

        if (msg.contains("존재하지") || msg.contains("not found")) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(HomeApiErrorResponse.of(404, msg));
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(HomeApiErrorResponse.of(400, msg));
    }
    //500: 서버 오류
    @ExceptionHandler(Exception.class)
    public ResponseEntity<HomeApiErrorResponse> handleServerError(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(HomeApiErrorResponse.of(500, "서버 오류"));
    }
}

