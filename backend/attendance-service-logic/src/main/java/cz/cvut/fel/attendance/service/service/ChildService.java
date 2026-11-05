package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.mappers.ChildMapper;
import cz.cvut.fel.attendance.service.mappers.ParentMapper;
import cz.cvut.fel.attendance.service.model.Child;
import cz.cvut.fel.attendance.service.model.ChildTrainingAttendance;
import cz.cvut.fel.attendance.service.model.Parent;
import cz.cvut.fel.attendance.service.model.Training;
import cz.cvut.fel.attendance.service.repository.ChildRepository;
import cz.cvut.fel.attendance.service.repository.TrainingRepository;
import cz.cvut.fel.attendance.service.repository.UserRepository;
import cz.fel.cvut.attendance.service.exception.ChildException;
import cz.fel.cvut.attendance.service.exception.TrainingException;
import cz.fel.cvut.attendance.service.model.ChildDto;
import cz.fel.cvut.attendance.service.model.ParentDto;
import cz.fel.cvut.attendance.service.model.admin.ChildParentInfo;
import cz.fel.cvut.attendance.service.model.admin.RegistrationRequestDto;
import cz.fel.cvut.attendance.service.model.parent.ChildAttendanceDetailDto;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cz.cvut.fel.attendance.service.utils.HelpMethods.getDayName;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChildService {

    private final ChildRepository childRepository;
    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;

    private final ChildMapper childMapper;
    private final ParentMapper parentMapper;

    private final MailService mailService;

    public List<ChildAttendanceDetailDto> getChildAttendanceDetails(Long id) {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new ChildException("Child with ID " + id + " not found.", HttpStatus.NOT_FOUND));

        List<ChildTrainingAttendance> attendances = child.getTrainingAttendances().stream()
                .filter(ChildTrainingAttendance::isPresent).toList();

        List<ChildAttendanceDetailDto> childAttendanceDetails = new ArrayList<>();

        for (ChildTrainingAttendance attendance : attendances) {
            childAttendanceDetails.add(new ChildAttendanceDetailDto(
                    attendance.getTrainingUnit().getDate(),
                    attendance.getTrainingUnit().getTrainingSchedule().getTraining().getSchool().getName(),
                    attendance.getTrainingUnit().getDescription()
            ));
        }

        return childAttendanceDetails;
    }

    public List<RegistrationRequestDto> getUnassignedChildren() {

        List<Child> children = childRepository.findByRequestedTrainingIdIsNotNull();

        List<RegistrationRequestDto> registrationRequests = children.stream()
                .map(child -> {
                    Training training = trainingRepository.findById(child.getRequestedTrainingId())
                            .orElseThrow(() -> new TrainingException(
                                    "Training with ID " + child.getRequestedTrainingId() + " not found",
                                    HttpStatus.NOT_FOUND
                            ));

                    String dayOfWeek = training.getTrainingSchedules().stream()
                            .map(schedule -> getDayName(schedule.getDayOfWeek()))
                            .collect(Collectors.joining(", "));

                    return new RegistrationRequestDto(
                            child.getId(),
                            child.getFirstName(),
                            child.getLastName(),
                            child.getParent().getFirstName(),
                            child.getParent().getLastName(),
                            child.getParent().getEmail(),
                            child.getParent().getPhoneNumber(),
                            training.getSchool().getName(),
                            dayOfWeek,
                            training.getName(),
                            child.getBirthNumber(),
                            child.getDateOfBirth(),
                            child.getCity(),
                            child.getStreet(),
                            child.getZip()
                            );
                })
                .collect(Collectors.toList());

        return registrationRequests;
    }

    @Transactional
    public ChildDto addChildToTraining(Long id) {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new ChildException("Child with ID " + id + " not found.", HttpStatus.NOT_FOUND));

        Training requestedTraining = trainingRepository.findById(child.getRequestedTrainingId())
                .orElseThrow(() -> new TrainingException("Training with ID " + child.getRequestedTrainingId() + " not found.", HttpStatus.NOT_FOUND));

        if (!requestedTraining.addChild(child)) {
            throw new TrainingException("Child is already registered to this training.", HttpStatus.CONFLICT);
        }

        child.setTraining(requestedTraining);
        child.setRequestedTrainingId(null);

        childRepository.save(child);
        trainingRepository.save(requestedTraining);

        mailService.sendChildApprovedMail(child, requestedTraining);

        return childMapper.toDto(child);
    }

    @Transactional
    public ChildParentInfo updateChildPaidStatus(Long childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ChildException("Child with ID " + childId + " not found.", HttpStatus.NOT_FOUND));

        boolean currentPaid = Boolean.TRUE.equals(child.getPaid());
        boolean newPaidStatus = !currentPaid;

        child.setPaid(newPaidStatus);

        childRepository.save(child);

        return childMapper.childParentInfoToDto(child);
    }

    @Transactional
    public void removeChildFromTraining(Long childId, Long trainingId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ChildException("Child with ID " + childId + " not found.", HttpStatus.NOT_FOUND));
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new TrainingException("Training with ID " + trainingId + " not found. ", HttpStatus.NOT_FOUND));

        if (!training.removeChild(child)) {
            throw new TrainingException("Child is was not registered to this Training", HttpStatus.FORBIDDEN);
        }
        child.setTraining(null);

        childRepository.save(child);
        trainingRepository.save(training);
    }

    @Transactional
    public void moveChildToDifferentTraining(Long childId, Long trainingId) {
        Child child = childRepository.findById(childId)
          .orElseThrow(() -> new ChildException("Child with ID " + childId + " not found.", HttpStatus.NOT_FOUND));
        Training newTraining = trainingRepository.findById(trainingId)
          .orElseThrow(() -> new TrainingException("Training with ID " + trainingId + " not found. ", HttpStatus.NOT_FOUND));

        boolean childIsAssigned;
        Training oldTraining = child.getTraining();
        childIsAssigned = oldTraining != null;

        if (childIsAssigned) {
            oldTraining.removeChild(child);
            oldTraining.setCapacity(oldTraining.getCapacity() + 1);
        }

        newTraining.addChild(child);
        if (newTraining.getCapacity() > 0) {
          newTraining.setCapacity(newTraining.getCapacity() - 1);
        }

        child.setTraining(newTraining);
        childRepository.save(child);

        if (childIsAssigned) {
            trainingRepository.save(oldTraining);
        }

        trainingRepository.save(newTraining);
    }

    public ChildDto getChild(Long id) {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new ChildException("Child with ID " + id + " not found.", HttpStatus.NOT_FOUND));

        return childMapper.toDto(child);
    }

    public List<ChildDto> getChildren() {
        List<Child> children = childRepository.findByActiveTrue();

        return childMapper.toDtoList(children);
    }

    public List<ChildParentInfo> getChildrenParentInfo() {
        List<Child> children = childRepository.findByActiveTrue();

        return childMapper.childParentInfoToDtoList(children);
    }

    @Transactional
    public void deleteChild(Long id) {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new ChildException("Child with ID " + id + " not found.", HttpStatus.NOT_FOUND));

        if (child.getTraining() != null) {
            Training training = child.getTraining();
            training.removeChild(child);
            training.setCapacity(training.getCapacity() + 1);
        } else if (child.getRequestedTrainingId() != null) {
            trainingRepository.findById(child.getRequestedTrainingId())
                    .ifPresent(t -> t.setCapacity(t.getCapacity() + 1));
        }

        child.setTraining(null);
        child.setRequestedTrainingId(null);
        child.setActive(false);
    }

    @Transactional
    public void rejectChild(Long id) {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new ChildException("Child with ID " + id + " not found.", HttpStatus.NOT_FOUND));

        if (child.getRequestedTrainingId() != null) {
            Training training = trainingRepository.findById(child.getRequestedTrainingId())
                    .orElseThrow(() -> new TrainingException("Training with ID " + child.getRequestedTrainingId() + " not found.", HttpStatus.NOT_FOUND));

            training.setCapacity(training.getCapacity() + 1);
            trainingRepository.save(training);
            child.setRequestedTrainingId(null);
            childRepository.save(child);
        }
    }

    @Transactional
    public ChildDto updateChild(Long id, ChildDto childDto) {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new ChildException("Child with ID " + id + " not found.", HttpStatus.NOT_FOUND));

        childMapper.updateChildFromDto(childDto, child);

        childRepository.save(child);
        return childMapper.toDto(child);
    }

    @Transactional
    public ParentDto updateChildParent(Long id, ParentDto parentDto) {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new ChildException("Child with ID " + id + " not found.", HttpStatus.NOT_FOUND));
        Parent parent = child.getParent();

        parentMapper.updateParentFromDto(parentDto, parent);

        userRepository.save(parent);
        return parentMapper.toDto(parent);
    }
}
