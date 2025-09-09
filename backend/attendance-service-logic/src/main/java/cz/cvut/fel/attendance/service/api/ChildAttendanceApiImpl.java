package cz.cvut.fel.attendance.service.api;

import cz.cvut.fel.attendance.service.service.ChildAttendanceService;
import cz.fel.cvut.attendance.service.api.ChildAttendanceApi;
import cz.fel.cvut.attendance.service.model.ChildAttendanceDto;
import cz.fel.cvut.attendance.service.model.trainer.ParentContactDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChildAttendanceApiImpl implements ChildAttendanceApi {

    private final ChildAttendanceService childAttendanceService;

    @Override
    public ResponseEntity<ChildAttendanceDto> markPresent(Long id) {
        return ResponseEntity.ok(childAttendanceService.markPresent(id));
    }

    @Override
    public ResponseEntity<ChildAttendanceDto> markAbsent(Long id) {
        return ResponseEntity.ok(childAttendanceService.markAbsent(id));
    }

    @Override
    public ResponseEntity<ParentContactDto> getParentContact(Long id) {
        return ResponseEntity.ok(childAttendanceService.getParentContact(id));
    }
}
