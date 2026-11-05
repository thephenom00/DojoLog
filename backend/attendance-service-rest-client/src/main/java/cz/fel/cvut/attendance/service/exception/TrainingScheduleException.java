package cz.fel.cvut.attendance.service.exception;

import org.springframework.http.HttpStatus;

public class TrainingScheduleException extends RuntimeException {
    private final HttpStatus status;

    public TrainingScheduleException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
