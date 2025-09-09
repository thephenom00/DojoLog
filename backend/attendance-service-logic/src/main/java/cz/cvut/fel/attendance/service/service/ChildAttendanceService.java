package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.mappers.ChildAttendanceMapper;
import cz.cvut.fel.attendance.service.model.ChildTrainingAttendance;
import cz.cvut.fel.attendance.service.repository.ChildAttendanceRepository;
import cz.fel.cvut.attendance.service.exception.AttendanceException;
import cz.fel.cvut.attendance.service.model.ChildAttendanceDto;
import cz.fel.cvut.attendance.service.model.trainer.ParentContactDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChildAttendanceService {

    private final ChildAttendanceRepository childAttendanceRepository;
    private final ChildAttendanceMapper childAttendanceMapper;

    public ChildAttendanceDto markPresent(Long id) {
        ChildTrainingAttendance childTrainingAttendance = childAttendanceRepository.findById(id)
                .orElseThrow(() -> new AttendanceException("Attendance with ID " + id + " not found", HttpStatus.NOT_FOUND));

        childTrainingAttendance.setPresent(true);

        childAttendanceRepository.save(childTrainingAttendance);

        return childAttendanceMapper.toDto(childTrainingAttendance);
    }

    public ChildAttendanceDto markAbsent(Long id) {
        ChildTrainingAttendance childTrainingAttendance = childAttendanceRepository.findById(id)
                .orElseThrow(() -> new AttendanceException("Attendance with ID " + id + " not found", HttpStatus.NOT_FOUND));

        childTrainingAttendance.setPresent(false);

        childAttendanceRepository.save(childTrainingAttendance);

        return childAttendanceMapper.toDto(childTrainingAttendance);
    }

    public ParentContactDto getParentContact(Long id) {
        ChildTrainingAttendance childTrainingAttendance = childAttendanceRepository.findById(id)
                .orElseThrow(() -> new AttendanceException("Attendance with ID " + id + " not found", HttpStatus.NOT_FOUND));

        String firstName = childTrainingAttendance.getChild().getParent().getFirstName();
        String lastName = childTrainingAttendance.getChild().getParent().getLastName();
        String email = childTrainingAttendance.getChild().getParent().getEmail();
        String phoneNumber = childTrainingAttendance.getChild().getParent().getPhoneNumber();

        return new ParentContactDto(firstName, lastName, email, phoneNumber);
    }
}
