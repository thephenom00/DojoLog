package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.mappers.ChildMapper;
import cz.cvut.fel.attendance.service.mappers.ChildUpcomingTrainingUnitMapper;
import cz.cvut.fel.attendance.service.model.Child;
import cz.cvut.fel.attendance.service.model.Event;
import cz.cvut.fel.attendance.service.model.Parent;
import cz.cvut.fel.attendance.service.model.Training;
import cz.cvut.fel.attendance.service.model.TrainingUnit;
import cz.cvut.fel.attendance.service.repository.ChildRepository;
import cz.cvut.fel.attendance.service.repository.EventRepository;
import cz.cvut.fel.attendance.service.repository.TrainingRepository;
import cz.cvut.fel.attendance.service.repository.TrainingUnitRepository;
import cz.cvut.fel.attendance.service.repository.UserRepository;
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
@Transactional
public class ParentService {

    private final TrainingUnitRepository trainingUnitRepository;
    private final ChildRepository childRepository;
    private final TrainingRepository trainingRepository;

    private final ChildUpcomingTrainingUnitMapper childUpcomingTrainingUnitMapper;
    private final UserRepository userRepository;
    private final ChildMapper childMapper;
    private final EventRepository eventRepository;

    private final MailService mailService;

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

        List<Child> children = parent.getChildren();

        return childMapper.toDtoList(children);
    }

    public ChildDto createChild(String email, ChildDto childDto) {
        Parent parent = (Parent) userRepository.findByEmail(email)
                .orElseThrow(() -> new ParentException("User with ID: " + email + " is not found.", HttpStatus.NOT_FOUND));

        if (childRepository.existsByBirthNumber(childDto.birthNumber())) {
            throw new ChildException("CHILD_ALREADY_EXISTS_BY_BIRTH_NUMBER",
                    HttpStatus.CONFLICT);
        }

        if (childRepository.existsByFirstNameAndLastNameAndParent(childDto.firstName(), childDto.lastName(), parent)) {
            throw new ChildException("CHILD_ALREADY_EXISTS_BY_NAME",
                    HttpStatus.CONFLICT);
        }

        if (parent.getChildren().size() >= 5) {
            throw new ChildException("MAX_CHILDREN_REACHED",
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Child child = childMapper.toEntity(childDto);

        Training training = trainingRepository.findById(childDto.requestedTrainingId()).orElseThrow(()-> new TrainingException("Training with ID: " + childDto.requestedTrainingId() + " not found.", HttpStatus.NOT_FOUND));

        if ((training.getCapacity() - 1) < 0) {
            throw new TrainingException("Child cannot be added to training, because it has full capacity.", HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
        }

        training.setCapacity(training.getCapacity() - 1);
        parent.addChild(child);
        child.setParent(parent);

        sendMail(parent, childDto);

        trainingRepository.save(training);
        userRepository.save(parent);
        childRepository.save(child);
        return childMapper.toDto(child);
    }

    private void sendMail(Parent parent, ChildDto childDto) {
        String adminMessage = "Dítě " + childDto.firstName() + " " + childDto.lastName() +
                " bylo právě zaregistrováno rodičem " + parent.getFirstName() + " " + parent.getLastName() +
                " (e-mail: " + parent.getEmail() + ") \n\nProsím, zkontrolujte registraci a schvalte nebo zamítněte žádost.";

        String parentMessage  = "Vaše dítě " + childDto.firstName() + " " + childDto.lastName() +
                " bylo úspěšně zaregistrováno na trénink. Nyní prosím vyčkejte na schválení administrátorem. Jakmile bude žádost zpracována, obdržíte potvrzení e-mailem.\n\nDěkujeme, že využíváte naši platformu.\nTým Judo SG Plzeň";

        mailService.sendMail(List.of(adminEmail), "Nové dítě zaregistrováno na trénink", adminMessage);
        mailService.sendMail(List.of(parent.getEmail()), "Vaše žádost o registraci dítěte byla přijata", parentMessage);
    }
}
