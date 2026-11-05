package cz.cvut.fel.attendance.service.api;

import cz.cvut.fel.attendance.service.service.TrainingScheduleService;
import cz.fel.cvut.attendance.service.api.TrainingScheduleApi;
import cz.fel.cvut.attendance.service.model.TrainerSubstitutionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class TrainingScheduleApiImpl implements TrainingScheduleApi {

    private final TrainingScheduleService trainingScheduleService;

    @Override
    public ResponseEntity<TrainerSubstitutionDto> createTrainerSubstitution(TrainerSubstitutionDto trainerSubstitutionDto, Long id) {
        return ResponseEntity.ok(trainingScheduleService.createTrainerSubstitution(trainerSubstitutionDto, id));
    }

}
