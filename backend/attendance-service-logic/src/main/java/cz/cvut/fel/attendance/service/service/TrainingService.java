package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.mappers.ChildMapper;
import cz.cvut.fel.attendance.service.mappers.TrainerMapper;
import cz.cvut.fel.attendance.service.mappers.TrainingMapper;
import cz.cvut.fel.attendance.service.mappers.TrainingScheduleMapper;
import cz.cvut.fel.attendance.service.mappers.TrainingUnitMapper;
import cz.cvut.fel.attendance.service.model.*;
import cz.cvut.fel.attendance.service.repository.SchoolRepository;
import cz.cvut.fel.attendance.service.repository.TrainerRepository;
import cz.cvut.fel.attendance.service.repository.TrainingRepository;
import cz.cvut.fel.attendance.service.repository.TrainingScheduleRepository;
import cz.fel.cvut.attendance.service.exception.SchoolException;
import cz.fel.cvut.attendance.service.exception.TrainingException;
import cz.fel.cvut.attendance.service.exception.UserException;
import cz.fel.cvut.attendance.service.model.TrainerDto;
import cz.fel.cvut.attendance.service.model.TrainingDto;
import cz.fel.cvut.attendance.service.model.TrainingScheduleDto;
import cz.fel.cvut.attendance.service.model.TrainingUnitDto;
import cz.fel.cvut.attendance.service.model.admin.ChildParentInfo;
import cz.fel.cvut.attendance.service.model.admin.EmailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final SchoolRepository schoolRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingScheduleRepository trainingScheduleRepository;
    private final TrainingUnitService trainingUnitService;

    private final TrainingMapper trainingMapper;
    private final ChildMapper childMapper;
    private final TrainingUnitMapper trainingUnitMapper;
    private final TrainerMapper trainerMapper;
    private final TrainingScheduleMapper trainingScheduleMapper;

    private final MailService mailService;

    @Transactional
    public TrainingDto addTrainerToTraining(Long id, Long trainerId) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new TrainingException("Training with ID " + id + " not found", HttpStatus.NOT_FOUND));
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new UserException("Trainer not found.", HttpStatus.NOT_FOUND));

        training.getTrainers().add(trainer);
        trainer.getTrainings().add(training);

        trainingUnitService.createAttendanceForCurrentUnits(trainer, training);

        return trainingMapper.toDto(training);
    }

    @Transactional
    public TrainingDto removeTrainerFromTraining(Long id, Long trainerId) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new TrainingException("Training with ID " + id + " not found", HttpStatus.NOT_FOUND));
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new UserException("Trainer not found.", HttpStatus.NOT_FOUND));

        training.getTrainers().remove(trainer);
        trainer.getTrainings().remove(training);

        return trainingMapper.toDto(training);
    }

    public List<TrainerDto> getTrainers(Long id) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new TrainingException("Training with ID " + id + " not found", HttpStatus.NOT_FOUND));

        return trainerMapper.toDtoList(training.getTrainers());
    }

    @Transactional
    public TrainingDto createTraining(TrainingDto trainingDto, Long schoolId) {
        School school = schoolRepository.findById(schoolId).orElseThrow(() -> new SchoolException("School with ID " + schoolId + " not found", HttpStatus.NOT_FOUND));

        Training training = trainingMapper.toEntity(trainingDto);
        if (training.getInitialCapacity() <= 0 && training.getCapacity() > 0) {
            training.setInitialCapacity(training.getCapacity());
        } else if (training.getCapacity() <= 0 && training.getInitialCapacity() > 0) {
            training.setCapacity(training.getInitialCapacity());
        }
        training.setSchool(school);
        school.addTraining(training);
        training = trainingRepository.save(training);

        List<TrainingSchedule> trainingSchedules = trainingDto.trainingSchedules().stream()
                .map(trainingScheduleMapper::toEntity)
                .toList();

        for (TrainingSchedule schedule : trainingSchedules) {
            schedule.setTraining(training);
            trainingScheduleRepository.save(schedule);
            training.getTrainingSchedules().add(schedule);
        }

        return trainingMapper.toDto(training);
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

    public List<TrainingUnitDto> getCurrentTrainingUnit(Long id) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new TrainingException("Training with ID " + id + " not found", HttpStatus.NOT_FOUND));

        return training.getTrainingSchedules().stream()
                .flatMap(schedule -> schedule.getTrainingUnits().stream())
                .filter(TrainingUnit::isCurrent)
                .map(trainingUnitMapper::toDto)
                .toList();
    }

    public List<TrainingUnitDto> getPastTrainingUnits(Long id) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new TrainingException("Training with ID " + id + " not found", HttpStatus.NOT_FOUND));

        return training.getTrainingSchedules().stream()
                .flatMap(schedule -> schedule.getTrainingUnits().stream())
                .filter(unit -> !unit.isCurrent())
                .map(trainingUnitMapper::toDto)
                .toList();
    }

    public List<TrainingUnitDto> getTrainingUnits(Long id) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new TrainingException("Training with ID " + id + " not found", HttpStatus.NOT_FOUND));

        return training.getTrainingSchedules().stream()
                .flatMap(schedule -> schedule.getTrainingUnits().stream())
                .map(trainingUnitMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteTraining(Long id) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new TrainingException("Training with ID " + id + " not found", HttpStatus.NOT_FOUND));

        training.setActive(false);
        trainingRepository.save(training);
    }

    @Transactional
    public TrainingDto updateTraining(Long id, TrainingDto trainingDto) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new TrainingException("Training with ID " + id + " not found.",
                        HttpStatus.NOT_FOUND));
        int previousInitialCapacity = training.getInitialCapacity();
        trainingMapper.updateTrainingFromDto(trainingDto, training);

        int capacityDelta = training.getInitialCapacity() - previousInitialCapacity;
        if (capacityDelta != 0) {
            training.setCapacity(Math.max(0, training.getCapacity() + capacityDelta));
        }

        updateTrainingSchedules(training, trainingDto.trainingSchedules());

        trainingRepository.save(training);
        return trainingMapper.toDto(training);
    }

    private void updateTrainingSchedules(Training training, List<TrainingScheduleDto> incomingSchedules) {
        Map<Long, TrainingSchedule> existingById = trainingScheduleRepository.findByTrainingId(training.getId()).stream()
                .collect(Collectors.toMap(TrainingSchedule::getId, ts -> ts));

        Set<Long> incomingIds = incomingSchedules.stream()
                .map(TrainingScheduleDto::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<TrainingSchedule> toDelete = existingById.values().stream()
                .filter(ts -> !incomingIds.contains(ts.getId()))
                .toList();
        trainingScheduleRepository.deleteAll(toDelete);
        training.getTrainingSchedules().removeAll(toDelete);

        List<TrainingSchedule> toAdd = new ArrayList<>();
        for (TrainingScheduleDto dto : incomingSchedules) {
            TrainingSchedule existing = dto.id() != null ? existingById.get(dto.id()) : null;
            if (existing != null) {
                existing.setDayOfWeek(dto.dayOfWeek());
                existing.setStartTime(dto.startTime());
                existing.setEndTime(dto.endTime());
            } else {
                TrainingSchedule schedule = trainingScheduleMapper.toEntity(dto);
                schedule.setTraining(training);
                toAdd.add(schedule);
            }
        }
        trainingScheduleRepository.saveAll(toAdd);
        training.getTrainingSchedules().addAll(toAdd);
    }

    public List<ChildParentInfo> getChildren(Long id) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new TrainingException("Training with ID " + id + " not found.",
                        HttpStatus.NOT_FOUND));

        List<Child> children = training.getChildren().stream()
                .filter(Child::isActive)
                .toList();

        return childMapper.childParentInfoToDtoList(children);
    }

    public EmailDto sendEmail(Long id, EmailDto emailDto) {
      Training training = trainingRepository.findById(id)
        .orElseThrow(() -> new TrainingException("Training with ID " + id + " not found.",
          HttpStatus.NOT_FOUND));

      List<String> parents = training.getChildren().stream()
        .map(c -> c.getParent().getEmail())
        .distinct()
        .collect(Collectors.toList());

      mailService.sendCustomTrainingMail(parents, emailDto.subject(), emailDto.message());

      return emailDto;
    }

}
