package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.mappers.ChildMapper;
import cz.cvut.fel.attendance.service.mappers.TrainerMapper;
import cz.cvut.fel.attendance.service.mappers.TrainerSubstitutionMapper;
import cz.cvut.fel.attendance.service.mappers.TrainingMapper;
import cz.cvut.fel.attendance.service.mappers.TrainingUnitMapper;
import cz.cvut.fel.attendance.service.model.Child;
import cz.cvut.fel.attendance.service.model.School;
import cz.cvut.fel.attendance.service.model.Trainer;
import cz.cvut.fel.attendance.service.model.TrainerSubstitution;
import cz.cvut.fel.attendance.service.model.Training;
import cz.cvut.fel.attendance.service.model.TrainingUnit;
import cz.cvut.fel.attendance.service.repository.SchoolRepository;
import cz.cvut.fel.attendance.service.repository.TrainerRepository;
import cz.cvut.fel.attendance.service.repository.TrainerSubstitutionRepository;
import cz.cvut.fel.attendance.service.repository.TrainingRepository;
import cz.cvut.fel.attendance.service.repository.TrainingUnitRepository;
import cz.fel.cvut.attendance.service.exception.SchoolException;
import cz.fel.cvut.attendance.service.exception.TrainingException;
import cz.fel.cvut.attendance.service.exception.UserException;
import cz.fel.cvut.attendance.service.model.ChildDto;
import cz.fel.cvut.attendance.service.model.TrainerDto;
import cz.fel.cvut.attendance.service.model.TrainerSubstitutionDto;
import cz.fel.cvut.attendance.service.model.TrainingDto;
import cz.fel.cvut.attendance.service.model.TrainingUnitDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

import static cz.cvut.fel.attendance.service.utils.HelpMethods.getDayName;

