package cz.cvut.fel.attendance.service.api;

import cz.cvut.fel.attendance.service.model.ChildEventAttendance;
import cz.cvut.fel.attendance.service.service.ChildEventAttendanceService;
import cz.fel.cvut.attendance.service.api.ChildEventAttendanceApi;
import cz.fel.cvut.attendance.service.model.trainer.RegisteredChildInEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChildEventAttendanceApiImpl implements ChildEventAttendanceApi {

    private final ChildEventAttendanceService childEventAttendanceService;

    @Override
    public ResponseEntity<RegisteredChildInEventDto> toggleAttendance(Long id) {
        return ResponseEntity.ok(childEventAttendanceService.toggleAttendance(id));
    }

    @Override
    public ResponseEntity<RegisteredChildInEventDto> togglePayment(Long id) {
        return ResponseEntity.ok(childEventAttendanceService.togglePayment(id));
    }
}
