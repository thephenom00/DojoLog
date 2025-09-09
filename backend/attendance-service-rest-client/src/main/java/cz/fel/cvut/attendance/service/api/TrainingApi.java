package cz.fel.cvut.attendance.service.api;

import cz.fel.cvut.attendance.service.model.ChildDto;
import cz.fel.cvut.attendance.service.model.TrainerDto;
import cz.fel.cvut.attendance.service.model.TrainerSubstitutionDto;
import cz.fel.cvut.attendance.service.model.TrainingDto;
import cz.fel.cvut.attendance.service.model.TrainingUnitDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/training")
public interface TrainingApi {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/create/{schoolId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<TrainingDto> createTraining(@RequestBody TrainingDto trainingDto, @PathVariable Long schoolId);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}")
    ResponseEntity<TrainingDto> getTraining(@PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/{id}/add-trainer/{trainerId}")
    ResponseEntity<TrainingDto> addTrainerToTraining(@PathVariable Long id, @PathVariable Long trainerId);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    ResponseEntity<List<TrainingDto>> getTrainings();

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/trainers")
    ResponseEntity<List<TrainerDto>> getTrainers(@PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value="/{id}/create-trainer-substitution", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<TrainerSubstitutionDto> createTrainerSubstitution(@RequestBody TrainerSubstitutionDto trainerSubstitutionDto, @PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}/training-unit/current")
    ResponseEntity<TrainingUnitDto> getCurrentTrainingUnit(@PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}/training-unit/past")
    ResponseEntity<List<TrainingUnitDto>> getPastTrainingUnits(@PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}/upcoming-dates")
    ResponseEntity<List<LocalDate>> getUpcomingDates(@PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}/training-unit")
    ResponseEntity<List<TrainingUnitDto>> getTrainingUnits(@PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> deleteTraining(@PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}")
    ResponseEntity<TrainingDto> updateTraining(@PathVariable Long id, @RequestBody TrainingDto trainingDto);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/get-children")
    ResponseEntity<List<ChildDto>> getChildren(@PathVariable Long id);

}
