package com.clerodri.web.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Setter
@Getter
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private Map<String, String> details;

    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, Map<String, String> details) {
        this.timestamp=timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
    }

}
