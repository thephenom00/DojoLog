package cz.fel.cvut.attendance.service.exception;

import org.springframework.http.HttpStatus;

public class ChildException extends RuntimeException {

    private final HttpStatus status;

    public ChildException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
