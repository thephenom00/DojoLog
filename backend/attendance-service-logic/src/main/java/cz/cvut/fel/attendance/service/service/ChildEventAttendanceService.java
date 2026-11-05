package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.mappers.ChildEventAttendanceMapper;
import cz.cvut.fel.attendance.service.model.ChildEventAttendance;
import cz.cvut.fel.attendance.service.model.Event;
import cz.cvut.fel.attendance.service.repository.ChildEventAttendanceRepository;
import cz.fel.cvut.attendance.service.exception.AttendanceException;
import cz.fel.cvut.attendance.service.model.trainer.RegisteredChildInEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChildEventAttendanceService {
    private final ChildEventAttendanceRepository childEventAttendanceRepository;
    private final ChildEventAttendanceMapper childEventAttendanceMapper;

    @Transactional
    public RegisteredChildInEventDto toggleAttendance(Long id) {
        ChildEventAttendance childEventAttendance = childEventAttendanceRepository.findById(id)
                .orElseThrow(() -> new AttendanceException("Child Event Attendance with ID: " + id + " not found.", HttpStatus.NOT_FOUND));

        checkTrainerOrAdminAccess(childEventAttendance.getEvent());

        childEventAttendance.setPresent(!childEventAttendance.isPresent());
        childEventAttendance = childEventAttendanceRepository.save(childEventAttendance);

        return childEventAttendanceMapper.toDto(childEventAttendance);
    }

    @Transactional
    public RegisteredChildInEventDto togglePayment(Long id) {
        ChildEventAttendance childEventAttendance = childEventAttendanceRepository.findById(id)
                .orElseThrow(() -> new AttendanceException("Child Event Attendance with ID: " + id + " not found.", HttpStatus.NOT_FOUND));

        checkTrainerOrAdminAccess(childEventAttendance.getEvent());

        childEventAttendance.setPaymentReceived(!childEventAttendance.isPaymentReceived());
        childEventAttendance = childEventAttendanceRepository.save(childEventAttendance);

        return childEventAttendanceMapper.toDto(childEventAttendance);
    }

    private void checkTrainerOrAdminAccess(Event event) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            String email = auth != null ? auth.getName() : null;
            boolean isAssignedTrainer = event != null && event.getTrainers().stream()
                    .anyMatch(t -> t.getEmail() != null && t.getEmail().equalsIgnoreCase(email));
            if (!isAssignedTrainer) {
                throw new AttendanceException("Access denied: only assigned trainers or admin can modify event attendance.", HttpStatus.FORBIDDEN);
            }
        }
    }
}
