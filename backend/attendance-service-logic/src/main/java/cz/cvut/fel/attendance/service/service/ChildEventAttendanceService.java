package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.mappers.ChildEventAttendanceMapper;
import cz.cvut.fel.attendance.service.model.ChildEventAttendance;
import cz.cvut.fel.attendance.service.repository.ChildEventAttendanceRepository;
import cz.fel.cvut.attendance.service.exception.AttendanceException;
import cz.fel.cvut.attendance.service.model.trainer.RegisteredChildInEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChildEventAttendanceService {
    private final ChildEventAttendanceRepository childEventAttendanceRepository;
    private final ChildEventAttendanceMapper childEventAttendanceMapper;

    public RegisteredChildInEventDto toggleAttendance(Long id) {
        ChildEventAttendance childEventAttendance = childEventAttendanceRepository.findById(id)
                .orElseThrow(() -> new AttendanceException("Child Event Attendance with ID: " + id + " not found.", HttpStatus.NOT_FOUND));

        childEventAttendance.setPresent(!childEventAttendance.isPresent());
        childEventAttendance = childEventAttendanceRepository.save(childEventAttendance);

        return childEventAttendanceMapper.toDto(childEventAttendance);
    }

    public RegisteredChildInEventDto togglePayment(Long id) {
        ChildEventAttendance childEventAttendance = childEventAttendanceRepository.findById(id)
                .orElseThrow(() -> new AttendanceException("Child Event Attendance with ID: " + id + " not found.", HttpStatus.NOT_FOUND));

        childEventAttendance.setPaymentReceived(!childEventAttendance.isPaymentReceived());
        childEventAttendance = childEventAttendanceRepository.save(childEventAttendance);

        return childEventAttendanceMapper.toDto(childEventAttendance);
    }
}
