package cz.fel.cvut.attendance.service.api;

import cz.fel.cvut.attendance.service.model.ChildAttendanceDto;
import cz.fel.cvut.attendance.service.model.TrainerAttendanceDto;
import cz.fel.cvut.attendance.service.model.TrainingDto;
import cz.fel.cvut.attendance.service.model.TrainingUnitDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@RequestMapping("/training-unit")
public interface TrainingUnitApi {

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}/description")
    ResponseEntity<TrainingUnitDto> updateDescription(@PathVariable Long id, @RequestBody String description);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    ResponseEntity<TrainingUnitDto> getTrainingUnit(@PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/child-attendance")
    ResponseEntity<List<ChildAttendanceDto>> getChildAttendances(@PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/trainer-attendance")
    ResponseEntity<List<TrainerAttendanceDto>> getTrainerAttendances(@PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/create-weekly-training-units")
    ResponseEntity<Void> createWeeklyTrainingUnits();


}
