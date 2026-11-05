package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.mappers.ChildMapper;
import cz.cvut.fel.attendance.service.mappers.ChildUpcomingTrainingUnitMapper;
import cz.cvut.fel.attendance.service.model.*;
import cz.cvut.fel.attendance.service.repository.*;
import cz.fel.cvut.attendance.service.exception.ChildException;
import cz.fel.cvut.attendance.service.exception.EventException;
import cz.fel.cvut.attendance.service.exception.ParentException;
import cz.fel.cvut.attendance.service.exception.TrainingException;
import cz.fel.cvut.attendance.service.exception.UserException;
import cz.fel.cvut.attendance.service.model.ChildDto;
import cz.fel.cvut.attendance.service.model.parent.ChildEventStatusDto;
import cz.fel.cvut.attendance.service.model.parent.ChildUpcomingTrainingUnitDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParentService {

    private final TrainingUnitRepository trainingUnitRepository;
    private final ChildRepository childRepository;
    private final TrainingRepository trainingRepository;

    private final ChildUpcomingTrainingUnitMapper childUpcomingTrainingUnitMapper;
    private final UserRepository userRepository;
    private final ChildMapper childMapper;
    private final EventRepository eventRepository;

    private final MailService mailService;
    private final AppSettingsRepository appSettingsRepository;

    @Value("${admin.email}")
    private String adminEmail;

    public List<ChildUpcomingTrainingUnitDto> getUpcomingTrainingUnits(String email) {
        List<TrainingUnit> trainingUnits = trainingUnitRepository.findUpcomingUnitsByParentEmail(email);
        return trainingUnits.stream()
                .map(unit -> childUpcomingTrainingUnitMapper.toDtoWithChildren(unit, email))
                .toList();
    }

    public List<ChildEventStatusDto> getChildrenEventStatus(String email, Long id) {
        Parent parent = (Parent) userRepository.findByEmail(email)
                .orElseThrow(() -> new ParentException("Parent not found", HttpStatus.NOT_FOUND));

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventException("Event with ID: " + id + " not found.", HttpStatus.NOT_FOUND));

        List<ChildEventStatusDto> results = new ArrayList<>();

        for (Child child : parent.getChildren()) {
            boolean isRegistered = event.getChildEventAttendances().stream()
                            .anyMatch(a->a.getChild().equals(child));

            results.add(new ChildEventStatusDto(
                    child.getId(),
                    child.getFirstName(),
                    child.getLastName(),
                    isRegistered
                    ));
        }
        return results;
    }

    public List<ChildDto> getChildren(String email) {
        Parent parent = (Parent) userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("Parent not found.", HttpStatus.NOT_FOUND));

        List<Child> children = parent.getChildren().stream()
                .filter(Child::isActive)
                .toList();

        return childMapper.toDtoList(children);
    }

    @Transactional
    public ChildDto requestTraining(String email, Long trainingId, Long childId) {
        AppSettings appSettings = appSettingsRepository.findById(1L).orElseThrow();
        if (!appSettings.isEnrollmentOpen()) {
            throw new ChildException("Enrollment is currently closed", HttpStatus.FORBIDDEN);
        }
        Parent parent = (Parent) userRepository.findByEmail(email).orElseThrow(() -> new ParentException("User with ID: " + email + " not found.", HttpStatus.NOT_FOUND));
        Child child = childRepository.findById(childId).orElseThrow(() -> new ChildException("Child with ID: " + childId + " not found.", HttpStatus.NOT_FOUND));
        Training training = trainingRepository.findById(trainingId).orElseThrow(()-> new TrainingException("Training with ID: " + trainingId + " not found.", HttpStatus.NOT_FOUND));

        if (!parent.getChildren().contains(child)) {
            throw new ChildException(
                    "Child with ID " + childId + " does not belong to parent: " + email,
                    HttpStatus.FORBIDDEN
            );
        }
        if (child.getRequestedTrainingId() != null) {
            throw new TrainingException("Child already has a pending training request.", HttpStatus.CONFLICT);
        }
        if ((training.getCapacity() - 1) < 0) {
            throw new TrainingException("Child cannot be added to training, because it has full capacity.", HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
        }
        training.setCapacity(training.getCapacity() - 1);

        child.setRequestedTrainingId(trainingId);
        childRepository.save(child);
        return childMapper.toDto(child);
    }

    @Transactional
    public ChildDto createChild(String email, ChildDto childDto) {
        Parent parent = (Parent) userRepository.findByEmail(email)
                .orElseThrow(() -> new ParentException("User with ID: " + email + " not found.", HttpStatus.NOT_FOUND));

        if (childRepository.existsByBirthNumber(childDto.birthNumber())) {
            throw new ChildException("CHILD_ALREADY_EXISTS_BY_BIRTH_NUMBER",
                    HttpStatus.CONFLICT);
        }

        if (childRepository.existsByFirstNameAndLastNameAndParentAndActiveTrue(childDto.firstName(), childDto.lastName(), parent)) {
            throw new ChildException("CHILD_ALREADY_EXISTS_BY_NAME",
                    HttpStatus.CONFLICT);
        }

        long activeChildrenCount = parent.getChildren().stream().filter(Child::isActive).count();
        if (activeChildrenCount >= 5) {
            throw new ChildException("MAX_CHILDREN_REACHED",
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Child child = childMapper.toEntity(childDto);

        Training training = trainingRepository.findById(childDto.requestedTrainingId()).orElseThrow(()-> new TrainingException("Training with ID: " + childDto.requestedTrainingId() + " not found.", HttpStatus.NOT_FOUND));

        if ((training.getCapacity() - 1) < 0) {
            throw new TrainingException("Child cannot be added to training, because it has full capacity.", HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
        }

        training.setCapacity(training.getCapacity() - 1);
        child.setParent(parent);

        mailService.sendChildRegistrationMail(parent, childDto, adminEmail);

        trainingRepository.save(training);
        childRepository.save(child);
        return childMapper.toDto(child);
    }
}
