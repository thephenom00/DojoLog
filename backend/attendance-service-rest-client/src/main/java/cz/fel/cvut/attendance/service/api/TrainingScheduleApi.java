package cz.fel.cvut.attendance.service.api;

import cz.fel.cvut.attendance.service.model.TrainerSubstitutionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/training-schedule")
public interface TrainingScheduleApi {
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value="/{id}/create-trainer-substitution", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<TrainerSubstitutionDto> createTrainerSubstitution(@RequestBody TrainerSubstitutionDto trainerSubstitutionDto, @PathVariable Long id);

}
