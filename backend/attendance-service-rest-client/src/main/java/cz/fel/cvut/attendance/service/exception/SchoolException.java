package cz.fel.cvut.attendance.service.exception;

import org.springframework.http.HttpStatus;

public class SchoolException extends RuntimeException {

    private final HttpStatus status;

    public SchoolException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

}
