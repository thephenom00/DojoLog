package cz.fel.cvut.attendance.service.api;

import cz.fel.cvut.attendance.service.model.ChildAttendanceDto;
import cz.fel.cvut.attendance.service.model.trainer.RegisteredChildInEventDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/child-event-attendance")
public interface ChildEventAttendanceApi {
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}/toggle-attendance")
    ResponseEntity<RegisteredChildInEventDto> toggleAttendance(@PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}/toggle-payment")
    ResponseEntity<RegisteredChildInEventDto> togglePayment(@PathVariable Long id);
}
