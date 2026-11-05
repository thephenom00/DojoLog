package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.mappers.TrainerSubstitutionMapper;
import cz.cvut.fel.attendance.service.model.Trainer;
import cz.cvut.fel.attendance.service.model.TrainerAttendance;
import cz.cvut.fel.attendance.service.model.TrainerSubstitution;
import cz.cvut.fel.attendance.service.model.TrainingSchedule;
import cz.cvut.fel.attendance.service.model.TrainingUnit;
import cz.cvut.fel.attendance.service.repository.TrainerAttendanceRepository;
import cz.cvut.fel.attendance.service.repository.TrainerRepository;
import cz.cvut.fel.attendance.service.repository.TrainerSubstitutionRepository;
import cz.cvut.fel.attendance.service.repository.TrainingScheduleRepository;
import cz.cvut.fel.attendance.service.repository.UserRepository;
import cz.fel.cvut.attendance.service.exception.TrainingScheduleException;
import cz.fel.cvut.attendance.service.exception.UserException;
import cz.fel.cvut.attendance.service.model.TrainerSubstitutionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainingScheduleService {

    private final TrainerRepository trainerRepository;
    private final TrainerSubstitutionRepository trainerSubstitutionRepository;
    private final TrainingScheduleRepository trainingScheduleRepository;
    private final TrainerAttendanceRepository trainerAttendanceRepository;
    private final UserRepository userRepository;

    private final TrainerSubstitutionMapper trainerSubstitutionMapper;

    private final MailService mailService;

    @Transactional
    public TrainerSubstitutionDto createTrainerSubstitution(TrainerSubstitutionDto trainerSubstitutionDto, Long id) {
        TrainingSchedule trainingSchedule = trainingScheduleRepository.findById(id)
                .orElseThrow(() -> new TrainingScheduleException("Training Schedule with ID " + id + " not found", HttpStatus.NOT_FOUND));
        Trainer originalTrainer = trainerRepository.findById(trainerSubstitutionDto.originalTrainerId())
                .orElseThrow(() -> new UserException("Trainer not found.", HttpStatus.NOT_FOUND));
        Trainer substituteTrainer = trainerRepository.findById(trainerSubstitutionDto.substituteTrainerId())
                .orElseThrow(() -> new UserException("Trainer not found.", HttpStatus.NOT_FOUND));
        LocalDate date = trainerSubstitutionDto.date();

        TrainerSubstitution trainerSubstitution = new TrainerSubstitution(
                originalTrainer.getId(),
                substituteTrainer.getId(),
                date,
                trainingSchedule
        );

        trainingSchedule.getTrainerSubstitutions().add(trainerSubstitution);
        trainingScheduleRepository.save(trainingSchedule);

        trainerSubstitution = trainerSubstitutionRepository.save(trainerSubstitution);
        sendEmail(originalTrainer, substituteTrainer, trainingSchedule, date);

        TrainingUnit currentTrainingUnit = trainingScheduleRepository.findCurrentTrainingUnitById(id);

        if (currentTrainingUnit.getDate().equals(date)) {
            for (TrainerAttendance attendance : currentTrainingUnit.getTrainerAttendances()) {
                if (attendance.getTrainer() == originalTrainer) {
                    attendance.setTrainer(substituteTrainer);
                    originalTrainer.getTrainingAttendances().remove(attendance);
                    substituteTrainer.addTrainerAttendance(attendance);

                    trainerAttendanceRepository.save(attendance);
                    userRepository.save(substituteTrainer);
                    userRepository.save(originalTrainer);
                }
            }
        }

        return trainerSubstitutionMapper.toDto(trainerSubstitution);
    }

    public void sendEmail(Trainer originalTrainer, Trainer substituteTrainer, TrainingSchedule trainingSchedule, LocalDate date) {
        mailService.sendTrainerSubstitutionMail(originalTrainer, substituteTrainer, trainingSchedule, date);
    }
}