@Service
@RequiredArgsConstructor
@Transactional
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final SchoolRepository schoolRepository;
    private final TrainerSubstitutionRepository trainerSubstitutionRepository;
    private final TrainerRepository trainerRepository;

    private final TrainerSubstitutionMapper trainerSubstitutionMapper;
    private final TrainingMapper trainingMapper;
    private final ChildMapper childMapper;
    private final TrainingUnitMapper trainingUnitMapper;
    private final TrainerMapper trainerMapper;

    private final MailService mailService;

    public TrainerSubstitutionDto createTrainerSubstitution(TrainerSubstitutionDto trainerSubstitutionDto, Long id) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new TrainingException("Training with ID " + id + " not found", HttpStatus.NOT_FOUND));
        Trainer originalTrainer = trainerRepository.findById(trainerSubstitutionDto.originalTrainerId())
                .orElseThrow(() -> new UserException("Trainer not found.", HttpStatus.NOT_FOUND));
        Trainer substituteTrainer = trainerRepository.findById(trainerSubstitutionDto.substituteTrainerId())
                .orElseThrow(() -> new UserException("Trainer not found.", HttpStatus.NOT_FOUND));

        TrainerSubstitution trainerSubstitution = new TrainerSubstitution(
                trainerSubstitutionDto.originalTrainerId(),
                trainerSubstitutionDto.substituteTrainerId(),
                trainerSubstitutionDto.date(),
                training);

        trainerSubstitution = trainerSubstitutionRepository.save(trainerSubstitution);

        sendEmail(originalTrainer, substituteTrainer, trainerSubstitution);

        return trainerSubstitutionMapper.toDto(trainerSubstitution);
    }

    public TrainingDto addTrainerToTraining(Long id, Long trainerId) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new TrainingException("Training with ID " + id + " not found", HttpStatus.NOT_FOUND));
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new UserException("Trainer not found.", HttpStatus.NOT_FOUND));

        training.getTrainers().add(trainer);
        trainer.getTrainings().add(training);

        return trainingMapper.toDto(training);
    }

    public List<TrainerDto> getTrainers(Long id) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new TrainingException("Training with ID " + id + " not found", HttpStatus.NOT_FOUND));

        return trainerMapper.toDtoList(training.getTrainers());
    }

    public List<LocalDate> getUpcomingDates(Long id) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new TrainingException("Training with ID " + id + " not found", HttpStatus.NOT_FOUND));
        return training.getUpcomingDates();
    }

    public TrainingDto createTraining(TrainingDto trainingDto, Long schoolId) {
        School school = schoolRepository.findById(schoolId).orElseThrow(() -> new SchoolException("School with ID " + schoolId + " not found", HttpStatus.NOT_FOUND));

        Training training = trainingMapper.toEntity(trainingDto);

        LocalDate now = LocalDate.now();
        LocalDate upcomingDate = now.with(TemporalAdjusters.next(training.getDayOfWeek()));

        for (int i=0 ; i <= 4; i++) {
            training.getUpcomingDates().add(upcomingDate);
            upcomingDate = upcomingDate.plusDays(7);
        }

        training.setSchool(school);
        school.addTraining(training);

        Training savedTraining = trainingRepository.save(training);

        return trainingMapper.toDto(savedTraining);
    }

    public TrainingDto getTraining(Long id) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new TrainingException("Training with ID " + id + " not found", HttpStatus.NOT_FOUND));

        return trainingMapper.toDto(training);
    }

    public List<TrainingDto> getTrainings() {
        List<Training> trainings = trainingRepository.findAll();

        return trainingMapper.toDtoList(trainings);
    }

    public TrainingUnitDto getCurrentTrainingUnit(Long id) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new TrainingException("Training with ID " + id + " not found", HttpStatus.NOT_FOUND));

        List<TrainingUnit> trainingUnits = training.getTrainingUnits();
        TrainingUnit currentTrainingUnit = trainingUnits.stream()
                .filter(t -> t.isCurrent())
                .findFirst()
                .orElseThrow(() -> new TrainingException("No active training unit found", HttpStatus.NOT_FOUND));

        return trainingUnitMapper.toDto(currentTrainingUnit);
    }

    public List<TrainingUnitDto> getPastTrainingUnits(Long id) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new TrainingException("Training with ID " + id + " not found", HttpStatus.NOT_FOUND));

        List<TrainingUnit> trainingUnits = training.getTrainingUnits();
        List<TrainingUnit> pastTrainingUnits = trainingUnits.stream()
                .filter(t -> !t.isCurrent())
                .toList();

        return trainingUnitMapper.toDtoList(pastTrainingUnits);
    }

    public List<TrainingUnitDto> getTrainingUnits(Long id) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new TrainingException("Training with ID " + id + " not found", HttpStatus.NOT_FOUND));

        return trainingUnitMapper.toDtoList(training.getTrainingUnits());
    }

    public void deleteTraining(Long id) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new TrainingException("Training with ID " + id + " not found", HttpStatus.NOT_FOUND));

        trainingRepository.delete(training);
    }

    public TrainingDto updateTraining(Long id, TrainingDto trainingDto) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new TrainingException("Training with ID " + id + " not found.",
                        HttpStatus.NOT_FOUND));

        trainingMapper.updateTrainingFromDto(trainingDto, training);

        trainingRepository.save(training);
        return trainingMapper.toDto(training);
    }

    public List<ChildDto> getChildren(Long id) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new TrainingException("Training with ID " + id + " not found.",
                        HttpStatus.NOT_FOUND));

        List<Child> children = training.getChildren();

        return childMapper.toDtoList(children);
    }

    public void sendEmail(Trainer originalTrainer, Trainer substituteTrainer, TrainerSubstitution trainerSubstitution) {
        Training training = trainerSubstitution.getTraining();

        List<String> emails = training.getChildren().stream()
                .map(child -> child.getParent().getEmail())
                .collect(Collectors.toList());
        emails.add(originalTrainer.getEmail());
        emails.add(substituteTrainer.getEmail());

        String date = trainerSubstitution.getDate().format(DateTimeFormatter.ofPattern("dd.MM."));

        String subjectParents = "Změna trenéra dne " + date;
        String textParents =
                "Krásný dobrý den,\n\n" +
                        "Dovolujeme si Vás informovat, na změnu trenéra dne " + date + "\n\n" +
                        "- Skupina: " + training.getName() + "\n" +
                        "- Místo: " + training.getSchool().getName() + "\n" +
                        "- Den: " + getDayName(training.getDayOfWeek()) + " " + date +"\n" +
                        "- Čas: " + training.getStartTime() + " - " + training.getEndTime() + "\n" +
                        "- Původní trenér: " + originalTrainer.getFirstName() + " " + originalTrainer.getLastName() + "\n" +
                        "- Nový trenér: " + substituteTrainer.getFirstName() + " " + substituteTrainer.getLastName() + "\n\n" +
                        "Děkujeme za pochopení.\n\nS pozdravem,\nTým Judo SG Plzeň";

        mailService.sendMail(emails, subjectParents, textParents);
    }
}
