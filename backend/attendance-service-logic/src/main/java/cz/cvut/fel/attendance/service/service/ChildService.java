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
import cz.fel.cvut.attendance.service.model.admin.RegistrationRequestDto;
import cz.fel.cvut.attendance.service.model.parent.ChildAttendanceDetailDto;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cz.cvut.fel.attendance.service.utils.HelpMethods.getDayName;

@Service
@RequiredArgsConstructor
@Transactional
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
                .filter(ChildTrainingAttendance::isPresent).collect(Collectors.toList());

        List<ChildAttendanceDetailDto> childAttendanceDetails = new ArrayList<>();

        for (ChildTrainingAttendance attendance : attendances) {
            childAttendanceDetails.add(new ChildAttendanceDetailDto(
                    attendance.getTrainingUnit().getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    attendance.getTrainingUnit().getTraining().getSchool().getName(),
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

                    return new RegistrationRequestDto(
                            child.getId(),
                            child.getFirstName(),
                            child.getLastName(),
                            child.getParent().getFirstName(),
                            child.getParent().getLastName(),
                            child.getParent().getEmail(),
                            child.getParent().getPhoneNumber(),
                            training.getSchool().getName(),
                            String.valueOf(training.getDayOfWeek()),
                            training.getName(),
                            String.format("%s - %s", training.getStartTime(), training.getEndTime()),
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

        sendEmail(child, requestedTraining);

        return childMapper.toDto(child);
    }

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

    public ChildDto getChild(Long id) {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new ChildException("Child with ID " + id + " not found.", HttpStatus.NOT_FOUND));

        return childMapper.toDto(child);
    }

    public List<ChildDto> getChildren() {
        List<Child> children = childRepository.findAll();

        return childMapper.toDtoList(children);
    }

    public void deleteChild(Long id) {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new ChildException("Child with ID " + id + " not found.", HttpStatus.NOT_FOUND));

        childRepository.delete(child);
    }

    public ChildDto updateChild(Long id, ChildDto childDto) {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new ChildException("Child with ID " + id + " not found.", HttpStatus.NOT_FOUND));

        childMapper.updateChildFromDto(childDto, child);

        childRepository.save(child);
        return childMapper.toDto(child);
    }

    public ParentDto updateChildParent(Long id, ParentDto parentDto) {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new ChildException("Child with ID " + id + " not found.", HttpStatus.NOT_FOUND));
        Parent parent = child.getParent();

        parentMapper.updateParentFromDto(parentDto, parent);

        userRepository.save(parent);
        return parentMapper.toDto(parent);
    }

    public void sendEmail(Child child, Training training) {
        String subject = "Potvrzení registrace na trénink juda";

        String message = "Vážený rodiči,\n\n" +
                "Vaše dítě " + child.getFirstName() + " " + child.getLastName() +
                " bylo úspěšně zaregistrováno na trénink.\n\n" +
                "Informace o tréninku:\n" +
                "- Škola: " + training.getSchool().getName() + "\n" +
                "- Skupina: " + training.getName() + "\n" +
                "- Den v týdnu: " + getDayName(training.getDayOfWeek()) + "\n" +
                "- Čas: " + training.getStartTime() + " - " + training.getEndTime() + "\n" +
                "Prosíme o úhradu " + training.getPrice() + "Kč na účet 1035146928/5500.\n" +
                "Do poznámky pro příjemce uveďte celé jméno dítěte a školu, kde bude dítě trénovat. např. (Karel Novotný - 33. ZŠ)\n\n" +
                "Děkujeme, že využíváte naši platformu.\n" +
                "Tým Judo SG Plzeň";

        mailService.sendMail(List.of(child.getParent().getEmail()), subject, message);
    }
}
