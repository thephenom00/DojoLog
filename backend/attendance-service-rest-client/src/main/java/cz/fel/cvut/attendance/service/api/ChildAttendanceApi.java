package cz.fel.cvut.attendance.service.api;

import cz.fel.cvut.attendance.service.model.ChildAttendanceDto;
import cz.fel.cvut.attendance.service.model.trainer.ParentContactDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/child-attendance")
public interface ChildAttendanceApi {
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}/mark-present")
    ResponseEntity<ChildAttendanceDto> markPresent(@PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}/mark-absent")
    ResponseEntity<ChildAttendanceDto> markAbsent(@PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/parent-contact")
    ResponseEntity<ParentContactDto> getParentContact(@PathVariable Long id);
}
