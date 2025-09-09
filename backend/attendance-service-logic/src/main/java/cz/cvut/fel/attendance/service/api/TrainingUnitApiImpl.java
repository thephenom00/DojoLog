package cz.cvut.fel.attendance.service.api;

import cz.cvut.fel.attendance.service.service.TrainingUnitService;
import cz.fel.cvut.attendance.service.api.TrainingUnitApi;
import cz.fel.cvut.attendance.service.model.ChildAttendanceDto;
import cz.fel.cvut.attendance.service.model.TrainerAttendanceDto;
import cz.fel.cvut.attendance.service.model.TrainingUnitDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TrainingUnitApiImpl implements TrainingUnitApi {

    private final TrainingUnitService trainingUnitService;

    @Override
    public ResponseEntity<TrainingUnitDto> updateDescription(Long id, String description) {
        return ResponseEntity.ok(trainingUnitService.updateDescription(id, description));
    }

    @Override
    public ResponseEntity<TrainingUnitDto> getTrainingUnit(Long id) {
        return ResponseEntity.ok(trainingUnitService.getTrainingUnit(id));
    }

    @Override
    public ResponseEntity<List<ChildAttendanceDto>> getChildAttendances(Long id) {
        return ResponseEntity.ok(trainingUnitService.getChildAttendances(id));
    }

    @Override
    public ResponseEntity<List<TrainerAttendanceDto>> getTrainerAttendances(Long id) {
        return ResponseEntity.ok(trainingUnitService.getTrainerAttendances(id));
    }

    @Override
    public ResponseEntity<Void> createWeeklyTrainingUnits() {
        trainingUnitService.createWeeklyTrainingUnits();
        return ResponseEntity.noContent().build();
    }

}
